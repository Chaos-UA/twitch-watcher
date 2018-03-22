package com.twitch.watcher.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitch.watcher.bot.dto.SearchStreamsResponseDTO;
import com.twitch.watcher.bot.dto.StreamDTO;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TwitchService {

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    public List<StreamDTO> searchForAllStreams(String gameName, String clientId) {
        List<StreamDTO> streams = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            SearchStreamsResponseDTO response = searchStreams(gameName, clientId, i, 100);
            if (response.getStreams().isEmpty()) {
                break;
            }
            streams.addAll(response.getStreams());
        }
        return streams;
    }

    public SearchStreamsResponseDTO searchStreams(String gameName, String clientId, int page, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.twitch.tv/kraken/streams")
                .queryParam("limit", limit)
                .queryParam("offset", calculateOffset(page, limit))
                .queryParam("game", gameName)
                .queryParam("broadcaster_language", "")
                .queryParam("on_site", "1")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("client-id", clientId);

        ResponseEntity<SearchStreamsResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                SearchStreamsResponseDTO.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error has occurred. Invalid status code: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private static int calculateOffset(int page, int limit) {
        return (page - 1) * limit;
    }
}
