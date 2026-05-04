package com.tracker.penalty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.penalty.dto.AuthDtos.AuthResponse;
import com.tracker.penalty.dto.AuthDtos.LoginRequest;
import com.tracker.penalty.dto.AuthDtos.RegisterRequest;
import com.tracker.penalty.dto.PenaltyDto;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.entity.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender javaMailSender; // Mock mail so it doesn't try to connect

    @MockBean
    private RedisConnectionFactory redisConnectionFactory; // Mock Redis

    @Test
    void testEndToEndFlow() throws Exception {
        // 1. Register User
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setUsername("testadmin");
        registerReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // 2. Login User
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("testadmin");
        loginReq.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.getToken();

        // 3. Create Penalty
        PenaltyDto penaltyDto = new PenaltyDto();
        penaltyDto.setTitle("Pollution Violation");
        penaltyDto.setDescription("Spilled waste in river.");
        penaltyDto.setSeverity(Severity.CRITICAL);
        penaltyDto.setStatus(PenaltyStatus.ACTIVE);

        MvcResult createResult = mockMvc.perform(post("/api/penalties/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(penaltyDto)))
                .andExpect(status().isCreated())
                .andReturn();

        PenaltyDto createdPenalty = objectMapper.readValue(createResult.getResponse().getContentAsString(), PenaltyDto.class);
        Long id = createdPenalty.getId();

        // 4. Get Penalty By ID
        mockMvc.perform(get("/api/penalties/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5. Get All Penalties (Pagination)
        mockMvc.perform(get("/api/penalties/all?page=0&size=10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
