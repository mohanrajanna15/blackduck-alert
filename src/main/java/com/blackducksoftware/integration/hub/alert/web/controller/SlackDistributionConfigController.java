/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.alert.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.hub.alert.datasource.entity.distribution.SlackDistributionConfigEntity;
import com.blackducksoftware.integration.hub.alert.web.actions.distribution.SlackDistributionConfigActions;
import com.blackducksoftware.integration.hub.alert.web.model.distribution.SlackDistributionRestModel;

@RestController
public class SlackDistributionConfigController extends ConfigController<SlackDistributionRestModel> {
    private final CommonConfigController<SlackDistributionConfigEntity, SlackDistributionRestModel> commonConfigController;

    @Autowired
    public SlackDistributionConfigController(final SlackDistributionConfigActions slackDistributionConfigActions) {
        commonConfigController = new CommonConfigController<>(SlackDistributionConfigEntity.class, SlackDistributionRestModel.class, slackDistributionConfigActions);
    }

    @Override
    @GetMapping(value = "/configuration/distribution/slack")
    public List<SlackDistributionRestModel> getConfig(final Long id) {
        return commonConfigController.getConfig(id);
    }

    @Override
    @PostMapping(value = "/configuration/distribution/slack")
    public ResponseEntity<String> postConfig(final SlackDistributionRestModel restModel) {
        return commonConfigController.postConfig(restModel);
    }

    @Override
    @PutMapping(value = "/configuration/distribution/slack")
    public ResponseEntity<String> putConfig(final SlackDistributionRestModel restModel) {
        return commonConfigController.putConfig(restModel);
    }

    @Override
    @DeleteMapping(value = "/configuration/distribution/slack")
    public ResponseEntity<String> deleteConfig(final SlackDistributionRestModel restModel) {
        return commonConfigController.deleteConfig(restModel);
    }

    @Override
    @PostMapping(value = "/configuration/distribution/slack/test")
    public ResponseEntity<String> testConfig(final SlackDistributionRestModel restModel) {
        return commonConfigController.testConfig(restModel);
    }

    @Override
    public ResponseEntity<String> validateConfig(final SlackDistributionRestModel restModel) {
        return commonConfigController.validateConfig(restModel);
    }

}
