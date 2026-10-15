package com.carcrowd.service;

import com.carcrowd.dto.admin.*;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.LeadActivityRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminLeadService {

    private static final Logger log = LoggerFactory.getLogger(AdminLeadService.class);
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InterestRegistrationRepository registrationRepository;
    private final LeadActivityRepository activityRepository;

    public AdminLeadService(InterestRegistrationRepository registrationRepository,
                            LeadActivityRepository activityRepository) {
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public Page<LeadSummaryDTO> getLeads(
            String search,
            String brand,
            String model,
            String city,
            String purchaseTimeline,
            LeadStatus status,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        return getLeads(search, brand, model, city, purchaseTimeline, status, startDate, endDate, null, null, page, size, sortBy, sortDirection);
    }

    @Transactional(readOnly = true)
    public Page<LeadSummaryDTO> getLeads(
            String search,
            String brand,
            String model,
            String city,
            String purchaseTimeline,
            LeadStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long assignedMemberId,
            Boolean unassigned,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<InterestRegistration> spec = buildSpecification(
            search, brand, model, city, purchaseTimeline, status, startDate, endDate, assignedMemberId, unassigned
        );

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(sortBy);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(direction, sortProperty));

        Page<InterestRegistration> pageResult = registrationRepository.findAll(spec, pageable);
        return pageResult.map(this::toSummaryDTO);
    }

    @Transactional(readOnly = true)
    public LeadDetailDTO getLeadById(Long id) {
        InterestRegistration lead = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer lead not found with ID: " + id));

        List<LeadActivityDTO> activities = activityRepository
            .findByInterestRegistrationIdOrderByCreatedAtDesc(id)
            .stream()
            .map(this::toActivityDTO)
            .toList();

        return toDetailDTO(lead, activities);
    }

    @Transactional
    public LeadDetailDTO updateLeadStatus(Long id, UpdateLeadStatusRequest request, String adminEmail, String adminName) {
        if (request.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        InterestRegistration lead = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer lead not found with ID: " + id));

        LeadStatus oldStatus = lead.getLeadStatus();
        validateTransition(oldStatus, request.getStatus());

        lead.setLeadStatus(request.getStatus());
        lead.setUpdatedAt(LocalDateTime.now());

        if (lead.getLastContactedAt() == null || request.getStatus() != LeadStatus.JOINED) {
            lead.setLastContactedAt(LocalDateTime.now());
        }

        if (request.getNote() != null && !request.getNote().isBlank()) {
            lead.setInternalNotes(request.getNote().trim());
        }

        registrationRepository.save(lead);

        String note = request.getNote() != null && !request.getNote().isBlank()
            ? " (" + request.getNote().trim() + ")"
            : "";
        String desc = "Status changed from " + (oldStatus != null ? oldStatus.name() : "N/A") + " to " + request.getStatus().name() + note;
        activityRepository.save(new LeadActivity(
            id,
            adminEmail,
            adminName,
            "STATUS_CHANGE",
            oldStatus != null ? oldStatus.name() : null,
            request.getStatus().name(),
            desc
        ));

        log.info("Lead {} status updated to {} by {}", id, request.getStatus(), adminEmail);
        return getLeadById(id);
    }

    private void validateTransition(LeadStatus current, LeadStatus target) {
        if (current == target && current != LeadStatus.CONTACTED) {
            throw new BadRequestException("Request is already in " + current.name() + " status.");
        }
        if (current == LeadStatus.BOUGHT) {
            throw new BadRequestException("Cannot update status: BOUGHT is a final completed terminal state.");
        }
        if (current == LeadStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status: CANCELLED is a terminal state.");
        }
        if (current == LeadStatus.WAITING) {
            if (target != LeadStatus.BOUGHT && target != LeadStatus.CANCELLED) {
                throw new BadRequestException("Invalid status transition from WAITING to " + target + ". From WAITING, status can be updated to BOUGHT or CANCELLED.");
            }
        } else if (current == LeadStatus.JOINED) {
            if (target != LeadStatus.CONTACTED && target != LeadStatus.WAITING && target != LeadStatus.BOUGHT && target != LeadStatus.CANCELLED) {
                throw new BadRequestException("Invalid status transition from " + current + " to " + target);
            }
        } else if (current == LeadStatus.CONTACTED) {
            if (target != LeadStatus.WAITING && target != LeadStatus.BOUGHT && target != LeadStatus.CANCELLED && target != LeadStatus.CONTACTED) {
                throw new BadRequestException("Invalid status transition from " + current + " to " + target);
            }
        }
    }

    @Transactional
    public LeadDetailDTO updateFollowUp(Long id, UpdateFollowUpRequest request, String adminEmail, String adminName) {
        InterestRegistration lead = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer lead not found with ID: " + id));

        StringBuilder activityDesc = new StringBuilder("Follow-up details updated: ");
        boolean updated = false;

        if (request.getNextFollowUpDate() != null) {
            lead.setNextFollowUpDate(request.getNextFollowUpDate());
            activityDesc.append("Next follow-up scheduled for ").append(request.getNextFollowUpDate()).append(". ");
            updated = true;
        }

        if (request.getLastContactedAt() != null) {
            lead.setLastContactedAt(request.getLastContactedAt());
            activityDesc.append("Last contacted marked at ").append(request.getLastContactedAt().format(ISO_DT)).append(". ");
            updated = true;
        }

        if (request.getInternalNotes() != null) {
            lead.setInternalNotes(request.getInternalNotes());
            activityDesc.append("Internal notes updated. ");
            updated = true;
        }

        registrationRepository.save(lead);

        if (updated) {
            activityRepository.save(new LeadActivity(id, adminEmail, adminName, "FOLLOW_UP_UPDATE", activityDesc.toString().trim()));
        }

        log.info("Lead {} follow-up details updated by {}", id, adminEmail);
        return getLeadById(id);
    }

    @Transactional
    public LeadActivityDTO addActivity(Long id, CreateActivityRequest request, String adminEmail, String adminName) {
        if (!registrationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer lead not found with ID: " + id);
        }

        if (!StringUtils.hasText(request.getActivityType())) {
            throw new BadRequestException("Activity type is required");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new BadRequestException("Activity description is required");
        }

        LeadActivity activity = new LeadActivity(
            id,
            adminEmail,
            adminName,
            request.getActivityType().trim().toUpperCase(),
            request.getDescription().trim()
        );

        LeadActivity saved = activityRepository.save(activity);
        log.info("Activity created for lead {} by {}: {}", id, adminEmail, request.getActivityType());
        return toActivityDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<LeadActivityDTO> getActivities(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer lead not found with ID: " + id);
        }
        return activityRepository.findByInterestRegistrationIdOrderByCreatedAtDesc(id)
            .stream()
            .map(this::toActivityDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public String exportLeadsToCsv(
            String search,
            String brand,
            String model,
            String city,
            String purchaseTimeline,
            LeadStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String sortBy,
            String sortDirection,
            String adminEmail
    ) {
        Specification<InterestRegistration> spec = buildSpecification(
            search, brand, model, city, purchaseTimeline, status, startDate, endDate
        );

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(sortBy);
        List<InterestRegistration> leads = registrationRepository.findAll(spec, Sort.by(direction, sortProperty));

        StringWriter sw = new StringWriter();
        // CSV Header
        sw.append("Lead ID,Full Name,Phone Number,Email,Brand,Model,City,Age,Timeline,Status,Registered At,Last Contacted At,Next Follow-up,Internal Notes\n");

        for (InterestRegistration lead : leads) {
            sw.append(String.valueOf(lead.getId())).append(",");
            sw.append(sanitizeCsv(lead.getFullName())).append(",");
            sw.append(sanitizeCsv(lead.getPhoneNumber())).append(",");
            sw.append(sanitizeCsv(lead.getEmail())).append(",");
            sw.append(sanitizeCsv(lead.getCar() != null && lead.getCar().getBrand() != null ? lead.getCar().getBrand().getName() : "")).append(",");
            sw.append(sanitizeCsv(lead.getCar() != null ? lead.getCar().getModelName() : "")).append(",");
            sw.append(sanitizeCsv(lead.getCity())).append(",");
            sw.append(lead.getAge() != null ? String.valueOf(lead.getAge()) : "").append(",");
            sw.append(sanitizeCsv(lead.getPurchaseTimeline())).append(",");
            sw.append(lead.getLeadStatus() != null ? lead.getLeadStatus().name() : "").append(",");
            sw.append(lead.getCreatedAt() != null ? lead.getCreatedAt().format(ISO_DT) : "").append(",");
            sw.append(lead.getLastContactedAt() != null ? lead.getLastContactedAt().format(ISO_DT) : "").append(",");
            sw.append(lead.getNextFollowUpDate() != null ? lead.getNextFollowUpDate().toString() : "").append(",");
            sw.append(sanitizeCsv(lead.getInternalNotes()));
            sw.append("\n");
        }

        log.info("Exported {} customer leads to CSV by admin: {}", leads.size(), adminEmail);
        return sw.toString();
    }

    private Specification<InterestRegistration> buildSpecification(
            String search,
            String brand,
            String model,
            String city,
            String purchaseTimeline,
            LeadStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return buildSpecification(search, brand, model, city, purchaseTimeline, status, startDate, endDate, null, null);
    }

    private Specification<InterestRegistration> buildSpecification(
            String search,
            String brand,
            String model,
            String city,
            String purchaseTimeline,
            LeadStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long assignedMemberId,
            Boolean unassigned
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search keyword across name, phone, email, and car model
            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate phoneMatch = cb.like(cb.lower(root.get("phoneNumber")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                Predicate modelMatch = cb.like(cb.lower(root.join("car", JoinType.LEFT).get("modelName")), pattern);
                predicates.add(cb.or(nameMatch, phoneMatch, emailMatch, modelMatch));
            }

            // Brand filter
            if (StringUtils.hasText(brand)) {
                predicates.add(cb.equal(
                    cb.lower(root.join("car", JoinType.LEFT).join("brand", JoinType.LEFT).get("name")),
                    brand.trim().toLowerCase()
                ));
            }

            // Model filter
            if (StringUtils.hasText(model)) {
                predicates.add(cb.equal(
                    cb.lower(root.join("car", JoinType.LEFT).get("modelName")),
                    model.trim().toLowerCase()
                ));
            }

            // City filter
            if (StringUtils.hasText(city)) {
                predicates.add(cb.equal(
                    cb.lower(root.get("city")),
                    city.trim().toLowerCase()
                ));
            }

            // Purchase timeline filter
            if (StringUtils.hasText(purchaseTimeline)) {
                predicates.add(cb.equal(
                    cb.lower(root.get("purchaseTimeline")),
                    purchaseTimeline.trim().toLowerCase()
                ));
            }

            // Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("leadStatus"), status));
            }

            // Assigned Member filter
            if (Boolean.TRUE.equals(unassigned)) {
                predicates.add(cb.isNull(root.get("assignedMember")));
            } else if (assignedMemberId != null) {
                predicates.add(cb.equal(root.get("assignedMember").get("id"), assignedMemberId));
            }

            // Date range filter
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String resolveSortProperty(String sortBy) {
        if (!StringUtils.hasText(sortBy)) {
            return "createdAt";
        }
        return switch (sortBy.toLowerCase()) {
            case "name", "fullname" -> "fullName";
            case "city" -> "city";
            case "status", "leadstatus" -> "leadStatus";
            default -> "createdAt";
        };
    }

    /**
     * Sanitizes CSV values against formula injection (=, +, -, @) and quotes strings containing commas/quotes.
     */
    private String sanitizeCsv(String val) {
        if (val == null) {
            return "\"\"";
        }
        String clean = val.trim();
        // Prevent formula injection
        if (clean.startsWith("=") || clean.startsWith("+") || clean.startsWith("-") || clean.startsWith("@")) {
            clean = "'" + clean;
        }
        // Escape quotes
        clean = clean.replace("\"", "\"\"");
        return "\"" + clean + "\"";
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
