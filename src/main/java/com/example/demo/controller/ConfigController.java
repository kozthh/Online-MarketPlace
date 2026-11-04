package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final String domain;
    private final String clientId;
    private final String audience;

    public ConfigController(@Value("${auth0.domain}") String domain,
                            @Value("${auth0.client-id}") String clientId,
                            @Value("${auth0.audience}") String audience) {
        this.domain = domain;
        this.clientId = clientId;
        this.audience = audience;
    }

    @GetMapping
    public Map<String, String> config() {
        return Map.of(
                "domain", domain,
                "clientId", clientId,
                "audience", audience
        );
    }
}
