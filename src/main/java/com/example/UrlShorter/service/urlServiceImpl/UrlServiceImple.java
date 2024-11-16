package com.example.UrlShorter.service.urlServiceImpl;

import com.example.UrlShorter.model.UrlMapping;
import com.example.UrlShorter.repository.UrlRepository;
import com.example.UrlShorter.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

//        String result = shortUrl.replace("https://bit.ly/", "");
        Optional<UrlMapping> optional = urlRepository.findByShortCode(shortUrl);
        if (optional.isPresent()) {

            UrlMapping urlMapping = optional.get();
            urlMapping.setAccessAccount(urlMapping.getAccessAccount() + 1);
            return Optional.of(urlRepository.save(urlMapping));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UrlMapping> updateShortUrl(String shortUrl, String newUrl) {

        Optional<UrlMapping> optionalUrlMapping = urlRepository.findByShortCode("https://bit.ly/" + shortUrl);

        if (optionalUrlMapping.isEmpty()) {
            throw new RuntimeException("Short URL not found: " + shortUrl);
        }

        UrlMapping urlMapping = optionalUrlMapping.get();
        urlMapping.setOriginalUrl(newUrl);
        urlMapping.setUpdatedAt(LocalDateTime.now());

        return Optional.of(urlRepository.save(urlMapping));
    }

    @Override
    @Transactional
    public boolean deleteUrl(String shortUrl) {
        String sLink= "https://bit.ly/" + shortUrl;
        Optional<UrlMapping> existingUrl = urlRepository.findByShortCode(sLink);
        if (existingUrl.isPresent()) {
            UrlMapping mapping = existingUrl.get();
            urlRepository.deleteById(String.valueOf(mapping.getId()));
            return true;
        }else{
            return false;
        }
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
