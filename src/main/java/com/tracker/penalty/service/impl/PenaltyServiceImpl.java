package com.tracker.penalty.service.impl;

import com.tracker.penalty.dto.PenaltyDto;
import com.tracker.penalty.entity.Penalty;
import com.tracker.penalty.exception.ResourceNotFoundException;
import com.tracker.penalty.repository.PenaltyRepository;
import com.tracker.penalty.service.EmailService;
import com.tracker.penalty.service.PenaltyService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final EmailService emailService;

    public PenaltyServiceImpl(PenaltyRepository penaltyRepository, EmailService emailService) {
        this.penaltyRepository = penaltyRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    @CacheEvict(value = "penalties_page", allEntries = true)
    public PenaltyDto createPenalty(PenaltyDto penaltyDto) {
        Penalty penalty = mapToEntity(penaltyDto);
        Penalty savedPenalty = penaltyRepository.save(penalty);
        
        try {
            emailService.sendNewPenaltyAlert(savedPenalty.getTitle(), savedPenalty.getSeverity().name());
        } catch (Exception e) {
            // Error sending email, log it but don't fail transaction
        }

        return mapToDto(savedPenalty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "penalties_page", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PenaltyDto> getAllPenalties(Pageable pageable) {
        return penaltyRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "penalty", key = "#id")
    public PenaltyDto getPenaltyById(Long id) {
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found with id: " + id));
        return mapToDto(penalty);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "penalty", key = "#id"),
            @CacheEvict(value = "penalties_page", allEntries = true)
    })
    public PenaltyDto updatePenalty(Long id, PenaltyDto penaltyDto) {
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found with id: " + id));

        penalty.setTitle(penaltyDto.getTitle());
        penalty.setDescription(penaltyDto.getDescription());
        penalty.setSeverity(penaltyDto.getSeverity());
        penalty.setStatus(penaltyDto.getStatus());

        Penalty updatedPenalty = penaltyRepository.save(penalty);
        return mapToDto(updatedPenalty);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "penalty", key = "#id"),
            @CacheEvict(value = "penalties_page", allEntries = true)
    })
    public void deletePenalty(Long id) {
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found with id: " + id));
        penaltyRepository.delete(penalty);
    }

    private PenaltyDto mapToDto(Penalty penalty) {
        PenaltyDto dto = new PenaltyDto();
        dto.setId(penalty.getId());
        dto.setTitle(penalty.getTitle());
        dto.setDescription(penalty.getDescription());
        dto.setSeverity(penalty.getSeverity());
        dto.setStatus(penalty.getStatus());
        dto.setCreatedAt(penalty.getCreatedAt());
        dto.setUpdatedAt(penalty.getUpdatedAt());
        return dto;
    }

    private Penalty mapToEntity(PenaltyDto dto) {
        Penalty penalty = new Penalty();
        penalty.setTitle(dto.getTitle());
        penalty.setDescription(dto.getDescription());
        penalty.setSeverity(dto.getSeverity());
        penalty.setStatus(dto.getStatus());
        return penalty;
    }
}
