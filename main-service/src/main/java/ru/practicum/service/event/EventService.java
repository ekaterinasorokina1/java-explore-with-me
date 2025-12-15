package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    public List<EventFullDto> getUsersEvents(Long userId, Pageable pageable);

    public EventFullDto createNewUserEvent(Long userId, NewEventDto newEventDto);

    public EventFullDto getUserEventById(Long userId, Long eventId);

    public EventFullDto updateUserEventById(Long userId, Long eventId,
                                            UpdateEventUserRequest updateEventUserRequest);

    public List<EventFullDto> getAllEvents(List<Long> userIds,
                                           List<String> states,
                                           List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Pageable pageable);

    public EventFullDto updateEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories,
                                                  Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                  String sort, int from, int size,
                                                  HttpServletRequest httpServletRequest);

    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);
}
