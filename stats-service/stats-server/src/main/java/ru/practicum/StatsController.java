package ru.practicum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsServerService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsServerService statsServerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public HitDto saveHit(@Valid @RequestBody HitDto hitDto) {
        return statsServerService.hit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                  LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                  LocalDateTime end,
                                  @RequestParam(required = false) List<String> uri,
                                  @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        return statsServerService.getStats(start, end, uri, unique);
    }

}
