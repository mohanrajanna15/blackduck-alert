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
package com.synopsys.integration.alert.channel.jira.cloud.descriptor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.config.field.ConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.PasswordConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.TextInputConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.URLInputConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.EndpointButtonField;
import com.synopsys.integration.alert.common.descriptor.config.field.validators.EncryptionSettingsValidator;
import com.synopsys.integration.alert.common.descriptor.config.ui.UIConfig;

@Component
public class JiraCloudGlobalUIConfig extends UIConfig {
    public static final String LABEL_URL = "Url";
    public static final String LABEL_ADMIN_EMAIL_ADDRESS = "Admin Email Address";
    public static final String LABEL_ADMIN_API_TOKEN = "Admin API Token";
    public static final String LABEL_CONFIGURE_PLUGIN = "Configure Jira Cloud plugin";

    public static final String DESCRIPTION_URL = "The URL of the Jira Cloud server.";
    public static final String DESCRIPTION_ADMIN_USER_NAME = "The email address of the admin used to log into the Jira Cloud server that has generated the API token.";
    public static final String DESCRIPTION_ADMIN_API_TOKEN = "The admin API token used to send API requests to the Jira Cloud server.";
    public static final String DESCRIPTION_CONFIGURE_PLUGIN = "Installs a required plugin on the Jira Cloud server.";

    public static final String BUTTON_LABEL_PLUGIN_CONFIGURATION = "Install Plugin Remotely";

    private final EncryptionSettingsValidator encryptionValidator;

    @Autowired
    public JiraCloudGlobalUIConfig(EncryptionSettingsValidator encryptionValidator) {
        super(JiraCloudDescriptor.JIRA_LABEL, JiraCloudDescriptor.JIRA_DESCRIPTION, JiraCloudDescriptor.JIRA_URL);
        this.encryptionValidator = encryptionValidator;
    }

    @Override
    public List<ConfigField> createFields() {
        ConfigField jiraUrl = new URLInputConfigField(JiraCloudDescriptor.KEY_JIRA_URL, LABEL_URL, DESCRIPTION_URL).applyRequired(true);
        ConfigField jiraUserName = new TextInputConfigField(JiraCloudDescriptor.KEY_JIRA_ADMIN_EMAIL_ADDRESS, LABEL_ADMIN_EMAIL_ADDRESS, DESCRIPTION_ADMIN_USER_NAME).applyRequired(true);
        ConfigField jiraAccessToken = new PasswordConfigField(JiraCloudDescriptor.KEY_JIRA_ADMIN_API_TOKEN, LABEL_ADMIN_API_TOKEN, DESCRIPTION_ADMIN_API_TOKEN, encryptionValidator).applyRequired(true);
        ConfigField jiraConfigurePlugin = new EndpointButtonField(JiraCloudDescriptor.KEY_JIRA_CONFIGURE_PLUGIN, LABEL_CONFIGURE_PLUGIN, DESCRIPTION_CONFIGURE_PLUGIN, BUTTON_LABEL_PLUGIN_CONFIGURATION)
                                              .applyRequestedDataFieldKey(JiraCloudDescriptor.KEY_JIRA_URL)
                                              .applyRequestedDataFieldKey(JiraCloudDescriptor.KEY_JIRA_ADMIN_EMAIL_ADDRESS)
                                              .applyRequestedDataFieldKey(JiraCloudDescriptor.KEY_JIRA_ADMIN_API_TOKEN);

        return List.of(jiraUrl, jiraUserName, jiraAccessToken, jiraConfigurePlugin);
    }

}
