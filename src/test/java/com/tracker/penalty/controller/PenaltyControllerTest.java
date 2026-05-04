package com.tracker.penalty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.penalty.dto.PenaltyDto;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.entity.Severity;
import com.tracker.penalty.security.CustomUserDetailsService;
import com.tracker.penalty.security.JwtUtil;
import com.tracker.penalty.service.PenaltyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PenaltyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PenaltyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private PenaltyService penaltyService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PenaltyDto penaltyDto;

    @BeforeEach
    void setUp() {
        penaltyDto = new PenaltyDto();
        penaltyDto.setId(1L);
        penaltyDto.setTitle("Test Penalty");
        penaltyDto.setDescription("Test Description");
        penaltyDto.setSeverity(Severity.HIGH);
        penaltyDto.setStatus(PenaltyStatus.ACTIVE);
    }

    @Test
    @WithMockUser
    void testGetAllPenalties() throws Exception {
        Page<PenaltyDto> page = new PageImpl<>(Collections.singletonList(penaltyDto));
        when(penaltyService.getAllPenalties(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/penalties/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Penalty"));
    }

    @Test
    @WithMockUser
    void testGetPenaltyById() throws Exception {
        when(penaltyService.getPenaltyById(1L)).thenReturn(penaltyDto);

        mockMvc.perform(get("/api/penalties/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Penalty"));
    }

    @Test
    @WithMockUser
    void testCreatePenalty() throws Exception {
        when(penaltyService.createPenalty(any(PenaltyDto.class))).thenReturn(penaltyDto);

        mockMvc.perform(post("/api/penalties/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(penaltyDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Penalty"));
    }
}
