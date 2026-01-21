package io.werescuecats.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
public class CatApiConfig {
    
    @Getter
    @Value("live_REPpK4lQGn2aP0UT1PNiZoX81otNElo3F3TWfqL9kdsOD2gnEM5bU7OSwZZwCJeK")
    private String apiKey;
    
    @Getter
    @Value("${catapi.base-url:https://api.thecatapi.com/v1}")
    private String baseUrl;
    
    @Value("${catapi.fetch-on-startup:true}")
    private boolean fetchOnStartup;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public boolean isFetchOnStartup() {
        return fetchOnStartup;
    }
}
