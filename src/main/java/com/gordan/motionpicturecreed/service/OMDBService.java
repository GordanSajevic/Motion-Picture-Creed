package com.gordan.motionpicturecreed.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OMDBService {
	
	private final RestTemplate restTemplate;

    public OMDBService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getData(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }
}
