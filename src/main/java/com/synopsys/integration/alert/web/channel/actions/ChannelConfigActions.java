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
package com.synopsys.integration.alert.web.channel.actions;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.alert.common.ContentConverter;
import com.synopsys.integration.alert.common.descriptor.ChannelDescriptor;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.database.entity.DatabaseEntity;
import com.synopsys.integration.alert.web.exception.AlertFieldException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.alert.web.model.Config;

public abstract class ChannelConfigActions<R extends Config> {
    private final ContentConverter contentConverter;

    public ChannelConfigActions(final ContentConverter contentConverter) {
        this.contentConverter = contentConverter;
    }

    public boolean doesConfigExist(final String id, final ChannelDescriptor descriptor) {
        return doesConfigExist(contentConverter.getLongValue(id), descriptor);
    }

    public abstract boolean doesConfigExist(final Long id, ChannelDescriptor descriptor);

    public abstract List<R> getConfig(final Long id, ChannelDescriptor descriptor) throws AlertException;

    public void deleteConfig(final String id, final ChannelDescriptor descriptor) {
        deleteConfig(contentConverter.getLongValue(id), descriptor);
    }

    public abstract void deleteConfig(final Long id, ChannelDescriptor descriptor);

    public abstract DatabaseEntity saveConfig(final R restModel, ChannelDescriptor descriptor) throws AlertException;

    public abstract String validateConfig(final R restModel, ChannelDescriptor descriptor) throws AlertFieldException;

    public abstract String testConfig(final R restModel, final ChannelDescriptor descriptor) throws IntegrationException;

    public abstract DatabaseEntity saveNewConfigUpdateFromSavedConfig(final R restModel, ChannelDescriptor descriptor) throws AlertException;

    public Boolean isBoolean(final String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        final String trimmedValue = value.trim();
        return trimmedValue.equalsIgnoreCase("false") || trimmedValue.equalsIgnoreCase("true");
    }

    public ContentConverter getContentConverter() {
        return contentConverter;
    }

}