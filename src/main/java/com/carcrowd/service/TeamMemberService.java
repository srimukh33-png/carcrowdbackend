package com.carcrowd.service;

import com.carcrowd.dto.admin.*;
import com.carcrowd.entity.AdminUser;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.exception.BadRequestException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamMemberService {

    private static final Logger log = LoggerFactory.getLogger(TeamMemberService.class);

    private final AdminUserRepository adminUserRepository;
    private final InterestRegistrationRepository registrationRepository;
    private final LeadActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;

    public TeamMemberService(AdminUserRepository adminUserRepository,
                             InterestRegistrationRepository registrationRepository,
                             LeadActivityRepository activityRepository,
                             PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<TeamMemberDTO> getMembers(String search, Boolean active, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProp = "createdAt";
        if (StringUtils.hasText(sortBy)) {
            sortProp = switch (sortBy.toLowerCase()) {
                case "name", "fullname" -> "fullName";
                case "email" -> "email";
                case "role" -> "role";
                default -> "createdAt";
            };
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(direction, sortProp));
        String querySearch = StringUtils.hasText(search) ? search.trim() : null;

        Page<AdminUser> users = adminUserRepository.searchMembers(querySearch, active, pageable);
        return users.map(this::toTeamMemberDTO);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDTO> getActiveMembersList() {
        return adminUserRepository.findByActiveTrueOrderByFullNameAsc().stream()
            .map(this::toTeamMemberDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public TeamMemberDTO getMemberById(Long id) {
        AdminUser user = adminUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team member not found with ID: " + id));
        return toTeamMemberDTO(user);
    }

    @Transactional
    public TeamMemberDTO createMember(CreateMemberRequest request, String createdByAdminEmail) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (adminUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("A team member with email '" + normalizedEmail + "' already exists");
        }

        if (!StringUtils.hasText(request.getFullName())) {
            throw new BadRequestException("Full name is required");
        }
        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        String assignedRole = "ROLE_MEMBER";
        if (StringUtils.hasText(request.getRole())) {
            String cleanRole = request.getRole().trim().toUpperCase();
            if (cleanRole.contains("ADMIN")) {
                assignedRole = "ROLE_ADMIN";
            }
        }

        AdminUser newUser = new AdminUser(
            normalizedEmail,
            passwordEncoder.encode(request.getPassword()),
            request.getFullName().trim(),
            StringUtils.hasText(request.getPhoneNumber()) ? request.getPhoneNumber().trim() : null,
            assignedRole,
            request.isActive()
        );

        AdminUser saved = adminUserRepository.save(newUser);
        log.info("Team member created: ID={}, email={}, role={} by {}", saved.getId(), saved.getEmail(), saved.getRole(), createdByAdminEmail);
        return toTeamMemberDTO(saved);
    }

    @Transactional
    public TeamMemberDTO updateMember(Long id, UpdateMemberRequest request, String currentAdminEmail) {
        AdminUser member = adminUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team member not found with ID: " + id));

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (adminUserRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
            throw new BadRequestException("Another team member with email '" + normalizedEmail + "' already exists");
        }

        // Safeguard: Do not leave system without an active admin
        boolean isDemotingAdmin = member.isAdmin() && request.getRole() != null && !request.getRole().toUpperCase().contains("ADMIN");
        boolean isDeactivatingAdmin = member.isAdmin() && Boolean.FALSE.equals(request.getActive());

        if ((isDemotingAdmin || isDeactivatingAdmin) && adminUserRepository.countActiveAdmins() <= 1) {
            throw new BadRequestException("Cannot demote or deactivate the only active administrator. At least one active admin must remain.");
        }

        member.setFullName(request.getFullName().trim());
        member.setEmail(normalizedEmail);
        member.setPhoneNumber(StringUtils.hasText(request.getPhoneNumber()) ? request.getPhoneNumber().trim() : null);

        if (StringUtils.hasText(request.getRole())) {
            String roleUpper = request.getRole().trim().toUpperCase();
            member.setRole(roleUpper.contains("ADMIN") ? "ROLE_ADMIN" : "ROLE_MEMBER");
        }

        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }

        if (StringUtils.hasText(request.getPassword()) && request.getPassword().length() >= 6) {
            member.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        member.setUpdatedAt(LocalDateTime.now());
        AdminUser updated = adminUserRepository.save(member);
        log.info("Team member updated: ID={}, email={} by {}", updated.getId(), updated.getEmail(), currentAdminEmail);
        return toTeamMemberDTO(updated);
    }

    @Transactional
    public TeamMemberDTO updateMemberStatus(Long id, UpdateMemberStatusRequest request, String currentAdminEmail, String currentAdminName) {
        AdminUser member = adminUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team member not found with ID: " + id));

        if (Boolean.FALSE.equals(request.getActive())) {
            // Safeguard against removing only admin
            if (member.isAdmin() && adminUserRepository.countActiveAdmins() <= 1) {
                throw new BadRequestException("Cannot deactivate the only active administrator. At least one active admin must remain.");
            }

            // Check if member has assigned requests
            long assignedCount = registrationRepository.countByAssignedMemberId(id);
            if (assignedCount > 0) {
                if (request.getReassignToMemberId() != null) {
                    AdminUser targetMember = adminUserRepository.findById(request.getReassignToMemberId())
                        .orElseThrow(() -> new ResourceNotFoundException("Reassignment target member not found with ID: " + request.getReassignToMemberId()));
                    if (!targetMember.isActive()) {
                        throw new BadRequestException("Cannot reassign requests to an inactive member");
                    }
                    if (targetMember.getId().equals(id)) {
                        throw new BadRequestException("Cannot reassign requests to the member being deactivated");
                    }

                    // Reassign all requests
                    registrationRepository.reassignAll(id, targetMember, LocalDateTime.now(), currentAdminEmail);

                    log.info("Reassigned {} requests from member ID={} to member ID={} before deactivation",
                        assignedCount, id, targetMember.getId());
                }
                // If no reassignToMemberId specified, requests remain preserved with member or admin handles individually
            }
        }

        member.setActive(request.getActive());
        member.setUpdatedAt(LocalDateTime.now());
        AdminUser saved = adminUserRepository.save(member);
        log.info("Member status changed: ID={}, active={} by {}", saved.getId(), saved.isActive(), currentAdminEmail);
        return toTeamMemberDTO(saved);
    }

    @Transactional
    public void deleteMember(Long id, String currentAdminEmail) {
        AdminUser member = adminUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team member not found with ID: " + id));

        // Safeguard 1: Prevent self-deletion if current admin
        if (member.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            long activeAdmins = adminUserRepository.countActiveAdmins();
            if (activeAdmins <= 1) {
                throw new BadRequestException("Cannot delete your own administrator account when it is the only active admin.");
            }
        }

        // Safeguard 2: Check for active assigned customer requests
        long assignedCount = registrationRepository.countByAssignedMemberId(id);
        if (assignedCount > 0) {
            throw new BadRequestException("Cannot delete team member '" + member.getFullName() +
                "' because they have " + assignedCount + " assigned customer request(s). " +
                "Please reassign or unassign all customer requests before deleting this member.");
        }

        // Safeguard 3: If admin, verify another active admin exists
        if (member.isAdmin() && adminUserRepository.countActiveAdmins() <= 1) {
            throw new BadRequestException("Cannot delete the only active administrator. At least one active admin must remain in the system.");
        }

        adminUserRepository.delete(member);
        log.info("Team member deleted: ID={}, email={} by {}", id, member.getEmail(), currentAdminEmail);
    }

    @Transactional(readOnly = true)
    public Page<LeadSummaryDTO> getMemberCustomers(Long memberId, int page, int size) {
        if (!adminUserRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Team member not found with ID: " + memberId);
        }
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return registrationRepository.findByAssignedMemberId(memberId, pageable).map(this::toLeadSummaryDTO);
    }

    @Transactional
    public LeadDetailDTO assignRequest(Long requestId, Long memberId, String adminEmail, String adminName) {
        InterestRegistration req = registrationRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer request not found with ID: " + requestId));

        AdminUser previousMember = req.getAssignedMember();
        String previousName = previousMember != null ? previousMember.getFullName() : "Unassigned";

        if (memberId == null) {
            // Unassign
            req.setAssignedMember(null);
            req.setAssignedAt(null);
            req.setAssignedBy(null);
            req.setUpdatedAt(LocalDateTime.now());
            registrationRepository.save(req);

            // Log activity
            LeadActivity activity = new LeadActivity(
                requestId,
                adminEmail,
                adminName,
                "ASSIGNMENT",
                "Request was unassigned from " + previousName + " by " + adminName
            );
            activityRepository.save(activity);
            log.info("Request {} unassigned by {}", requestId, adminEmail);
        } else {
            AdminUser newMember = adminUserRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Target team member not found with ID: " + memberId));

            if (!newMember.isActive()) {
                throw new BadRequestException("Cannot assign request to an inactive team member: " + newMember.getFullName());
            }

            req.setAssignedMember(newMember);
            req.setAssignedAt(LocalDateTime.now());
            req.setAssignedBy(adminEmail);
            req.setUpdatedAt(LocalDateTime.now());
            registrationRepository.save(req);

            String desc = previousMember == null
                ? "Assigned to " + newMember.getFullName() + " (" + newMember.getEmail() + ") by " + adminName
                : "Reassigned from " + previousName + " to " + newMember.getFullName() + " (" + newMember.getEmail() + ") by " + adminName;

            LeadActivity activity = new LeadActivity(requestId, adminEmail, adminName, "ASSIGNMENT", desc);
            activityRepository.save(activity);
            log.info("Request {} assigned to {} by {}", requestId, newMember.getEmail(), adminEmail);
        }

        return toLeadDetailDTO(req);
    }

    @Transactional
    public int bulkAssign(List<Long> requestIds, Long memberId, String adminEmail, String adminName) {
        if (requestIds == null || requestIds.isEmpty()) {
            return 0;
        }

        AdminUser newMember = null;
        if (memberId != null) {
            newMember = adminUserRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Target team member not found with ID: " + memberId));
            if (!newMember.isActive()) {
                throw new BadRequestException("Cannot assign requests to inactive member: " + newMember.getFullName());
            }
        }

        int count = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Long reqId : requestIds) {
            InterestRegistration req = registrationRepository.findById(reqId).orElse(null);
            if (req == null) continue;

            AdminUser previousMember = req.getAssignedMember();
            String previousName = previousMember != null ? previousMember.getFullName() : "Unassigned";

            req.setAssignedMember(newMember);
            req.setAssignedAt(newMember != null ? now : null);
            req.setAssignedBy(newMember != null ? adminEmail : null);
            req.setUpdatedAt(now);
            registrationRepository.save(req);

            String desc = newMember != null
                ? (previousMember == null
                    ? "Assigned to " + newMember.getFullName() + " via bulk assignment by " + adminName
                    : "Reassigned from " + previousName + " to " + newMember.getFullName() + " via bulk assignment by " + adminName)
                : "Unassigned via bulk action by " + adminName;

            LeadActivity activity = new LeadActivity(reqId, adminEmail, adminName, "ASSIGNMENT", desc);
            activityRepository.save(activity);
            count++;
        }

        log.info("Bulk assigned {} requests to memberId={} by {}", count, memberId, adminEmail);
        return count;
    }

    @Transactional(readOnly = true)
    public Page<LeadSummaryDTO> getUnassignedRequests(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return registrationRepository.findByAssignedMemberIsNull(pageable).map(this::toLeadSummaryDTO);
    }

    private TeamMemberDTO toTeamMemberDTO(AdminUser u) {
        long count = registrationRepository.countByAssignedMemberId(u.getId());
        return new TeamMemberDTO(
            u.getId(),
            u.getFullName(),
            u.getEmail(),
            u.getPhoneNumber(),
            u.getDisplayRole(),
            u.isActive(),
            count,
            u.getCreatedAt(),
            u.getUpdatedAt()
        );
    }

    private LeadSummaryDTO toLeadSummaryDTO(InterestRegistration r) {
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
            r.getLeadStatus() != null ? r.getLeadStatus() : com.carcrowd.entity.LeadStatus.JOINED,
            r.getCreatedAt(),
            r.getNextFollowUpDate(),
            r.getLastContactedAt(),
            assignedMemberId,
            assignedMemberName,
            assignedMemberEmail,
            assignedAt
        );
    }

    private LeadDetailDTO toLeadDetailDTO(InterestRegistration r) {
        String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
        String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
        Long carId = r.getCar() != null ? r.getCar().getId() : null;

        Long assignedMemberId = r.getAssignedMember() != null ? r.getAssignedMember().getId() : null;
        String assignedMemberName = r.getAssignedMember() != null ? r.getAssignedMember().getFullName() : null;
        String assignedMemberEmail = r.getAssignedMember() != null ? r.getAssignedMember().getEmail() : null;
        String assignedMemberPhone = r.getAssignedMember() != null ? r.getAssignedMember().getPhoneNumber() : null;
        LocalDateTime assignedAt = r.getAssignedAt();
        String assignedBy = r.getAssignedBy();

        List<LeadActivityDTO> activities = activityRepository
            .findByInterestRegistrationIdOrderByCreatedAtDesc(r.getId())
            .stream()
            .map(a -> new LeadActivityDTO(
                a.getId(),
                a.getInterestRegistrationId(),
                a.getAdminEmail(),
                a.getAdminName(),
                a.getActivityType(),
                a.getDescription(),
                a.getCreatedAt()
            ))
            .toList();

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
            r.getLeadStatus() != null ? r.getLeadStatus() : com.carcrowd.entity.LeadStatus.JOINED,
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
}
