package com.carcrowd.repository;

import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterestRegistrationRepository extends JpaRepository<InterestRegistration, Long>, JpaSpecificationExecutor<InterestRegistration> {

    long countByCarId(Long carId);

    @Query("SELECT COUNT(r) FROM InterestRegistration r WHERE r.car.id = :carId AND (r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED)")
    long countActiveByCarId(@org.springframework.data.repository.query.Param("carId") Long carId);

    long countByCarIdAndLeadStatusNot(Long carId, LeadStatus leadStatus);

    boolean existsByCarIdAndPhoneNumber(Long carId, String phoneNumber);

    boolean existsByCarIdAndEmail(Long carId, String email);

    boolean existsByCarIdAndUserId(Long carId, Long userId);

    java.util.Optional<InterestRegistration> findByCarIdAndUserId(Long carId, Long userId);

    java.util.Optional<InterestRegistration> findByCarIdAndPhoneNumber(Long carId, String phoneNumber);

    java.util.Optional<InterestRegistration> findByCarIdAndEmail(Long carId, String email);

    List<InterestRegistration> findByUserIdOrderByCreatedAtDesc(Long userId);

    java.util.Optional<InterestRegistration> findByIdAndUserId(Long id, Long userId);

    long countByLeadStatus(LeadStatus leadStatus);

    long countByPurchaseTimeline(String purchaseTimeline);

    @Query("SELECT COUNT(DISTINCT r.car.id) FROM InterestRegistration r WHERE r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED")
    long countDistinctCarsWithInterest();

    @Query("SELECT r.city, COUNT(r) FROM InterestRegistration r WHERE r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED GROUP BY r.city ORDER BY COUNT(r) DESC")
    List<Object[]> countRegistrationsByCity();

    List<InterestRegistration> findTop10ByOrderByCreatedAtDesc();

    long countByCarIdAndCreatedAtAfter(Long carId, LocalDateTime since);

    @Query("SELECT COUNT(r) FROM InterestRegistration r WHERE r.car.id = :carId AND r.createdAt >= :since AND (r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED)")
    long countActiveByCarIdAndCreatedAtAfter(@org.springframework.data.repository.query.Param("carId") Long carId, @org.springframework.data.repository.query.Param("since") LocalDateTime since);

    @Query("SELECT r.city, COUNT(r) FROM InterestRegistration r WHERE r.car.id = :carId AND (r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED) GROUP BY r.city ORDER BY COUNT(r) DESC")
    List<Object[]> countCityDemandForCar(@org.springframework.data.repository.query.Param("carId") Long carId);

    @Query("SELECT r.car.id, r.car.brand.name, r.car.modelName, COUNT(r) FROM InterestRegistration r WHERE r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED GROUP BY r.car.id, r.car.brand.name, r.car.modelName ORDER BY COUNT(r) DESC")
    List<Object[]> findPopularCarsAggregated();

    @Query("SELECT COUNT(r) FROM InterestRegistration r WHERE (r.leadStatus IS NULL OR r.leadStatus <> com.carcrowd.entity.LeadStatus.CANCELLED) AND (LOWER(r.purchaseTimeline) LIKE '%immediate%' OR LOWER(r.purchaseTimeline) LIKE '%15 days%' OR LOWER(r.purchaseTimeline) LIKE '%30 days%' OR LOWER(r.purchaseTimeline) LIKE '%< 1 month%')")
    long countBuyingWithin30Days();

    long countByAssignedMemberId(Long memberId);

    long countByAssignedMemberIsNull();

    java.util.Optional<InterestRegistration> findByIdAndAssignedMemberId(Long id, Long memberId);

    org.springframework.data.domain.Page<InterestRegistration> findByAssignedMemberId(Long memberId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<InterestRegistration> findByAssignedMemberIsNull(org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE InterestRegistration r SET r.assignedMember = :newMember, r.assignedAt = :assignedAt, r.assignedBy = :assignedBy WHERE r.assignedMember.id = :oldMemberId")
    int reassignAll(
        @org.springframework.data.repository.query.Param("oldMemberId") Long oldMemberId,
        @org.springframework.data.repository.query.Param("newMember") com.carcrowd.entity.AdminUser newMember,
        @org.springframework.data.repository.query.Param("assignedAt") LocalDateTime assignedAt,
        @org.springframework.data.repository.query.Param("assignedBy") String assignedBy
    );

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE InterestRegistration r SET r.assignedMember = null, r.assignedAt = null, r.assignedBy = null WHERE r.assignedMember.id = :oldMemberId")
    int unassignAll(@org.springframework.data.repository.query.Param("oldMemberId") Long oldMemberId);
}
