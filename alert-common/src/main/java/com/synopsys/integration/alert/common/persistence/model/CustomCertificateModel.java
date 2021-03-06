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
package com.synopsys.integration.alert.common.persistence.model;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

// TODO rename this to something more descriptive like UserProvidedCertificateModel
public class CustomCertificateModel extends AlertSerializableModel {
    private static final long serialVersionUID = -6655684999195764188L;
    private Long id;
    private final String alias;
    private final String certificateContent;
    private final String lastUpdated;

    public CustomCertificateModel(String alias, String certificateContent, String lastUpdated) {
        this.id = null;
        this.alias = alias;
        this.certificateContent = certificateContent;
        this.lastUpdated = lastUpdated;
    }

    public CustomCertificateModel(Long id, String alias, String certificateContent, String lastUpdated) {
        this.id = id;
        this.alias = alias;
        this.certificateContent = certificateContent;
        this.lastUpdated = lastUpdated;
    }

    public Long getNullableId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public String getCertificateContent() {
        return certificateContent;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
