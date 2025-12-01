package ru.practicum.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerRepository extends CrudRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.dto.StatsDto(h.app, h.uri, count(h.ip))  " +
            "from EndpointHit as h " +
            "where (h.timestamp between ?1 and ?2) and (?3 is null or h.uri in ?3)" +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC")
    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.dto.StatsDto(h.app, h.uri, count(DISTINCT h.ip))  " +
            "from EndpointHit as h " +
            "where (h.timestamp between ?1 and ?2) and (?3 is null or h.uri in ?3)" +
            "group by h.app, h.uri " +
            "order BY count(DISTINCT h.ip) DESC")
    List<StatsDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uri);
}
