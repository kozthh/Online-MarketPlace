package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesAuth0ConfigWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domain").value("dev-10vbzn05euvzgpmi.us.auth0.com"))
                .andExpect(jsonPath("$.audience").value("https://mcplace-api"))
                .andExpect(jsonPath("$.clientId").value("9GfoU7nayQRGT3VYZUpZvZB69LovAmAT"));
    }

    @Test
    void servesLoginPageWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Online Marketplace")));
    }
}
