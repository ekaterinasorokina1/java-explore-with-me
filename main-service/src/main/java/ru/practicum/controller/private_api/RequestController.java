package ru.practicum.controller.private_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "users")
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getEventRequestByUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getUserEventRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestByUserId(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @RequestBody @Valid EventRequestStatusUpdateRequest requests) {
        return requestService.updateUserEventRequests(userId, eventId, requests);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getAllRequestsByUserId(@PathVariable Long userId) {
        return requestService.getUserRequestsInOtherEvents(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createUserRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto deleteRequestByUserId(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
