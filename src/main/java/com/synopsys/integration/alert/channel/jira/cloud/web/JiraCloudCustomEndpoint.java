/**
 * blackduck-alert
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.alert.channel.jira.cloud.web;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.synopsys.integration.alert.channel.jira.cloud.JiraCloudChannelKey;
import com.synopsys.integration.alert.channel.jira.cloud.JiraCloudProperties;
import com.synopsys.integration.alert.channel.jira.cloud.descriptor.JiraCloudDescriptor;
import com.synopsys.integration.alert.channel.jira.common.JiraConstants;
import com.synopsys.integration.alert.common.action.CustomEndpointManager;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.ButtonCustomEndpoint;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.rest.HttpServletContentWrapper;
import com.synopsys.integration.alert.common.rest.ResponseFactory;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.FieldValueModel;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.jira.common.cloud.service.JiraCloudServiceFactory;
import com.synopsys.integration.jira.common.rest.service.PluginManagerService;
import com.synopsys.integration.rest.response.Response;

@Component
public class JiraCloudCustomEndpoint extends ButtonCustomEndpoint {
    private final Logger logger = LoggerFactory.getLogger(JiraCloudCustomEndpoint.class);

    private final JiraCloudChannelKey jiraChannelKey;
    private final ResponseFactory responseFactory;
    private final ConfigurationAccessor configurationAccessor;
    private final Gson gson;

    @Autowired
    public JiraCloudCustomEndpoint(JiraCloudChannelKey jiraChannelKey, CustomEndpointManager customEndpointManager, ResponseFactory responseFactory, ConfigurationAccessor configurationAccessor, Gson gson) throws AlertException {
        super(JiraCloudDescriptor.KEY_JIRA_CONFIGURE_PLUGIN, customEndpointManager);
        this.jiraChannelKey = jiraChannelKey;
        this.responseFactory = responseFactory;
        this.configurationAccessor = configurationAccessor;
        this.gson = gson;
    }

    @Override
    public ResponseEntity<String> createResponse(FieldModel fieldModel, HttpServletContentWrapper ignoredServletContent) {
        JiraCloudProperties jiraProperties = createJiraProperties(fieldModel);
        try {
            JiraCloudServiceFactory jiraServicesCloudFactory = jiraProperties.createJiraServicesCloudFactory(logger, gson);
            PluginManagerService jiraAppService = jiraServicesCloudFactory.createPluginManagerService();
            String username = jiraProperties.getUsername();
            String accessToken = jiraProperties.getAccessToken();
            Response response = jiraAppService.installMarketplaceCloudApp(JiraConstants.JIRA_APP_KEY, username, accessToken);
            if (BooleanUtils.isTrue(response.isStatusCodeError())) {
                return responseFactory.createBadRequestResponse("", "The Jira Cloud server responded with error code: " + response.getStatusCode());
            }
            boolean jiraPluginInstalled = isJiraPluginInstalled(jiraAppService, accessToken, username, JiraConstants.JIRA_APP_KEY);
            if (!jiraPluginInstalled) {
                return responseFactory.createNotFoundResponse("Was not able to confirm Jira Cloud successfully installed the Jira Cloud plugin. Please verify the installation on you Jira Cloud server.");
            }
            return responseFactory.createOkResponse("", "Successfully installed the Alert plugin on Jira Cloud");
        } catch (IntegrationException e) {
            logger.error("There was an issue connecting to Jira Cloud", e);
            return responseFactory.createBadRequestResponse("", "The following error occurred when connecting to Jira Cloud: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted while validating jira install.", e);
            Thread.currentThread().interrupt();
            return responseFactory.createInternalServerErrorResponse("", "Thread was interrupted while validating Jira plugin installation: " + e.getMessage());
        }
    }

    private JiraCloudProperties createJiraProperties(FieldModel fieldModel) {
        String url = fieldModel.getFieldValue(JiraCloudDescriptor.KEY_JIRA_URL).orElse("");
        String username = fieldModel.getFieldValue(JiraCloudDescriptor.KEY_JIRA_ADMIN_EMAIL_ADDRESS).orElse("");
        String accessToken = fieldModel.getFieldValueModel(JiraCloudDescriptor.KEY_JIRA_ADMIN_API_TOKEN)
                                 .map(this::getAppropriateAccessToken)
                                 .orElse("");

        return new JiraCloudProperties(url, accessToken, username);
    }

    private String getAppropriateAccessToken(FieldValueModel fieldAccessToken) {
        String accessToken = fieldAccessToken.getValue().orElse("");
        boolean accessTokenSet = fieldAccessToken.isSet();
        if (StringUtils.isBlank(accessToken) && accessTokenSet) {
            try {
                return configurationAccessor.getConfigurationsByDescriptorKeyAndContext(jiraChannelKey, ConfigContextEnum.GLOBAL)
                           .stream()
                           .findFirst()
                           .flatMap(configurationModel -> configurationModel.getField(JiraCloudDescriptor.KEY_JIRA_ADMIN_API_TOKEN))
                           .flatMap(ConfigurationFieldModel::getFieldValue)
                           .orElse("");

            } catch (AlertDatabaseConstraintException e) {
                logger.error("Unable to retrieve existing Jira configuration.");
            }
        }

        return accessToken;
    }

    private boolean isJiraPluginInstalled(PluginManagerService jiraAppService, String accessToken, String username, String appKey) throws IntegrationException, InterruptedException {
        long maxTimeForChecks = 5L;
        long checkAgain = 1L;
        while (checkAgain <= maxTimeForChecks) {
            boolean foundPlugin = jiraAppService.isAppInstalled(username, accessToken, appKey);
            if (foundPlugin) {
                return true;
            }

            TimeUnit.SECONDS.sleep(checkAgain);
            checkAgain++;
        }

        return false;
    }

}
