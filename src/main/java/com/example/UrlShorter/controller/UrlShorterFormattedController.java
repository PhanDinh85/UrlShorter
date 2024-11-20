package com.example.UrlShorter.controller;

import com.example.UrlShorter.dto.request.UpdateShortUrlDto;
import com.example.UrlShorter.dto.response.BaseResponse;
import com.example.UrlShorter.dto.response.UrlResponse;
import com.example.UrlShorter.model.UrlMapping;
import com.example.UrlShorter.service.urlServiceImpl.UrlServiceImple;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v2/shorten/")
@RequiredArgsConstructor
public class UrlShorterFormattedController {

    private final UrlServiceImple serviceImple;
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String NOT_FOUND = "Short URL not found";


    @PostMapping()
    public ResponseEntity<BaseResponse<?>> createShortUrl(@Valid @RequestBody UrlMapping originMapping, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(new BaseResponse<>(ERROR, 400, "Validation error", errors));
        }

        try {
            UrlMapping savedMapping = serviceImple.createUrl(originMapping.getOriginalUrl());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(SUCCESS, 201, "URL shortened successfully", savedMapping));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(ERROR, 500, "Error creating short URL: " + e.getMessage(), null));
        }
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<?>> retrieveShortUrl(@RequestParam(name = "shortcode") String shortcode) {
        Optional<UrlMapping> urlMapping = serviceImple.retriveUrl(shortcode);

        if (urlMapping.isPresent()) {
            UrlMapping url = urlMapping.get();
            UrlResponse result = UrlResponse.builder()
                    .id(url.getId())
                    .shortCode(url.getShortCode())
                    .originalUrl(url.getOriginalUrl())
                    .createdAt(url.getCreatedAt())
                    .updatedAt(url.getUpdatedAt())
                    .build();
            return ResponseEntity.ok(new BaseResponse<>(SUCCESS, 200, "URL retrieved successfully", result));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(ERROR, 404, NOT_FOUND, null));
        }
    }

    @PutMapping()
    public ResponseEntity<BaseResponse<?>> updateShortUrl(@RequestParam(name = "shortcode") String shortCode, @Valid @RequestBody UpdateShortUrlDto updateShortUrlDto) {
        try {
            Optional<UrlMapping> updatedUrlMapping = serviceImple.updateShortUrl(shortCode, updateShortUrlDto.getOriginalUrl());

            if (updatedUrlMapping.isPresent()) {
                return ResponseEntity.ok(new BaseResponse<>(SUCCESS, 200, "URL updated successfully", updatedUrlMapping.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse<>(ERROR, 404, NOT_FOUND, null));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(ERROR, 500, "Error updating short URL: " + e.getMessage(), null));
        }
    }

    @DeleteMapping()
    public ResponseEntity<BaseResponse<?>> deleteShortUrl(@RequestParam(name = "shortcode") String shortCode) {
        if (serviceImple.deleteUrl(shortCode)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new BaseResponse<>(SUCCESS, 204, "URL deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(ERROR, 404, NOT_FOUND, null));
        }
    }

    @GetMapping("stats")
    public ResponseEntity<BaseResponse<?>> statisticsShortUrl(@RequestParam(name = "shortcode") String shortCode) {
        Optional<UrlMapping> urlMapping = serviceImple.getStatisticUrl(shortCode);

        if (urlMapping.isPresent()) {
            return ResponseEntity.ok(new BaseResponse<>(SUCCESS, 200, "Statistics retrieved successfully", urlMapping.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(ERROR, 404, NOT_FOUND, null));
        }
    }

    @PostMapping("fake-data")
    public ResponseEntity<BaseResponse<?>> fakeData(@RequestParam(name = "number") int number) {

        Faker faker = new Faker();
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            String domainName = faker.internet().domainName();
            String path = faker.internet().slug();
            String query = "id=" + faker.number().randomDigit() + "&page=" + faker.number().randomDigit();
            String url = "https://" + domainName + "/" + path + "?" + query;
            urls.add(url);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>("Created", 200, "Create list successfully", serviceImple.createListUrl(urls)));
    }
}
