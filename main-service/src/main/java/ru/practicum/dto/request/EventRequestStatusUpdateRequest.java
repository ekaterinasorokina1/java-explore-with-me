package ru.practicum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.model.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;

    @NotNull
    private RequestStatus status;
}
