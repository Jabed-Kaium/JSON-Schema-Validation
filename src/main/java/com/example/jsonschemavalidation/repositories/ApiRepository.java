package com.example.jsonschemavalidation.repositories;

import com.example.jsonschemavalidation.models.ApiDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiRepository extends JpaRepository<ApiDetails, Long> {
    Optional<ApiDetails> findByApiIdentifier(Long apiIdentifier);
}
