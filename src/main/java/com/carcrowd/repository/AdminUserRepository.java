package com.carcrowd.repository;

import com.carcrowd.entity.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT COUNT(u) FROM AdminUser u WHERE (u.role = 'ROLE_ADMIN' OR u.role = 'ADMIN') AND u.active = true")
    long countActiveAdmins();

    @Query("""
        SELECT u FROM AdminUser u
        WHERE (:search IS NULL OR :search = ''
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR (u.phoneNumber IS NOT NULL AND LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))))
          AND (:active IS NULL OR u.active = :active)
    """)
    Page<AdminUser> searchMembers(
        @Param("search") String search,
        @Param("active") Boolean active,
        Pageable pageable
    );

    List<AdminUser> findByActiveTrueOrderByFullNameAsc();
}
