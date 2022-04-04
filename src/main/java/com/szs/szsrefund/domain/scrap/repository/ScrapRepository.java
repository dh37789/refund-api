package com.szs.szsrefund.domain.scrap.repository;

import com.szs.szsrefund.domain.scrap.entity.ScrapUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<ScrapUser, Long> {
    Optional<ScrapUser> findByRegNo(String regNo);
}
