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
package com.synopsys.integration.alert.provider.blackduck.descriptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.alert.common.descriptor.config.DescriptorConfig;
import com.synopsys.integration.alert.common.descriptor.config.UIComponent;
import com.synopsys.integration.alert.database.entity.DatabaseEntity;
import com.synopsys.integration.alert.web.model.Config;

@Component
public class BlackDuckProviderDescriptorConfig extends DescriptorConfig {

    @Autowired
    public BlackDuckProviderDescriptorConfig(final BlackDuckTypeConverter databaseContentConverter, final BlackDuckRepositoryAccessor repositoryAccessor, final BlackDuckProviderStartupComponent startupComponent) {
        super(databaseContentConverter, repositoryAccessor, startupComponent);
    }

    @Override
    public UIComponent getUiComponent() {
        return new UIComponent("Black Duck", "blackduck", "laptop", "BlackDuckConfiguration");
    }

    @Override
    public void validateConfig(final Config restModel, final Map<String, String> fieldErrors) {

    }

    @Override
    public void testConfig(final DatabaseEntity entity) throws IntegrationException {

    }

}