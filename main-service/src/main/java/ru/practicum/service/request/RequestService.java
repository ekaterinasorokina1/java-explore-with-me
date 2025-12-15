package ru.practicum.service.request;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;

import java.util.List;

public interface RequestService {
    public List<RequestDto> getUserRequestsInOtherEvents(Long userId);

    public RequestDto createRequest(Long userId, Long eventId);

    public RequestDto cancelRequest(Long userId, Long requestId);

    public List<RequestDto> getUserEventRequest(Long userId, Long eventId);

    public EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest requests);
}
