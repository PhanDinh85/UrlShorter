package com.example.UrlShorter.repository;

import com.example.UrlShorter.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, String> {

    Optional<UrlMapping> findByShortCode(String shortCode);
}
