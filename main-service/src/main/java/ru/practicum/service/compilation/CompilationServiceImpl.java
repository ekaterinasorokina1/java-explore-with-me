package ru.practicum.service.compilation;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null) {
            dto.getEvents().forEach(event -> events.add(getEvent(event)));
        }
        Compilation compilation = CompilationMapper.toCompilation(dto, events);

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventDto(events));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = getCompilation(compId);
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null) {
            dto.getEvents().forEach(event -> events.add(getEvent(event)));
        }
        CompilationMapper.toUpdatedCompilation(compilation, dto, events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventDto(events));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        if (pinned == null) {
            return StreamSupport.stream(compilationRepository.findAll(pageable).spliterator(), false)
                    .map((c) -> CompilationMapper.toCompilationDto(c, getEventDto(c.getEvents()))).toList();
        }
        return compilationRepository.findByPinned(pinned, pageable).stream()
                .map((c) -> CompilationMapper.toCompilationDto(c, getEventDto(c.getEvents()))).toList();

    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilation(compId);

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventDto(compilation.getEvents()));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        getCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборока с id = " + compId + " не найдена"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private List<EventShortDto> getEventDto(Set<Event> events) {
        return events.stream()
                .map((e) -> EventMapper.fromEntityToShortDto(e, getEventView(e.getId()))).toList();
    }

    private long getEventView(long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        List<StatsDto> stats = statsClient.getStats(
                start,
                end,
                uris,
                true
        ).getBody();

        return stats == null || stats.isEmpty() ? 0L : stats.getFirst().getHits();
    }
}
