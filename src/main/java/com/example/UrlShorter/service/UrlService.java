package com.example.UrlShorter.service;


import com.example.UrlShorter.model.UrlMapping;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UrlService {

    public UrlMapping createUrl(String originalUrl);

    public Optional<UrlMapping> retriveUrl(String shortUrl);

    public Optional<UrlMapping> updateShortUrl(String shortUrl, String originalUrl);

    public boolean deleteUrl(String shortUrl);

    public Optional<UrlMapping> getStatisticUrl(String shortUrl);

}
