package com.tracker.penalty.service;

import com.tracker.penalty.entity.Penalty;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.repository.PenaltyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PenaltyReminderScheduler {

    private final PenaltyRepository penaltyRepository;
    private final EmailService emailService;

    public PenaltyReminderScheduler(PenaltyRepository penaltyRepository, EmailService emailService) {
        this.penaltyRepository = penaltyRepository;
        this.emailService = emailService;
    }

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendRemindersForPendingPenalties() {
        List<Penalty> pendingPenalties = penaltyRepository.findByStatus(PenaltyStatus.PENDING);
        for (Penalty penalty : pendingPenalties) {
            try {
                emailService.sendReminderAlert(penalty.getTitle(), penalty.getStatus().name());
            } catch (Exception e) {
                // Log and continue to the next penalty if one fails
            }
        }
    }
}
