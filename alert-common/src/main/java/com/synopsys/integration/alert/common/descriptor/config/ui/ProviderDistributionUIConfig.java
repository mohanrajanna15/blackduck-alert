/**
 * alert-common
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
package com.synopsys.integration.alert.common.descriptor.config.ui;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import com.synopsys.integration.alert.common.descriptor.ProviderDescriptor;
import com.synopsys.integration.alert.common.descriptor.config.field.ConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.HideCheckboxConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.LabelValueSelectOption;
import com.synopsys.integration.alert.common.descriptor.config.field.SelectConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.TextInputConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.EndpointSelectField;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.table.EndpointTableSelectField;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.table.TableSelectColumn;
import com.synopsys.integration.alert.common.descriptor.config.field.validators.ValidationResult;
import com.synopsys.integration.alert.common.enumeration.ProcessingType;
import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.provider.ProviderContent;
import com.synopsys.integration.alert.common.provider.ProviderNotificationType;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.FieldValueModel;

public abstract class ProviderDistributionUIConfig extends UIConfig {
    public static final String KEY_NOTIFICATION_TYPES = "provider.distribution.notification.types";
    public static final String KEY_PROCESSING_TYPE = "provider.distribution.processing.type";
    public static final String KEY_FILTER_BY_PROJECT = ChannelDistributionUIConfig.KEY_COMMON_CHANNEL_PREFIX + "filter.by.project";
    public static final String KEY_PROJECT_NAME_PATTERN = ChannelDistributionUIConfig.KEY_COMMON_CHANNEL_PREFIX + "project.name.pattern";
    public static final String KEY_CONFIGURED_PROJECT = ChannelDistributionUIConfig.KEY_COMMON_CHANNEL_PREFIX + "configured.project";

    protected static final String LABEL_FILTER_BY_PROJECT = "Filter By Project";
    protected static final String LABEL_PROJECT_NAME_PATTERN = "Project Name Pattern";
    protected static final String LABEL_PROJECTS = "Projects";
    protected static final String DESCRIPTION_FILTER_BY_PROJECT = "If selected, only notifications from the selected Projects table will be processed. Otherwise notifications from all Projects are processed.";
    protected static final String DESCRIPTION_PROJECT_NAME_PATTERN = "The regular expression to use to determine what Projects to include. These are in addition to the Projects selected in the table.";
    private static final String DESCRIPTION_PROJECTS = "Select a project or projects that will be used to retrieve notifications from your provider.";

    private static final String LABEL_NOTIFICATION_TYPES = "Notification Types";
    private static final String LABEL_PROCESSING = "Processing";
    private static final String DESCRIPTION_NOTIFICATION_TYPES = "Select one or more of the notification types. Only these notification types will be included for this distribution job.";
    private static final String DESCRIPTION_PROCESSING = "Select the way messages will be processed: ";

    private final ProviderContent providerContent;
    private final ConfigurationAccessor configurationAccessor;

    public ProviderDistributionUIConfig(String label, String urlName, ProviderContent providerContent, ConfigurationAccessor configurationAccessor) {
        super(label, label + " provider distribution setup.", urlName);
        this.providerContent = providerContent;
        this.configurationAccessor = configurationAccessor;
    }

    @Override
    public List<ConfigField> createFields() {
        ConfigField providerConfigNameField = new EndpointSelectField(ProviderDescriptor.KEY_PROVIDER_CONFIG_NAME, ProviderDescriptor.LABEL_PROVIDER_CONFIG_NAME, ProviderDescriptor.DESCRIPTION_PROVIDER_CONFIG_NAME)
                                                  .applyClearable(false)
                                                  .applyValidationFunctions(this::validateConfigExists)
                                                  .applyRequired(true);
        List<LabelValueSelectOption> notificationTypeOptions = providerContent.getContentTypes()
                                                                   .stream()
                                                                   .map(this::convertToLabelValueOption)
                                                                   .sorted()
                                                                   .collect(Collectors.toList());
        ConfigField notificationTypesField = new SelectConfigField(KEY_NOTIFICATION_TYPES, LABEL_NOTIFICATION_TYPES, DESCRIPTION_NOTIFICATION_TYPES, notificationTypeOptions)
                                                 .applyMultiSelect(true)
                                                 .applyRequired(true);

        // TODO the processing type field should be moved to the ChannelDistributionUIConfig
        // TODO add validation for this field, should add a warning if the User has chosen the Summary processing type with an issue tracker channel
        ConfigField processingField = new EndpointSelectField(KEY_PROCESSING_TYPE, LABEL_PROCESSING, DESCRIPTION_PROCESSING + createProcessingDescription())
                                          .applyRequestedDataFieldKey(ChannelDistributionUIConfig.KEY_CHANNEL_NAME)
                                          .applyRequired(true);

        ConfigField filterByProject = new HideCheckboxConfigField(KEY_FILTER_BY_PROJECT, LABEL_FILTER_BY_PROJECT, DESCRIPTION_FILTER_BY_PROJECT)
                                          .applyRelatedHiddenFieldKeys(KEY_PROJECT_NAME_PATTERN, KEY_CONFIGURED_PROJECT);
        ConfigField projectNamePattern = new TextInputConfigField(KEY_PROJECT_NAME_PATTERN, LABEL_PROJECT_NAME_PATTERN, DESCRIPTION_PROJECT_NAME_PATTERN)
                                             .applyValidationFunctions(this::validateProjectNamePattern);
        ConfigField configuredProject = new EndpointTableSelectField(KEY_CONFIGURED_PROJECT, LABEL_PROJECTS, DESCRIPTION_PROJECTS)
                                            .applyPaged(true)
                                            .applySearchable(true)
                                            .applyColumn(new TableSelectColumn("name", "Project Name", true, true))
                                            .applyColumn(new TableSelectColumn("description", "Project Description", false, false))
                                            .applyRequestedDataFieldKey(ChannelDistributionUIConfig.KEY_PROVIDER_NAME)
                                            .applyRequestedDataFieldKey(ProviderDescriptor.KEY_PROVIDER_CONFIG_NAME)
                                            .applyValidationFunctions(this::validateConfiguredProject);

        List<ConfigField> configFields = List.of(providerConfigNameField, notificationTypesField, processingField, filterByProject, projectNamePattern, configuredProject);
        List<ConfigField> providerDistributionFields = createProviderDistributionFields();
        return Stream.concat(configFields.stream(), providerDistributionFields.stream()).collect(Collectors.toList());
    }

    public abstract List<ConfigField> createProviderDistributionFields();

    private LabelValueSelectOption convertToLabelValueOption(ProviderNotificationType providerContentType) {
        String notificationType = providerContentType.getNotificationType();
        String notificationTypeLabel = WordUtils.capitalizeFully(notificationType.replace("_", " "));
        return new LabelValueSelectOption(notificationTypeLabel, notificationType);
    }

    private String createProcessingDescription() {
        StringBuilder formatDescription = new StringBuilder();
        for (ProcessingType format : providerContent.getSupportedProcessingTypes()) {
            String label = format.getLabel();
            String description = format.getDescription();

            formatDescription.append(label);
            formatDescription.append(": ");
            formatDescription.append(description);
            formatDescription.append(" ");
        }

        return formatDescription.toString();
    }

    private ValidationResult validateConfigExists(FieldValueModel fieldToValidate, FieldModel fieldModel) {
        Optional<String> providerConfigName = fieldToValidate.getValue();
        Optional<ConfigurationModel> configModel = providerConfigName.flatMap(this::readConfiguration);
        if (!configModel.isPresent()) {
            return ValidationResult.errors("Provider configuration missing.");
        }
        return ValidationResult.success();
    }

    private Optional<ConfigurationModel> readConfiguration(String configName) {
        try {
            return configurationAccessor.getProviderConfigurationByName(configName);
        } catch (AlertDatabaseConstraintException ex) {
            return Optional.empty();
        }
    }

    private ValidationResult validateProjectNamePattern(FieldValueModel fieldToValidate, FieldModel fieldModel) {
        String projectNamePattern = fieldToValidate.getValue().orElse(null);
        if (StringUtils.isNotBlank(projectNamePattern)) {
            try {
                Pattern.compile(projectNamePattern);
            } catch (PatternSyntaxException e) {
                return ValidationResult.errors("Project name pattern is not a regular expression. " + e.getMessage());
            }
        }
        return ValidationResult.success();
    }

    private ValidationResult validateConfiguredProject(FieldValueModel fieldToValidate, FieldModel fieldModel) {
        Collection<String> configuredProjects = Optional.ofNullable(fieldToValidate.getValues()).orElse(List.of());
        boolean filterByProject = fieldModel.getFieldValueModel(KEY_FILTER_BY_PROJECT).flatMap(FieldValueModel::getValue).map(Boolean::parseBoolean).orElse(false);
        String projectNamePattern = fieldModel.getFieldValueModel(KEY_PROJECT_NAME_PATTERN).flatMap(FieldValueModel::getValue).orElse(null);
        boolean missingProject = configuredProjects.isEmpty() && StringUtils.isBlank(projectNamePattern);
        if (filterByProject && missingProject) {
            return ValidationResult.errors("You must select at least one project.");
        }
        return ValidationResult.success();
    }

}
