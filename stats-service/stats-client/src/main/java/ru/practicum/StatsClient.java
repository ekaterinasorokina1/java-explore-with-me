package ru.practicum;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsClient {
    private final RestClient restClient;
    @Value("${stats-server.url}")
    private String serverUrl;

    public StatsClient() {
        restClient = RestClient.builder().build();
    }

    public ResponseEntity<HitDto> hit(HitDto hitDto) {
        String url = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/hit")
                .build()
                .toUriString();

        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(HitDto.class);
    }

    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new ValidationException("Нужно указать дату начала и окончания");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Нужно указать дату начала до даты окончания");
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end);

        if (uris != null && !uris.isEmpty()) {
            urlBuilder.queryParam("uris", uris.toArray());
        }
        if (unique != null) {
            urlBuilder.queryParam("unique", unique);
        }

        String url = urlBuilder.build()
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }
}
