package ru.practicum.request.service;

import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long userId, Long eventId) throws ConflictException, EntityNotFoundException;

    List<Request> getAllRequest(Long id);

    List<Request> getByUserAndEventId(Long userId, Long eventId);

    Request updateRequestStatusToCancel(Long userId, Long requestId);

    RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) throws ConflictException, InvalidParameterException, EntityNotFoundException;
}
