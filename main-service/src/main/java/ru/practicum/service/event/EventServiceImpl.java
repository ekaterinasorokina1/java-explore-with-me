package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    @Value("${app.name}")
    private String appName;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> getUsersEvents(Long userId, Pageable pageable) {
        getUser(userId);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream().map((e) -> EventMapper.fromEntityToFullDto(e, getEventView(e.getId()))).toList();
    }


    @Override
    @Transactional
    public EventFullDto createNewUserEvent(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.eventDtoToEntity(newEventDto);
        event.setCategory(getCategory(newEventDto.getCategory()));
        event.setInitiator(getUser(userId));
        return EventMapper.fromEntityToFullDto(eventRepository.save(event), 0);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        getUser(userId);
        return EventMapper.fromEntityToFullDto(
                eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Событие не найдено")),
                getEventView(eventId));
    }

    @Override
    @Transactional
    public EventFullDto updateUserEventById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        getUser(userId);
        Event event = getEvent(eventId);

        validateUserEventOnUpdate(event, updateEventUserRequest);
        EventMapper.updateToEvent(event, updateEventUserRequest);
        updateCategory(event, updateEventUserRequest.getCategory());
        updateUserStateAction(event, updateEventUserRequest.getStateAction());
        return EventMapper.fromEntityToFullDto(eventRepository.save(event), getEventView(eventId));
    }

    @Override
    public List<EventFullDto> getAllEvents(List<Long> userIds,
                                           List<String> states,
                                           List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Pageable pageable
    ) {
        List<EventState> eventStates = states == null ? null : states.stream().map(EventState::valueOf).toList();
        rangeStart = getRangeStart(rangeStart);
        rangeEnd = getRangeEnd(rangeEnd);
        validateDateRange(rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAllEvents(userIds, eventStates, categories, rangeStart, rangeEnd, pageable);

        return events.stream().map((e) -> EventMapper.fromEntityToFullDto(e, getEventView(e.getId()))).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEvent(eventId);
        validateAdminEventStateOnUpdate(event, updateEventAdminRequest);
        validateAdminEventDateOnUpdate(event, updateEventAdminRequest);
        EventMapper.updateToEvent(event, updateEventAdminRequest);
        updateCategory(event, updateEventAdminRequest.getCategory());
        updateAdminStateAction(event, updateEventAdminRequest.getStateAction());
        return EventMapper.fromEntityToFullDto(eventRepository.save(event), getEventView(eventId));
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        statsClient.hit(httpServletRequest);
        long views = getEventView(eventId);
        return EventMapper.fromEntityToFullDto(event, views);
    }

    @Override
    public List<EventShortDto> getPublishedEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort, int from, int size,
                                                  HttpServletRequest httpServletRequest) {

        Pageable pageable = getPageable(sort, from, size);
        text = text == null ? null : text.toLowerCase();
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? getRangeEnd(rangeEnd) : rangeEnd;
        validateDateRange(rangeStart, rangeEnd);
        List<Event> events = eventRepository.findPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        statsClient.hit(httpServletRequest);
        return events.stream().map((e) -> EventMapper.fromEntityToShortDto(e, getEventView(e.getId()))).toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findCategoryById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private void validateUserEventOnUpdate(Event event, UpdateEventUserRequest updateRequest) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента ");
        }
    }

    private void validateAdminEventStateOnUpdate(Event event, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getStateAction() == null) {
            return;
        }
        if (updateRequest.getStateAction().name().equals(EventStateAction.REJECT_EVENT.name())
                && event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
        }
        if (updateRequest.getStateAction().name().equals(EventStateAction.PUBLISH_EVENT.name())
                && !event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
    }

    private void validateAdminEventDateOnUpdate(Event event, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getEventDate() == null) {
            return;
        }
        if (event.getEventDate().minusHours(1L).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
    }

    private void updateUserStateAction(Event event, EventStateAction action) {
        if (action == null) {
            return;
        }
        if (action.equals(EventStateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        } else if (action.equals(EventStateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        }

    }

    private void updateAdminStateAction(Event event, EventStateAction action) {
        if (action == null) {
            return;
        }
        if (action.equals(EventStateAction.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (action.equals(EventStateAction.REJECT_EVENT)) {
            event.setState(EventState.CANCELED);
        }
        eventRepository.save(event);
    }

    private void updateCategory(Event event, Long catId) {
        if (event.getCategory().getId().equals(catId)) {
            Category category = getCategory(catId);
            event.setCategory(category);
        }
    }

    private Pageable getPageable(String sort, int from, int size) {
        if (sort == null) {
            return PageRequest.of(from / size, size, Sort.by("id").ascending());
        }
        if (sort.equals("VIEWS")) {
            return PageRequest.of(from / size, size, Sort.by("views").ascending());
        }
        if (sort.equals("EVENT_DATE")) {
            return PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        }
        throw new ValidationException("Указан некорректный вариант сортировки");
    }

    private LocalDateTime getRangeStart(LocalDateTime rangeStart) {
        return rangeStart == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : rangeStart;
    }

    private LocalDateTime getRangeEnd(LocalDateTime rangeEnd) {
        return rangeEnd == null ? LocalDateTime.of(2970, 1, 1, 0, 0, 0) : rangeEnd;
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Указаны некорректные даты");
        }
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
