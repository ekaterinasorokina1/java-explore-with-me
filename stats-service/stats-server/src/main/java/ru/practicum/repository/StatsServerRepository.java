package ru.practicum.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsServerRepository extends CrudRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.dto.StatsDto(h.app, h.uri, COUNT(h.ip))  " +
            "FROM EndpointHit as h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) AND (?3 IS NULL OR h.uri IN ?3)" +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.dto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))  " +
            "FROM EndpointHit as h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) AND (?3 IS NULL OR h.uri IN ?3)" +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<StatsDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uri);
}
