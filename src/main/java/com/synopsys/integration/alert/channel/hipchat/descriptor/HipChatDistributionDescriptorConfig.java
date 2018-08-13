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
package com.synopsys.integration.alert.channel.hipchat.descriptor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.event.ChannelEvent;
import com.synopsys.integration.alert.channel.event.ChannelEventFactory;
import com.synopsys.integration.alert.channel.hipchat.HipChatChannel;
import com.synopsys.integration.alert.database.channel.hipchat.HipChatDistributionConfigEntity;
import com.synopsys.integration.alert.database.channel.hipchat.HipChatDistributionRepositoryAccessor;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.alert.common.descriptor.config.DescriptorConfig;
import com.synopsys.integration.alert.common.descriptor.config.UIComponent;
import com.synopsys.integration.alert.database.entity.DatabaseEntity;
import com.synopsys.integration.alert.web.channel.model.HipChatDistributionConfig;
import com.synopsys.integration.alert.web.model.Config;

@Component
public class HipChatDistributionDescriptorConfig extends DescriptorConfig {
    private final ChannelEventFactory channelEventFactory;
    private final HipChatChannel hipChatChannel;

    @Autowired
    public HipChatDistributionDescriptorConfig(final HipChatDistributionTypeConverter databaseContentConverter, final HipChatDistributionRepositoryAccessor repositoryAccessor,
            final ChannelEventFactory channelEventFactory, final HipChatChannel hipChatChannel) {
        super(databaseContentConverter, repositoryAccessor);
        this.channelEventFactory = channelEventFactory;
        this.hipChatChannel = hipChatChannel;
    }

    @Override
    public UIComponent getUiComponent() {
        return new UIComponent("HipChat", "hipchat", "comments", "HipChatJobConfiguration");
    }

    @Override
    public void validateConfig(final Config restModel, final Map<String, String> fieldErrors) {
        final HipChatDistributionConfig hipChatRestModel = (HipChatDistributionConfig) restModel;
        if (StringUtils.isBlank(hipChatRestModel.getRoomId())) {
            fieldErrors.put("roomId", "A Room Id is required.");
        } else if (!StringUtils.isNumeric(hipChatRestModel.getRoomId())) {
            fieldErrors.put("roomId", "Room Id must be an integer value");
        }
    }

    @Override
    public void testConfig(final DatabaseEntity entity) throws IntegrationException {
        final HipChatDistributionConfigEntity hipChatEntity = (HipChatDistributionConfigEntity) entity;
        final ChannelEvent event = channelEventFactory.createChannelTestEvent(HipChatChannel.COMPONENT_NAME);
        hipChatChannel.sendAuditedMessage(event, hipChatEntity);
    }

}