package com.blackducksoftware.integration.hub.notification.event;

import com.blackducksoftware.integration.hub.notification.datasource.entity.event.NotificationEntity;

public abstract class AbstractChannelEvent extends AbstractEvent {
    private final NotificationEntity notificationEntity;

    public AbstractChannelEvent(final NotificationEntity notificationEntity) {
        super();
        this.notificationEntity = notificationEntity;
    }

    public NotificationEntity getNotificationEntity() {
        return notificationEntity;
    }
}
