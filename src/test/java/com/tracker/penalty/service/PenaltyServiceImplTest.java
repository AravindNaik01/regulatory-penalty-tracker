package com.tracker.penalty.service;

import com.tracker.penalty.dto.PenaltyDto;
import com.tracker.penalty.entity.Penalty;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.entity.Severity;
import com.tracker.penalty.exception.ResourceNotFoundException;
import com.tracker.penalty.repository.PenaltyRepository;
import com.tracker.penalty.service.impl.PenaltyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PenaltyServiceImplTest {

    @Mock
    private PenaltyRepository penaltyRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PenaltyServiceImpl penaltyService;

    private Penalty penalty;
    private PenaltyDto penaltyDto;

    @BeforeEach
    void setUp() {
        penalty = new Penalty("Title", "Description", Severity.HIGH, PenaltyStatus.ACTIVE);
        penalty.setId(1L);
        penalty.setCreatedAt(LocalDateTime.now());
        penalty.setUpdatedAt(LocalDateTime.now());

        penaltyDto = new PenaltyDto();
        penaltyDto.setTitle("Title");
        penaltyDto.setDescription("Description");
        penaltyDto.setSeverity(Severity.HIGH);
        penaltyDto.setStatus(PenaltyStatus.ACTIVE);
    }

    @Test
    void testCreatePenalty_Success() {
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);
        doNothing().when(emailService).sendNewPenaltyAlert(anyString(), anyString());

        PenaltyDto result = penaltyService.createPenalty(penaltyDto);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        verify(penaltyRepository, times(1)).save(any(Penalty.class));
        verify(emailService, times(1)).sendNewPenaltyAlert(anyString(), anyString());
    }

    @Test
    void testCreatePenalty_EmailFails_StillSucceeds() {
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);
        doThrow(new RuntimeException("Mail server down")).when(emailService).sendNewPenaltyAlert(anyString(), anyString());

        PenaltyDto result = penaltyService.createPenalty(penaltyDto);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        verify(penaltyRepository, times(1)).save(any(Penalty.class));
    }

    @Test
    void testGetAllPenalties_ReturnsPage() {
        Page<Penalty> page = new PageImpl<>(Collections.singletonList(penalty));
        when(penaltyRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<PenaltyDto> result = penaltyService.getAllPenalties(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(penaltyRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetAllPenalties_EmptyList() {
        Page<Penalty> page = new PageImpl<>(Collections.emptyList());
        when(penaltyRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<PenaltyDto> result = penaltyService.getAllPenalties(PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(penaltyRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetPenaltyById_Success() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.of(penalty));

        PenaltyDto result = penaltyService.getPenaltyById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(penaltyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPenaltyById_ThrowsResourceNotFoundException() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> penaltyService.getPenaltyById(1L));
        verify(penaltyRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdatePenalty_Success() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.of(penalty));
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);

        penaltyDto.setTitle("Updated Title");
        PenaltyDto result = penaltyService.updatePenalty(1L, penaltyDto);

        assertNotNull(result);
        assertEquals("Updated Title", penalty.getTitle());
        verify(penaltyRepository, times(1)).findById(1L);
        verify(penaltyRepository, times(1)).save(any(Penalty.class));
    }

    @Test
    void testUpdatePenalty_ThrowsResourceNotFoundException() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> penaltyService.updatePenalty(1L, penaltyDto));
        verify(penaltyRepository, times(1)).findById(1L);
        verify(penaltyRepository, never()).save(any(Penalty.class));
    }

    @Test
    void testDeletePenalty_Success() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.of(penalty));
        doNothing().when(penaltyRepository).delete(penalty);

        penaltyService.deletePenalty(1L);

        verify(penaltyRepository, times(1)).findById(1L);
        verify(penaltyRepository, times(1)).delete(penalty);
    }

    @Test
    void testDeletePenalty_ThrowsResourceNotFoundException() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> penaltyService.deletePenalty(1L));
        verify(penaltyRepository, times(1)).findById(1L);
        verify(penaltyRepository, never()).delete(any(Penalty.class));
    }
}
