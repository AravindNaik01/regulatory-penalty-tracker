package com.tracker.penalty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.penalty.dto.AuthDtos.LoginRequest;
import com.tracker.penalty.dto.AuthDtos.RegisterRequest;
import com.tracker.penalty.entity.User;
import com.tracker.penalty.repository.UserRepository;
import com.tracker.penalty.security.CustomUserDetailsService;
import com.tracker.penalty.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private UserRepository userRepository;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("pass");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("pass");

        when(jwtUtil.generateToken("user")).thenReturn("jwt_token");
        when(jwtUtil.generateRefreshToken("user")).thenReturn("refresh_token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
