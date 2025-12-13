package ru.practicum.service.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getUserRequestsInOtherEvents(Long userId) {
        getUser(userId);
        return requestRepository.findByRequestorId(userId).stream()
                .map(RequestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateRequest(userId, event);

        Request request = RequestMapper.toRequest(user, event);
        request.setStatus(getRequestStatus(event));
        updateEventConfirmedRequests(request, event);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = getRequest(requestId);
        if (!request.getRequestor().getId().equals(userId)) {
            throw new ConflictException("инициатор запроса не найден");
        }
        RequestStatus status = request.getStatus();
        request.setStatus(RequestStatus.CANCELED);
        updateConfirmedRequestsOnCanceled(status, request.getEvent());
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getUserEventRequest(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем события");
        }
        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequests) {
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("инициатор события не найден");
        }
        List<Request> requests = requestRepository.findByIdIn(updateRequests.getRequestIds());
        validateRequestsPending(requests);
        return updateRequestsStatus(requests, event, updateRequests.getStatus());
    }

    private void validateRequest(Long userId, Event event) {
        if (!requestRepository.findByRequestorId(userId).isEmpty()) {
            throw new ConflictException("нельзя добавить повторный запрос");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0) {
            Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников");
            }
        }
        if (event.getState() !=EventState.PUBLISHED) {
            throw new ConflictException("нельзя добавить запрос на неопубликованное событие");
        }
    }

    private void validateRequestsPending(List<Request> requests) {
        if (requests.stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            throw new ConflictException("Все запросы должы быть со статусом PENDING");
        }
    }

    private RequestStatus getRequestStatus(Event event) {
        boolean requiresModeration = event.getRequestModeration() != null && event.getRequestModeration();
        boolean hasLimit = event.getParticipantLimit() != null && event.getParticipantLimit() > 0;

        return (requiresModeration && hasLimit) ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
    }

    private void updateEventConfirmedRequests(Request request, Event event) {
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
    }

    private void updateConfirmedRequestsOnCanceled(RequestStatus status, Event event) {
        if (status == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
    }

    private EventRequestStatusUpdateResult updateRequestsStatus(List<Request> requests, Event event, RequestStatus status) {
        if (status.equals(RequestStatus.CONFIRMED)) {
            return confirmRequests(event, requests, status);
        }
        if (status.equals(RequestStatus.REJECTED)) {
            setRequestsStatis(requests, status);
            return rejectRequests(requests);
        }
        throw new ValidationException("Неизвестный статус: " + status);
    }

    private EventRequestStatusUpdateResult confirmRequests(Event event, List<Request> requests, RequestStatus status) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (event.getConfirmedRequests() + requests.size() > event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников исчерпан");
        }

        setRequestsStatis(requests, status);

        event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
        eventRepository.save(event);

        result.getConfirmedRequests().addAll(requests.stream().map(RequestMapper::toRequestDto).toList());
        return result;
    }

    private EventRequestStatusUpdateResult rejectRequests(List<Request> requests) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.getRejectedRequests().addAll(requests.stream().map(RequestMapper::toRequestDto).toList());
        return result;
    }

    private void setRequestsStatis(List<Request> requests, RequestStatus status) {
        requests.forEach(r -> r.setStatus(status));
        requestRepository.saveAll(requests);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private Request getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с id = " + requestId + " не найдена"));
    }
}
