/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.alert.web.provider.blackduck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.database.provider.blackduck.GlobalBlackDuckConfigEntity;
import com.synopsys.integration.alert.database.provider.blackduck.GlobalBlackDuckRepository;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProperties;
import com.synopsys.integration.alert.provider.blackduck.descriptor.BlackDuckTypeConverter;
import com.synopsys.integration.alert.web.exception.AlertFieldException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.configuration.HubServerConfig;
import com.synopsys.integration.hub.configuration.HubServerConfigBuilder;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.validator.AbstractValidator;
import com.synopsys.integration.validator.FieldEnum;
import com.synopsys.integration.validator.ValidationResult;
import com.synopsys.integration.validator.ValidationResults;
import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.web.actions.ConfigActions;

@Component
public class GlobalBlackDuckConfigActions extends ConfigActions<GlobalBlackDuckConfigEntity, GlobalBlackDuckConfig, GlobalBlackDuckRepository> {
    private final Logger logger = LoggerFactory.getLogger(GlobalBlackDuckConfigActions.class);
    private final BlackDuckProperties blackDuckProperties;
    private final AlertProperties alertProperties;

    @Autowired

    public GlobalBlackDuckConfigActions(final GlobalBlackDuckRepository globalRepository, final BlackDuckTypeConverter blackDuckTypeConverter, final BlackDuckProperties blackDuckProperties,
            final AlertProperties alertProperties) {
        super(globalRepository, blackDuckTypeConverter);
        this.blackDuckProperties = blackDuckProperties;
        this.alertProperties = alertProperties;
    }

    @Override
    public List<GlobalBlackDuckConfig> getConfig(final Long id) throws AlertException {
        if (id != null) {
            final Optional<GlobalBlackDuckConfigEntity> foundEntity = getRepository().findById(id);
            if (foundEntity.isPresent()) {

                GlobalBlackDuckConfig restModel = (GlobalBlackDuckConfig) getDatabaseContentConverter().populateConfigFromEntity(foundEntity.get());
                restModel = updateModelFromProperties(restModel);

                if (restModel != null) {
                    final GlobalBlackDuckConfig maskedRestModel = maskRestModel(restModel);
                    return Arrays.asList(maskedRestModel);
                }
            }
            return Collections.emptyList();
        }
        final List<GlobalBlackDuckConfigEntity> databaseEntities = getRepository().findAll();
        List<GlobalBlackDuckConfig> restModels = new ArrayList<>(databaseEntities.size());
        if (databaseEntities != null && !databaseEntities.isEmpty()) {
            for (final GlobalBlackDuckConfigEntity entity : databaseEntities) {
                restModels.add((GlobalBlackDuckConfig) getDatabaseContentConverter().populateConfigFromEntity(entity));
            }
        } else {
            restModels.add(new GlobalBlackDuckConfig());
        }
        restModels = updateModelsFromProperties(restModels);
        restModels = maskRestModels(restModels);
        return restModels;
    }

    public GlobalBlackDuckConfig updateModelFromProperties(final GlobalBlackDuckConfig restModel) {
        restModel.setBlackDuckUrl(blackDuckProperties.getBlackDuckUrl().orElse(null));
        final Boolean trustCertificate = alertProperties.getAlertTrustCertificate().orElse(null);
        if (null != trustCertificate) {
            restModel.setBlackDuckAlwaysTrustCertificate(String.valueOf(trustCertificate));
        }
        restModel.setBlackDuckProxyHost(alertProperties.getAlertProxyHost().orElse(null));
        restModel.setBlackDuckProxyPort(alertProperties.getAlertProxyPort().orElse(null));
        restModel.setBlackDuckProxyUsername(alertProperties.getAlertProxyUsername().orElse(null));
        // Do not send passwords going to the UI
        final boolean proxyPasswordIsSet = StringUtils.isNotBlank(alertProperties.getAlertProxyPassword().orElse(null));
        restModel.setBlackDuckProxyPasswordIsSet(proxyPasswordIsSet);
        return restModel;
    }

    public List<GlobalBlackDuckConfig> updateModelsFromProperties(final List<GlobalBlackDuckConfig> restModels) {
        final List<GlobalBlackDuckConfig> updatedRestModels = new ArrayList<>();
        for (final GlobalBlackDuckConfig restModel : restModels) {
            updatedRestModels.add(updateModelFromProperties(restModel));
        }
        return restModels;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T updateNewConfigWithSavedConfig(final T newConfig, final GlobalBlackDuckConfigEntity savedConfig) throws AlertException {
        T updatedConfig = super.updateNewConfigWithSavedConfig(newConfig, savedConfig);
        if (updatedConfig instanceof GlobalBlackDuckConfig) {
            updatedConfig = (T) updateModelFromProperties((GlobalBlackDuckConfig) updatedConfig);
        }
        return updatedConfig;
    }

    @Override
    public String validateConfig(final GlobalBlackDuckConfig restModel) throws AlertFieldException {
        final Map<String, String> fieldErrors = new HashMap<>();
        if (StringUtils.isNotBlank(restModel.getBlackDuckTimeout()) && !StringUtils.isNumeric(restModel.getBlackDuckTimeout())) {
            fieldErrors.put("blackDuckTimeout", "Not an Integer.");
        }
        if (StringUtils.isNotBlank(restModel.getBlackDuckApiKey())) {
            if (restModel.getBlackDuckApiKey().length() < 64) {
                fieldErrors.put("blackDuckApiKey", "Not enough characters to be a Hub API Key.");
            } else if (restModel.getBlackDuckApiKey().length() > 256) {
                fieldErrors.put("blackDuckApiKey", "Too many characters to be a Hub API Key.");
            }
        }
        if (!fieldErrors.isEmpty()) {
            throw new AlertFieldException(fieldErrors);
        }
        return "Valid";
    }

    @Override
    public String channelTestConfig(final GlobalBlackDuckConfig restModel) throws IntegrationException {
        final Slf4jIntLogger intLogger = new Slf4jIntLogger(logger);

        final String apiToken = restModel.getBlackDuckApiKey();

        final HubServerConfigBuilder blackDuckServerConfigBuilder = blackDuckProperties.createServerConfigBuilderWithoutAuthentication(intLogger, NumberUtils.toInt(restModel.getBlackDuckTimeout()));
        blackDuckServerConfigBuilder.setApiToken(apiToken);

        validateBlackDuckConfiguration(blackDuckServerConfigBuilder);
        try (final RestConnection restConnection = createRestConnection(blackDuckServerConfigBuilder)) {
            restConnection.connect();
        } catch (final IOException ex) {
            logger.error("Failed to close rest connection", ex);
        }
        return "Successfully connected to the Hub.";
    }

    public void validateBlackDuckConfiguration(final HubServerConfigBuilder blackDuckServerConfigBuilder) throws AlertFieldException {
        final AbstractValidator validator = blackDuckServerConfigBuilder.createValidator();
        final ValidationResults results = validator.assertValid();
        if (!results.getResultMap().isEmpty()) {
            final Map<String, String> fieldErrors = new HashMap<>();
            for (final Entry<FieldEnum, Set<ValidationResult>> result : results.getResultMap().entrySet()) {
                final Set<ValidationResult> validationResult = result.getValue();
                final List<String> errors = new ArrayList<>();
                for (final ValidationResult currentValidationResult : validationResult) {
                    errors.add(currentValidationResult.getMessage());
                }

                fieldErrors.put(result.getKey().getKey(), StringUtils.join(errors, " , "));
            }
            throw new AlertFieldException("There were issues with the configuration.", fieldErrors);
        }
    }

    public RestConnection createRestConnection(final HubServerConfigBuilder blackDuckServerConfigBuilder) throws IntegrationException {
        final HubServerConfig blackDuckServerConfig = blackDuckServerConfigBuilder.build();
        return blackDuckServerConfig.createRestConnection(blackDuckServerConfigBuilder.getLogger());
    }
}