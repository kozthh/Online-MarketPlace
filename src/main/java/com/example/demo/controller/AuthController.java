package com.example.demo.controller;

import com.auth0.spring.boot.Auth0AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Auth0AuthenticationToken token = (Auth0AuthenticationToken) authentication;

        Map<String, Object> body = new HashMap<>();
        body.put("sub", authentication.getName());
        body.put("email", token.getClaim("email"));
        body.put("scopes", token.getScopes());
        return body;
    }
}
