package com.learnnest.lms.domain.repository.auth;

import com.learnnest.lms.domain.model.auth.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserAccount> findAllByRoleOrderByCreatedAtDesc(String role);
}
