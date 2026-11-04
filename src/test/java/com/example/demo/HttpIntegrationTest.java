package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void loginPageAndConfigArePublic() throws Exception {
        HttpResponse<String> root = get("/");
        assertEquals(200, root.statusCode());
        assertTrue(root.body().contains("Online Marketplace"));

        HttpResponse<String> config = get("/api/config");
        assertEquals(200, config.statusCode());
        assertTrue(config.body().contains("https://mcplace-api"));

        HttpResponse<String> me = get("/api/auth/me");
        assertEquals(401, me.statusCode());
    }
}
