package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.GHNWebhookRequest;
import com.eyecommer.Backend.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class GHNWebhookController {

    private final ShipmentService shipmentService;

    @PostMapping("/ghn")
    public ResponseEntity<Void> handleGHNWebhook(
            @RequestBody GHNWebhookRequest request,
            @RequestHeader(value = "Token", required = false) String token
    ) {
        shipmentService.handleGHNWebhook(request);
        return ResponseEntity.ok().build();
    }
}