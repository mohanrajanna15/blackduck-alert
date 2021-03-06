/**
 * azure-boards-common
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
package com.synopsys.integration.azure.boards.common.service.workitem.response;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synopsys.integration.azure.boards.common.model.ReferenceLinkModel;
import com.synopsys.integration.azure.boards.common.service.workitem.WorkItemCommentVersionRefModel;
import com.synopsys.integration.azure.boards.common.service.workitem.WorkItemRelationModel;
import com.synopsys.integration.azure.boards.common.util.AzureFieldDefinition;
import com.synopsys.integration.azure.boards.common.util.AzureFieldsExtractor;

public class WorkItemResponseModel {
    public static final List<AzureFieldDefinition> FIELD_DEFINITIONS = WorkItemResponseFields.list();

    private Integer id;
    private Integer rev;
    private JsonObject fields;
    private List<WorkItemRelationModel> relations;
    private WorkItemCommentVersionRefModel commentVersionRef;
    private String url;
    private Map<String, ReferenceLinkModel> _links;

    public WorkItemResponseModel() {
        // For serialization
    }

    public Integer getId() {
        return id;
    }

    public Integer getRev() {
        return rev;
    }

    public JsonObject getFields() {
        return fields;
    }

    public WorkItemFieldsWrapper createFieldsWrapper(Gson gson) {
        AzureFieldsExtractor azureFieldsExtractor = new AzureFieldsExtractor(gson);
        return new WorkItemFieldsWrapper(azureFieldsExtractor, fields);
    }

    public List<WorkItemRelationModel> getRelations() {
        return relations;
    }

    public WorkItemCommentVersionRefModel getCommentVersionRef() {
        return commentVersionRef;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, ReferenceLinkModel> getLinks() {
        return _links;
    }

}
