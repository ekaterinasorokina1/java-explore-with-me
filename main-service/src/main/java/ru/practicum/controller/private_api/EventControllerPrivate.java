package ru.practicum.controller.private_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "users")
public class EventControllerPrivate {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getAllEventsByUserId(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return eventService.getUsersEvents(userId, PageRequest.of(from / size, size));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByUserId(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createNewUserEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUserId(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateUserEventById(userId, eventId, updateEventUserRequest);
    }
}
