package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class StatsDto {
    private String app;
    private String uri;
    private Long hits;
}
