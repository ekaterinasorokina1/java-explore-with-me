package ru.practicum.mapper;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.model.Event;

public class EventMapper {
    public static Event eventDtoToEntity(NewEventDto newEventDto) {
        Event event = new Event();
        event.setDescription(newEventDto.getDescription());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setEventDate(newEventDto.getEventDate());
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto fromEntityToFullDto(Event event, long views) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setLocation(LocationMapper.toLocation(event.getLat(), event.getLon()));
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setViews(views);
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        return dto;
    }

    public static EventShortDto fromEntityToShortDto(Event event, long views) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(views);
        dto.setAnnotation(event.getAnnotation());
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        return dto;
    }

    public static Event updateToEvent(Event event, UpdateEventUserRequest dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        return event;
    }
}
