package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class StatsClient {
    private final RestClient restClient;
    @Value("${stats-server.url}")
    private String serverUrl;

    @Value("${app.name}")
    private String appName;
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public StatsClient() {
        restClient = RestClient.builder().build();
    }

    public ResponseEntity<HitDto> hit(HttpServletRequest request) {
        String url = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/hit")
                .build()
                .toUriString();

        HitDto hitDto = new HitDto();
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.now());
        hitDto.setApp(appName);

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

        String formattedStart = DateTimeFormatter.ofPattern(dateFormat).format(start);
        String formattedEnd = DateTimeFormatter.ofPattern(dateFormat).format(end);

        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", formattedStart)
                .queryParam("end", formattedEnd);

        if (uris != null && !uris.isEmpty()) {
            urlBuilder.queryParam("uris", uris);
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
