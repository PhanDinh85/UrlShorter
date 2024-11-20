package com.example.UrlShorter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UrlResponse {

    private long id;

    private String shortCode;

    private String originalUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
