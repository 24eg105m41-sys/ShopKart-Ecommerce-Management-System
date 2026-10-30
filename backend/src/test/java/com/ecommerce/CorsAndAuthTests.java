package com.ecommerce;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CorsAndAuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("OPTIONS preflight for active Vercel preview domain returns 200 with CORS headers")
    public void testPreflightFromCurrentVercelPreview() throws Exception {
        String vercelOrigin = "https://shopkart-frontend-qlr8n5of6-ecommerce-f8fb.vercel.app";

        mockMvc.perform(options("/api/auth/register")
                .header(HttpHeaders.ORIGIN, vercelOrigin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,authorization"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, vercelOrigin))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")));
    }

    @Test
    @DisplayName("OPTIONS preflight for any vercel preview pattern returns 200 with matching origin")
    public void testPreflightFromAnyVercelSubdomain() throws Exception {
        String vercelOrigin = "https://shopkart-frontend-abc12345-ecommerce-f8fb.vercel.app";

        mockMvc.perform(options("/api/auth/register")
                .header(HttpHeaders.ORIGIN, vercelOrigin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, vercelOrigin))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("OPTIONS preflight for localhost returns 200 with matching origin")
    public void testPreflightFromLocalhost() throws Exception {
        String localhostOrigin = "http://localhost:5173";

        mockMvc.perform(options("/api/auth/register")
                .header(HttpHeaders.ORIGIN, localhostOrigin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, localhostOrigin))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("OPTIONS preflight from unauthorized origin is rejected with 403")
    public void testPreflightFromDisallowedOrigin() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                .header(HttpHeaders.ORIGIN, "https://unauthorized-malicious-site.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/auth/login works without preexisting token")
    public void testAdminLogin() throws Exception {
        String loginPayload = """
            {
                "email": "admin@shopkart.com",
                "password": "Admin@123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.user.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/register registers new merchant user")
    public void testUserRegistration() throws Exception {
        long timestamp = System.currentTimeMillis();
        String registerPayload = String.format("""
            {
                "name": "Test User",
                "email": "testuser%d@shopkart.com",
                "phone": "9876543299",
                "password": "Password@123",
                "confirmPassword": "Password@123"
            }
            """, timestamp);

        mockMvc.perform(post("/api/auth/register")
                .header(HttpHeaders.ORIGIN, "https://shopkart-frontend-qlr8n5of6-ecommerce-f8fb.vercel.app")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://shopkart-frontend-qlr8n5of6-ecommerce-f8fb.vercel.app"))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.user.email").value("testuser" + timestamp + "@shopkart.com"))
            .andExpect(jsonPath("$.data.user.role").value("MERCHANT"));
    }
}

