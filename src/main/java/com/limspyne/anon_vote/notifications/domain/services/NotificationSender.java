package com.limspyne.anon_vote.notifications.domain.services;

import java.util.UUID;

public interface NotificationSender {
    void send(UUID userId, String text);
}
