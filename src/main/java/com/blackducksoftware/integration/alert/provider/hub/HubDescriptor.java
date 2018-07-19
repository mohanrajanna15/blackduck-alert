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
package com.blackducksoftware.integration.alert.provider.hub;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.ObjectTransformer;
import com.blackducksoftware.integration.alert.datasource.entity.DatabaseEntity;
import com.blackducksoftware.integration.alert.descriptor.ProviderDescriptor;
import com.blackducksoftware.integration.alert.exception.AlertException;
import com.blackducksoftware.integration.alert.provider.hub.model.GlobalHubConfigEntity;
import com.blackducksoftware.integration.alert.provider.hub.model.GlobalHubConfigRestModel;
import com.blackducksoftware.integration.alert.provider.hub.model.GlobalHubRepository;
import com.blackducksoftware.integration.alert.web.model.ConfigRestModel;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.google.gson.Gson;

@Component
public class HubDescriptor extends ProviderDescriptor {
    public static final String PROVIDER_NAME = "provider_hub";

    private final GlobalHubRepository globalHubRepository;
    private final Gson gson;
    private final ObjectTransformer objectTransformer;

    @Autowired
    public HubDescriptor(final GlobalHubRepository globalHubRepository, final Gson gson, final ObjectTransformer objectTransformer) {
        super(PROVIDER_NAME);
        this.globalHubRepository = globalHubRepository;
        this.gson = gson;
        this.objectTransformer = objectTransformer;
    }

    @Override
    public List<? extends DatabaseEntity> readGlobalEntities() {
        return globalHubRepository.findAll();
    }

    @Override
    public Optional<? extends DatabaseEntity> readGlobalEntity(final long id) {
        return globalHubRepository.findById(id);
    }

    @Override
    public Optional<? extends DatabaseEntity> saveGlobalEntity(final DatabaseEntity entity) {
        if (entity instanceof GlobalHubConfigEntity) {
            final GlobalHubConfigEntity hubEntity = (GlobalHubConfigEntity) entity;
            return Optional.ofNullable(globalHubRepository.save(hubEntity));
        }
        return Optional.empty();
    }

    @Override
    public void deleteGlobalEntity(final long id) {
        globalHubRepository.deleteById(id);
    }

    @Override
    public ConfigRestModel convertFromStringToGlobalRestModel(final String json) {
        return gson.fromJson(json, GlobalHubConfigRestModel.class);
    }

    @Override
    public DatabaseEntity convertFromGlobalRestModelToGlobalConfigEntity(final ConfigRestModel restModel) throws AlertException {
        return objectTransformer.configRestModelToDatabaseEntity(restModel, GlobalHubConfigEntity.class);
    }

    @Override
    public ConfigRestModel convertFromGlobalEntityToGlobalRestModel(final DatabaseEntity entity) throws AlertException {
        return objectTransformer.databaseEntityToConfigRestModel(entity, GlobalHubConfigRestModel.class);
    }

    @Override
    public void validateGlobalConfig(final ConfigRestModel restModel, final Map<String, String> fieldErrors) {
        // TODO Auto-generated method stub

    }

    @Override
    public void testGlobalConfig(final DatabaseEntity entity) throws IntegrationException {
        // TODO Auto-generated method stub

    }

    @Override
    public Field[] getGlobalEntityFields() {
        return GlobalHubConfigEntity.class.getDeclaredFields();
    }

    @Override
    public ConfigRestModel getGlobalRestModelObject() {
        return new GlobalHubConfigRestModel();
    }

}