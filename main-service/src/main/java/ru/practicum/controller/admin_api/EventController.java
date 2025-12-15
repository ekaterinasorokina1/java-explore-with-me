package ru.practicum.controller.admin_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "admin/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEvents(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size, Sort.by("id").ascending())
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto getEvents(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventById(eventId, updateEventAdminRequest);
    }
}
