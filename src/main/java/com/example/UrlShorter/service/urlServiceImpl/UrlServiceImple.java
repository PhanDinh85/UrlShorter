package com.example.UrlShorter.service.urlServiceImpl;

import com.example.UrlShorter.model.UrlMapping;
import com.example.UrlShorter.repository.UrlRepository;
import com.example.UrlShorter.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlServiceImple implements UrlService {

    private static final String DOMAIN = "https://bit.ly/";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;
    private final UrlRepository urlRepository;

    @Override
    @Transactional
    public UrlMapping createUrl(String originalUrl) {

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortCode(shortenUrl());
        urlMapping.setOriginalUrl(originalUrl);
        return urlRepository.save(urlMapping);
    }

    @Override
    @Transactional
    public Optional<UrlMapping> retriveUrl(String shortUrl) {

        Optional<UrlMapping> optional = urlRepository.findById(shortUrl);
        if (optional.isPresent()) {

            UrlMapping urlMapping = optional.get();
            urlMapping.setAccessAccount(urlMapping.getAccessAccount() + 1);
            return Optional.of(urlRepository.save(urlMapping));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UrlMapping> updateShortUrl(String shortUrl, String originalUrl) {

        Optional<UrlMapping> existingUrl = urlRepository.findById(shortUrl);
        if (existingUrl.isPresent()) {
            UrlMapping mapping = existingUrl.get();

            if (shortUrl != null) {
                mapping.setShortCode(shortUrl);
                return Optional.of(urlRepository.save(mapping));
            } else if (originalUrl != null) {
                mapping.setOriginalUrl(originalUrl);
                return Optional.of(urlRepository.save(mapping));
            } else if (shortUrl != null && originalUrl != null) {
                mapping.setOriginalUrl(originalUrl);
                mapping.setShortCode(shortUrl);
                return Optional.of(urlRepository.save(mapping));
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteUrl(String shortUrl) {
        Optional<UrlMapping> existingUrl = urlRepository.findById(shortUrl);
        if (existingUrl.isPresent()) {
            UrlMapping mapping = existingUrl.get();
            urlRepository.deleteById(mapping.getShortCode());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UrlMapping> getStatisticUrl(String shortCode) {
        return urlRepository.findById(shortCode);
    }

    private static String shortenUrl() {

        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
        String encoded = base62Encode(uuidStr.getBytes());

        //Get first 6 charecters
        String shortUrl = encoded.substring(0, 6);
        return DOMAIN + shortUrl;
    }

    private static String base62Encode(byte[] input) {
        StringBuilder result = new StringBuilder();

        for (byte b : input) {
            int index = b & 0xFF;
            result.append(CHARACTERS.charAt(index % CHARACTERS.length()));
        }

        return result.toString();
    }
}
