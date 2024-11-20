package com.example.UrlShorter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String shortCode;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", message = "Invalid URL format")
    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "access_count")
    private int accessAccount;

    public UrlMapping() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accessAccount = 0;
    }
}
