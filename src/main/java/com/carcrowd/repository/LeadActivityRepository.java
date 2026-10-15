package com.carcrowd.repository;

import com.carcrowd.entity.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {

    List<LeadActivity> findByInterestRegistrationIdOrderByCreatedAtDesc(Long interestRegistrationId);

    List<LeadActivity> findByInterestRegistrationIdOrderByCreatedAtAsc(Long interestRegistrationId);
}
