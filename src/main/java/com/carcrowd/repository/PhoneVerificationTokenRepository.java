package com.carcrowd.repository;

import com.carcrowd.entity.PhoneVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneVerificationTokenRepository extends JpaRepository<PhoneVerificationToken, Long> {

    Optional<PhoneVerificationToken> findTopByUserIdAndPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(
        Long userId, String phoneNumber
    );

    Optional<PhoneVerificationToken> findTopByUserIdAndVerifiedFalseOrderByCreatedAtDesc(Long userId);

    List<PhoneVerificationToken> findByUserId(Long userId);
}
