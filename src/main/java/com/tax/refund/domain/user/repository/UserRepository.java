package com.tax.refund.domain.user.repository;

import com.tax.refund.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByRegNo(String regNo);
    Optional<User> findByUserId(String userId);

}
