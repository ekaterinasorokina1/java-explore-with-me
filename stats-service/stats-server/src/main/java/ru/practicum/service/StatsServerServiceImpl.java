package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StatsServerServiceImpl implements StatsServerService {
    private final StatsServerRepository statsServerRepository;

    @Transactional
    @Override
    public HitDto hit(HitDto hitDto) {
        EndpointHit endpointHit = statsServerRepository.save(EndpointHitMapper.toEntity(hitDto));
        return EndpointHitMapper.toHitDto(endpointHit);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique) {
        if (unique != null && unique) {
            return statsServerRepository.getStatsUnique(start, end, uri);
        }
        return statsServerRepository.getStats(start, end, uri);
    }
}
