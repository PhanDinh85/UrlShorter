package com.example.UrlShorter.controller;

import com.example.UrlShorter.dto.request.UpdateShortUrlDto;
import com.example.UrlShorter.model.UrlMapping;
import com.example.UrlShorter.service.urlServiceImpl.UrlServiceImple;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class URLShorterController {

    private final UrlServiceImple serviceImple;

    @PostMapping("shorten")
    public ResponseEntity<?> createShortUrl(@Valid @RequestBody UrlMapping originMapping, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            UrlMapping savedMapping = serviceImple.createUrl(originMapping.getOriginalUrl());
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedMapping.getId());
            response.put("url", savedMapping.getOriginalUrl());
            response.put("shortCode", savedMapping.getShortCode());
            response.put("createdAt", savedMapping.getCreatedAt());
            response.put("updatedAt", savedMapping.getUpdatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating short URL: " + e.getMessage());
        }
    }

    @GetMapping("shorten")
    public ResponseEntity<?> retrieveShortUrl(@RequestParam(name = "shortcode") String shortcode) {

        Optional<UrlMapping> urlMapping = serviceImple.retriveUrl(shortcode);
        if (urlMapping.isPresent()) {

            UrlMapping mapping = urlMapping.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", mapping.getId());
            response.put("url", mapping.getOriginalUrl());
            response.put("shortCode", mapping.getShortCode());
            response.put("createdAt", mapping.getCreatedAt());
            response.put("updatedAt", mapping.getUpdatedAt());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("shorten/{shortcode}")
    public ResponseEntity<?> UpdateShortUr(@PathVariable(name = "shortcode") String shortCode, @Valid @RequestBody UpdateShortUrlDto updateShortUrlDto) {

        try {
            Optional<UrlMapping> updatedUrlMapping = serviceImple.updateShortUrl(shortCode, updateShortUrlDto.getOriginalUrl());
            return new ResponseEntity<>(updatedUrlMapping, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("shorten/{shortcode}")
    public ResponseEntity<?> DeleteShortUr(@PathVariable(name = "shortcode") String shortCode) {

        if (serviceImple.deleteUrl(shortCode)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("shorten/stats")
    public ResponseEntity<?> statisticsShortUr(@RequestParam(name = "shortcode") String shortCode) {

        Optional<UrlMapping> urlMapping = serviceImple.getStatisticUrl(shortCode);
        if (urlMapping.isPresent()) {
            return new ResponseEntity<>(urlMapping.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}