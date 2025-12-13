package ru.practicum.mapper;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequestor().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        dto.setEvent(request.getEvent().getId());
        return dto;
    }

    public static Request toRequest(User user, Event event) {
        Request request = new Request();
        request.setRequestor(user);
        request.setEvent(event);
        return request;
    }
}
