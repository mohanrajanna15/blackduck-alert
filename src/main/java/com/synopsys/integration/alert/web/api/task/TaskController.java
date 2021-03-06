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
package com.synopsys.integration.alert.web.api.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.common.rest.ResponseFactory;
import com.synopsys.integration.alert.common.security.authorization.AuthorizationManager;
import com.synopsys.integration.alert.common.workflow.task.TaskMetaData;
import com.synopsys.integration.alert.component.tasks.TaskManagementDescriptorKey;
import com.synopsys.integration.alert.web.common.BaseController;

@RestController
@RequestMapping(TaskController.TASK_BASE_PATH)
public class TaskController extends BaseController {
    public static final String TASK_BASE_PATH = BaseController.BASE_PATH + "/task";
    private final AuthorizationManager authorizationManager;
    private final TaskActions taskActions;
    private final TaskManagementDescriptorKey descriptorKey;

    @Autowired
    public TaskController(AuthorizationManager authorizationManager, TaskActions taskActions, TaskManagementDescriptorKey descriptorKey) {
        this.authorizationManager = authorizationManager;
        this.taskActions = taskActions;
        this.descriptorKey = descriptorKey;
    }

    @GetMapping
    public List<TaskMetaData> getAllTasks() {
        if (!hasGlobalPermission(authorizationManager::hasReadPermission, descriptorKey)) {
            throw ResponseFactory.createForbiddenException();
        }
        return taskActions.getTasks();
    }
}
