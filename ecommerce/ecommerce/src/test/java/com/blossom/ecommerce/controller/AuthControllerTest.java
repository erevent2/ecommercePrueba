package com.blossom.ecommerce.controller;

import com.blossom.ecommerce.dto.request.AuthRequest;
import com.blossom.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        // Limpiamos la BD antes de cada test para evitar conflictos de email único
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe registrar un usuario exitosamente y retornar 201 Created")
    void register_should_return201_when_requestIsValid() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@blossom.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) 
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request si el email es inválido")
    void register_should_return400_when_emailIsInvalid() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("email-invalido");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.errors.email").exists());
    }
}