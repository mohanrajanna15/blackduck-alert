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
package com.synopsys.integration.alert.web.api.metadata;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.common.descriptor.config.ui.DescriptorMetadata;
import com.synopsys.integration.alert.web.api.metadata.model.DescriptorsResponseModel;

@RestController
@RequestMapping(DescriptorController.BASE_PATH)
public class DescriptorController {
    public static final String BASE_PATH = MetadataControllerConstants.METADATA_BASE_PATH + "/descriptors";

    private final DescriptorMetadataActions descriptorMetadataActions;

    @Autowired
    public DescriptorController(DescriptorMetadataActions descriptorMetadataActions) {
        this.descriptorMetadataActions = descriptorMetadataActions;
    }

    @GetMapping
    public DescriptorsResponseModel getDescriptors(@RequestParam(required = false) String name, @RequestParam(required = false) String type, @RequestParam(required = false) String context) {
        Set<DescriptorMetadata> permittedDescriptors = descriptorMetadataActions.getDescriptorsByPermissions(name, type, context);
        return new DescriptorsResponseModel(permittedDescriptors);
    }

}
