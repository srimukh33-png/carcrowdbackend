package com.carcrowd.service;

import com.carcrowd.dto.admin.LeadActivityDTO;
import com.carcrowd.dto.admin.LeadDetailDTO;
import com.carcrowd.dto.admin.LeadSummaryDTO;
import com.carcrowd.dto.admin.TeamMemberDTO;
import com.carcrowd.dto.admin.UpdateLeadStatusRequest;
import com.carcrowd.dto.member.MemberNoteRequest;
import com.carcrowd.entity.AdminUser;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ForbiddenException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.LeadActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MemberLeadService {

    private static final Logger log = LoggerFactory.getLogger(MemberLeadService.class);
    private static final DateTimeFormatter NOTE_DT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final AdminUserRepository adminUserRepository;
    private final InterestRegistrationRepository registrationRepository;
    private final LeadActivityRepository activityRepository;

    public MemberLeadService(AdminUserRepository adminUserRepository,
                             InterestRegistrationRepository registrationRepository,
                             LeadActivityRepository activityRepository) {
        this.adminUserRepository = adminUserRepository;
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public TeamMemberDTO getMemberProfile(String email) {
        AdminUser member = getAuthenticatedMember(email);
        long count = registrationRepository.countByAssignedMemberId(member.getId());
        return new TeamMemberDTO(
            member.getId(),
            member.getFullName(),
            member.getEmail(),
            member.getPhoneNumber(),
            member.getDisplayRole(),
            member.isActive(),
            count,
            member.getCreatedAt(),
            member.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<LeadSummaryDTO> getAssignedRequests(String email, LeadStatus status, int page, int size) {
        AdminUser member = getAuthenticatedMember(email);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<InterestRegistration> requests;
        if (status != null) {
            requests = registrationRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("assignedMember").get("id"), member.getId()),
                cb.equal(root.get("leadStatus"), status)
            ), pageable);
        } else {
            requests = registrationRepository.findByAssignedMemberId(member.getId(), pageable);
        }

        return requests.map(this::toSummaryDTO);
    }

    @Transactional(readOnly = true)
    public LeadDetailDTO getAssignedRequestById(String email, Long requestId) {
        AdminUser member = getAuthenticatedMember(email);
        InterestRegistration req = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer request not found with ID: " + requestId));

        verifyOwnership(req, member);

        List<LeadActivityDTO> activities = activityRepository
            .findByInterestRegistrationIdOrderByCreatedAtDesc(requestId)
            .stream()
            .map(this::toActivityDTO)
            .toList();

        return toDetailDTO(req, activities);
    }

    @Transactional
    public LeadDetailDTO updateAssignedRequestStatus(String email, Long requestId, UpdateLeadStatusRequest request) {
        if (request.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        AdminUser member = getAuthenticatedMember(email);
        InterestRegistration req = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer request not found with ID: " + requestId));

        verifyOwnership(req, member);

        LeadStatus oldStatus = req.getLeadStatus();
        validateTransition(oldStatus, request.getStatus());

        req.setLeadStatus(request.getStatus());
        req.setUpdatedAt(LocalDateTime.now());
        if (request.getStatus() == LeadStatus.CONTACTED && req.getLastContactedAt() == null) {
            req.setLastContactedAt(LocalDateTime.now());
        }

        // Append note if provided
        if (StringUtils.hasText(request.getNote())) {
            String noteEntry = "[" + LocalDateTime.now().format(NOTE_DT) + " by " + member.getFullName() + "]: " + request.getNote().trim();
            if (StringUtils.hasText(req.getInternalNotes())) {
                req.setInternalNotes(req.getInternalNotes() + "\n" + noteEntry);
            } else {
                req.setInternalNotes(noteEntry);
            }
        }

        InterestRegistration saved = registrationRepository.save(req);

        // Record status change activity
        String desc = StringUtils.hasText(request.getNote())
            ? "Status changed from " + oldStatus + " to " + request.getStatus() + ". Note: " + request.getNote().trim()
            : "Status changed from " + oldStatus + " to " + request.getStatus();

        LeadActivity activity = new LeadActivity(
            requestId,
            member.getEmail(),
            member.getFullName(),
            "STATUS_CHANGE",
            oldStatus.name(),
            request.getStatus().name(),
            desc
        );
        activityRepository.save(activity);

        log.info("Request {} status updated to {} by assigned member {}", requestId, request.getStatus(), member.getEmail());

        List<LeadActivityDTO> activities = activityRepository
            .findByInterestRegistrationIdOrderByCreatedAtDesc(requestId)
            .stream()
            .map(this::toActivityDTO)
            .toList();

        return toDetailDTO(saved, activities);
    }

    @Transactional
    public LeadDetailDTO addNote(String email, Long requestId, MemberNoteRequest request) {
        if (!StringUtils.hasText(request.getNote())) {
            throw new BadRequestException("Note content is required");
        }

        AdminUser member = getAuthenticatedMember(email);
        InterestRegistration req = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer request not found with ID: " + requestId));

        verifyOwnership(req, member);

        String noteEntry = "[" + LocalDateTime.now().format(NOTE_DT) + " - Call Note by " + member.getFullName() + "]: " + request.getNote().trim();
        if (StringUtils.hasText(req.getInternalNotes())) {
            req.setInternalNotes(req.getInternalNotes() + "\n" + noteEntry);
        } else {
            req.setInternalNotes(noteEntry);
        }
        req.setUpdatedAt(LocalDateTime.now());
        InterestRegistration saved = registrationRepository.save(req);

        LeadActivity activity = new LeadActivity(
            requestId,
            member.getEmail(),
            member.getFullName(),
            "NOTE",
            request.getNote().trim()
        );
        activityRepository.save(activity);

        log.info("Note added to request {} by assigned member {}", requestId, member.getEmail());

        List<LeadActivityDTO> activities = activityRepository
            .findByInterestRegistrationIdOrderByCreatedAtDesc(requestId)
            .stream()
            .map(this::toActivityDTO)
            .toList();

        return toDetailDTO(saved, activities);
    }

    private AdminUser getAuthenticatedMember(String email) {
        if (email == null) {
            throw new ForbiddenException("Authentication required");
        }
        AdminUser user = adminUserRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ForbiddenException("Team member profile not found for: " + email));

        if (!user.isActive()) {
            throw new ForbiddenException("Account is disabled. Please contact an administrator.");
        }
        return user;
    }

    private void verifyOwnership(InterestRegistration req, AdminUser member) {
        if (req.getAssignedMember() == null || !req.getAssignedMember().getId().equals(member.getId())) {
            log.warn("Access denied: Member {} attempted to access request {} assigned to {}",
                member.getEmail(), req.getId(), req.getAssignedMember() != null ? req.getAssignedMember().getEmail() : "nobody");
            throw new ForbiddenException("Access denied: You are not authorized to access this customer request as it is not assigned to you.");
        }
    }

    private void validateTransition(LeadStatus from, LeadStatus to) {
        if (from == to) {
            return;
        }

        // Terminal state checks: BOUGHT and CANCELLED cannot transition to anything
        if (from == LeadStatus.BOUGHT) {
            throw new BadRequestException("Status transition not permitted: Customer request is already in terminal completed state (BOUGHT)");
        }
        if (from == LeadStatus.CANCELLED) {
            throw new BadRequestException("Status transition not permitted: Customer request is already in terminal cancelled state (CANCELLED)");
        }

        boolean allowed = switch (from) {
            case JOINED -> to == LeadStatus.CONTACTED || to == LeadStatus.CANCELLED;
            case CONTACTED -> to == LeadStatus.WAITING || to == LeadStatus.CANCELLED || to == LeadStatus.BOUGHT;
            case WAITING -> to == LeadStatus.BOUGHT || to == LeadStatus.CANCELLED;
            default -> false;
        };

        if (!allowed) {
            throw new BadRequestException("Invalid status transition from " + from + " to " + to);
        }
    }

    private LeadSummaryDTO toSummaryDTO(InterestRegistration r) {
        String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
        String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
        Long carId = r.getCar() != null ? r.getCar().getId() : null;

        Long assignedMemberId = r.getAssignedMember() != null ? r.getAssignedMember().getId() : null;
        String assignedMemberName = r.getAssignedMember() != null ? r.getAssignedMember().getFullName() : null;
        String assignedMemberEmail = r.getAssignedMember() != null ? r.getAssignedMember().getEmail() : null;
        LocalDateTime assignedAt = r.getAssignedAt();

        return new LeadSummaryDTO(
            r.getId(),
            r.getFullName(),
            r.getPhoneNumber(),
            r.getEmail(),
            brandName,
            modelName,
            carId,
            r.getCity(),
            r.getAge(),
            r.getPurchaseTimeline(),
            r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED,
            r.getCreatedAt(),
            r.getNextFollowUpDate(),
            r.getLastContactedAt(),
            assignedMemberId,
            assignedMemberName,
            assignedMemberEmail,
            assignedAt
        );
    }

    private LeadDetailDTO toDetailDTO(InterestRegistration r, List<LeadActivityDTO> activities) {
        String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
        String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
        Long carId = r.getCar() != null ? r.getCar().getId() : null;

        Long assignedMemberId = r.getAssignedMember() != null ? r.getAssignedMember().getId() : null;
        String assignedMemberName = r.getAssignedMember() != null ? r.getAssignedMember().getFullName() : null;
        String assignedMemberEmail = r.getAssignedMember() != null ? r.getAssignedMember().getEmail() : null;
        String assignedMemberPhone = r.getAssignedMember() != null ? r.getAssignedMember().getPhoneNumber() : null;
        LocalDateTime assignedAt = r.getAssignedAt();
        String assignedBy = r.getAssignedBy();

        return new LeadDetailDTO(
            r.getId(),
            r.getFullName(),
            r.getPhoneNumber(),
            r.getEmail(),
            r.getAge(),
            r.getCity(),
            brandName,
            modelName,
            carId,
            r.getPurchaseTimeline(),
            r.isConsent(),
            r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED,
            r.getCreatedAt(),
            r.getNextFollowUpDate(),
            r.getLastContactedAt(),
            r.getInternalNotes(),
            assignedMemberId,
            assignedMemberName,
            assignedMemberEmail,
            assignedMemberPhone,
            assignedAt,
            assignedBy,
            activities
        );
    }

    private LeadActivityDTO toActivityDTO(LeadActivity a) {
        return new LeadActivityDTO(
            a.getId(),
            a.getInterestRegistrationId(),
            a.getAdminEmail(),
            a.getAdminName(),
            a.getActivityType(),
            a.getDescription(),
            a.getCreatedAt()
        );
    }
}
