package com.carcrowd.service;

import com.carcrowd.dto.customer.request.CustomerRequestDetailDTO;
import com.carcrowd.dto.customer.request.CustomerRequestSummaryDTO;
import com.carcrowd.dto.customer.request.StatusTimelineEventDTO;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.entity.User;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.exception.UnauthorizedException;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.LeadActivityRepository;
import com.carcrowd.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerRequestService {

    private final UserRepository userRepository;
    private final InterestRegistrationRepository registrationRepository;
    private final LeadActivityRepository activityRepository;

    public CustomerRequestService(UserRepository userRepository,
                                  InterestRegistrationRepository registrationRepository,
                                  LeadActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerRequestSummaryDTO> getCustomerRequests(Authentication authentication) {
        User customer = getAuthenticatedCustomer(authentication);
        List<InterestRegistration> registrations = registrationRepository.findByUserIdOrderByCreatedAtDesc(customer.getId());

        return registrations.stream().map(this::toSummaryDTO).toList();
    }

    @Transactional(readOnly = true)
    public CustomerRequestDetailDTO getCustomerRequestById(Long requestId, Authentication authentication) {
        User customer = getAuthenticatedCustomer(authentication);

        InterestRegistration reg = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        // Strict ownership check: prevent customer from viewing another customer's requests
        if (reg.getUser() == null || !reg.getUser().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Request not found with ID: " + requestId);
        }

        List<LeadActivity> activities = activityRepository.findByInterestRegistrationIdOrderByCreatedAtAsc(requestId);
        List<StatusTimelineEventDTO> timeline = buildTimeline(reg, activities);

        return toDetailDTO(reg, timeline);
    }

    @Transactional
    public CustomerRequestDetailDTO cancelCustomerRequest(Long requestId, String reason, Authentication authentication) {
        User customer = getAuthenticatedCustomer(authentication);

        InterestRegistration reg = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        if (reg.getUser() == null || !reg.getUser().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Request not found with ID: " + requestId);
        }

        LeadStatus currentStatus = reg.getLeadStatus();
        if (currentStatus == LeadStatus.CANCELLED) {
            throw new BadRequestException("This request is already cancelled.");
        }
        if (currentStatus == LeadStatus.BOUGHT) {
            throw new BadRequestException("Cannot cancel a completed purchase request.");
        }

        reg.setLeadStatus(LeadStatus.CANCELLED);
        reg.setUpdatedAt(LocalDateTime.now());

        if (StringUtils.hasText(reason)) {
            String note = "[Customer Cancelled: " + reason.trim() + "]";
            if (StringUtils.hasText(reg.getInternalNotes())) {
                reg.setInternalNotes(reg.getInternalNotes() + "\n" + note);
            } else {
                reg.setInternalNotes(note);
            }
        }
        registrationRepository.save(reg);

        String noteDesc = StringUtils.hasText(reason) ? " Reason: " + reason.trim() : "";
        activityRepository.save(new LeadActivity(
            requestId,
            customer.getEmail(),
            customer.getFullName(),
            "CUSTOMER_CANCELLED",
            currentStatus != null ? currentStatus.name() : null,
            LeadStatus.CANCELLED.name(),
            "Customer cancelled request." + noteDesc
        ));

        List<LeadActivity> activities = activityRepository.findByInterestRegistrationIdOrderByCreatedAtAsc(requestId);
        List<StatusTimelineEventDTO> timeline = buildTimeline(reg, activities);

        return toDetailDTO(reg, timeline);
    }

    private User getAuthenticatedCustomer(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Full authentication is required to access your requests.");
        }

        String principal = authentication.getName();
        return userRepository.findByEmailIgnoreCase(principal)
            .or(() -> userRepository.findByGoogleSub(principal))
            .orElseThrow(() -> new UnauthorizedException("Customer account not found for authenticated session."));
    }

    private CustomerRequestSummaryDTO toSummaryDTO(InterestRegistration r) {
        String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
        String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
        Long carId = r.getCar() != null ? r.getCar().getId() : null;
        Long brandId = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getId() : null;
        String imageUrl = r.getCar() != null ? r.getCar().getImageUrl() : null;

        LeadStatus status = r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED;
        LocalDateTime lastUpdated = r.getUpdatedAt() != null ? r.getUpdatedAt() : r.getCreatedAt();

        return new CustomerRequestSummaryDTO(
            r.getId(),
            carId,
            brandId,
            brandName,
            modelName,
            imageUrl,
            r.getCity(),
            r.getCreatedAt(),
            lastUpdated,
            status,
            status.getDisplayLabel(),
            status.getCustomerMessage(),
            status.getNextStep()
        );
    }

    private CustomerRequestDetailDTO toDetailDTO(InterestRegistration r, List<StatusTimelineEventDTO> timeline) {
        String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
        String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
        Long carId = r.getCar() != null ? r.getCar().getId() : null;
        Long brandId = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getId() : null;
        String imageUrl = r.getCar() != null ? r.getCar().getImageUrl() : null;

        LeadStatus status = r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED;
        LocalDateTime lastUpdated = r.getUpdatedAt() != null ? r.getUpdatedAt() : r.getCreatedAt();

        return new CustomerRequestDetailDTO(
            r.getId(),
            carId,
            brandId,
            brandName,
            modelName,
            imageUrl,
            r.getCity(),
            r.getCreatedAt(),
            lastUpdated,
            status,
            status.getDisplayLabel(),
            status.getCustomerMessage(),
            status.getNextStep(),
            r.getFullName(),
            r.getPhoneNumber(),
            r.getEmail(),
            r.getAge(),
            r.getPurchaseTimeline(),
            timeline
        );
    }

    private List<StatusTimelineEventDTO> buildTimeline(InterestRegistration reg, List<LeadActivity> activities) {
        List<StatusTimelineEventDTO> timeline = new ArrayList<>();
        LeadStatus currentStatus = reg.getLeadStatus() != null ? reg.getLeadStatus() : LeadStatus.JOINED;

        // Find activity timestamps if present
        LocalDateTime joinedTime = reg.getCreatedAt();
        LocalDateTime contactedTime = null;
        LocalDateTime interestedTime = null;
        LocalDateTime outcomeTime = null;

        for (LeadActivity act : activities) {
            if ("CONTACTED".equalsIgnoreCase(act.getNewStatus()) && contactedTime == null) {
                contactedTime = act.getCreatedAt();
            } else if ("WAITING".equalsIgnoreCase(act.getNewStatus()) && interestedTime == null) {
                interestedTime = act.getCreatedAt();
            } else if (("BOUGHT".equalsIgnoreCase(act.getNewStatus()) || "CANCELLED".equalsIgnoreCase(act.getNewStatus())) && outcomeTime == null) {
                outcomeTime = act.getCreatedAt();
            }
        }

        if (contactedTime == null && (currentStatus == LeadStatus.CONTACTED || currentStatus == LeadStatus.WAITING || currentStatus == LeadStatus.BOUGHT || currentStatus == LeadStatus.CANCELLED)) {
            contactedTime = reg.getLastContactedAt() != null ? reg.getLastContactedAt() : reg.getCreatedAt();
        }

        if (interestedTime == null && (currentStatus == LeadStatus.WAITING || currentStatus == LeadStatus.BOUGHT)) {
            interestedTime = reg.getUpdatedAt() != null ? reg.getUpdatedAt() : reg.getCreatedAt();
        }

        if (outcomeTime == null && (currentStatus == LeadStatus.BOUGHT || currentStatus == LeadStatus.CANCELLED)) {
            outcomeTime = reg.getUpdatedAt() != null ? reg.getUpdatedAt() : reg.getCreatedAt();
        }

        // 1. Step 1: Request Submitted (Always completed once joined)
        timeline.add(new StatusTimelineEventDTO(
            "JOINED",
            "Request Submitted",
            joinedTime,
            "Your request was submitted and you joined the group for " + (reg.getCar() != null ? reg.getCar().getModelName() : "this vehicle") + ".",
            true,
            currentStatus == LeadStatus.JOINED
        ));

        // 2. Step 2: Team Contacted
        boolean contactedCompleted = currentStatus == LeadStatus.WAITING || currentStatus == LeadStatus.BOUGHT || currentStatus == LeadStatus.CANCELLED;
        boolean isContactedCurrent = currentStatus == LeadStatus.CONTACTED;
        timeline.add(new StatusTimelineEventDTO(
            "CONTACTED",
            "Team Contacted",
            (contactedCompleted || isContactedCurrent) ? contactedTime : null,
            (contactedCompleted || isContactedCurrent)
                ? "Our CarCrowd team contacted you to discuss group details and confirm your purchase timeline."
                : "Our CarCrowd team will call your verified number to confirm your interest.",
            contactedCompleted,
            isContactedCurrent
        ));

        // 3. Step 3: Decision Outcome (WAITING or CANCELLED)
        if (currentStatus == LeadStatus.CANCELLED) {
            timeline.add(new StatusTimelineEventDTO(
                "CANCELLED",
                "Request Cancelled",
                outcomeTime,
                "Request cancelled as you decided not to proceed at this time.",
                true,
                true
            ));
        } else {
            boolean waitingCompleted = currentStatus == LeadStatus.BOUGHT;
            boolean isWaitingCurrent = currentStatus == LeadStatus.WAITING;
            timeline.add(new StatusTimelineEventDTO(
                "WAITING",
                "Waiting for Next Step",
                (waitingCompleted || isWaitingCurrent) ? (interestedTime != null ? interestedTime : outcomeTime) : null,
                waitingCompleted || isWaitingCurrent
                    ? "Interest confirmed! Group buying dealership coordination and negotiation in progress."
                    : "Wait for CarCrowd to coordinate dealership negotiations after confirming interest.",
                waitingCompleted,
                isWaitingCurrent
            ));

            boolean boughtCompleted = currentStatus == LeadStatus.BOUGHT;
            timeline.add(new StatusTimelineEventDTO(
                "BOUGHT",
                "Car Bought",
                boughtCompleted ? outcomeTime : null,
                boughtCompleted
                    ? "Congratulations! You have successfully purchased your car through CarCrowd group buying."
                    : "Final vehicle purchase and delivery at authorized dealership with CarCrowd savings.",
                boughtCompleted,
                boughtCompleted
            ));
        }

        return timeline;
    }
}
