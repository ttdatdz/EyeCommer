package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.GHNWebhookRequest;

public interface ShipmentService {
    void handleGHNWebhook(GHNWebhookRequest req);
}
