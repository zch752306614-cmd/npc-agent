package com.npcagent.config;

import com.npcagent.model.StoryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class StoryDataConfig {

    @Bean
    public StoryData storyData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/story-data.json")) {
            return objectMapper.readValue(inputStream, StoryData.class);
        }
    }
}