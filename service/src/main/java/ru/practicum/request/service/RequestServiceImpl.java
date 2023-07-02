package ru.practicum.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.model.RequestUpdateState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public Request createRequest(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Не найден пользователь с id: " + userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Не найдено событие с id: " + eventId);
        }
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new ConflictException("Твкой запрос уже существует");
        }

        if (event.get().getState() != EventState.PUBLISHED) {
            throw new ConflictException("Такого события нет. Запрос отклонен");
        }
        if (Objects.equals(event.get().getInitiator().getId(), userId)) {
            throw new ConflictException("Запрос на свое событие отклонен");
        }

        Integer confirmedRequestCount = requestRepository.getConfirmedRequestsByEventId(eventId);
        if (confirmedRequestCount == null) {
            confirmedRequestCount = 0;
        }

        if (event.get().getParticipantLimit() != 0 && event.get().getParticipantLimit() <= confirmedRequestCount) {
            throw new ConflictException("Превышен лимит участников");
        }
        RequestState status = RequestState.PENDING;
        if (!event.get().isRequestModeration() || event.get().getParticipantLimit() == 0) {
            status = RequestState.CONFIRMED;
        }
        Request newRequest = new Request(null,
                LocalDateTime.now(),
                event.get().getId(),
                user.get().getId(),
                status);
        return requestRepository.save(newRequest);
    }

    @Override
    public List<Request> getAllRequest(Long userId) {
        return requestRepository.getAllByUserId(userId);
    }

    @Override
    public List<Request> getByUserAndEventId(Long userId, Long eventId) {
        return requestRepository.getByUserAndEventId(userId, eventId);
    }

    @Override
    @Transactional
    public Request updateRequestStatusToCancel(Long userId, Long requestId) {
        Request request = requestRepository.getReferenceById(requestId);
        request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    @Transactional
    @Override
    public RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + eventId);
        }
        if (event.get().getConfirmedRequests() == null) {
            event.get().setConfirmedRequests(0);
        }
        List<Request> requests = requestRepository.getByRequestsList(requestUpdateDto.getRequestIds());

        RequestUpdateResultDto requestResultList = new RequestUpdateResultDto(new ArrayList<RequestDto>(), new ArrayList<RequestDto>());
        Integer confirmedRequestCount = event.get().getConfirmedRequests();
        if (confirmedRequestCount == null) {
            confirmedRequestCount = 0;
        }
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestState.PENDING)) {
                throw new ConflictException("Можно изменить заявку только в статусе PENDING");
            }
            if (requestUpdateDto.getStatus().equals(RequestUpdateState.CONFIRMED)) {
                if (event.get().getParticipantLimit() != 0 || !event.get().isRequestModeration()) {
                    if (event.get().getConfirmedRequests().equals(event.get().getParticipantLimit())) {
                        throw new ConflictException("Превышен лимит участников");
                    }
                    if (confirmedRequestCount < event.get().getParticipantLimit()) {
                        request.setStatus(RequestState.CONFIRMED);
                        event.get().setConfirmedRequests(event.get().getConfirmedRequests() + 1);
                        requestRepository.save(request);
                        requestResultList.getConfirmedRequests().add(RequestMapper.toRequestDtoFromRequest(request));
                        confirmedRequestCount++;
                    } else {
                        request.setStatus(RequestState.REJECTED);
                        requestRepository.save(request);
                        requestResultList.getRejectedRequests().add(RequestMapper.toRequestDtoFromRequest(request));
                    }
                }
            } else if (requestUpdateDto.getStatus().equals(RequestUpdateState.REJECTED)) {
                if (request.getStatus().equals(RequestState.CONFIRMED)) {
                    throw new ConflictException("Нельзя отменить уже принятую заявку");
                }
                request.setStatus(RequestState.REJECTED);
                requestRepository.save(request);
                requestResultList.getRejectedRequests().add(RequestMapper.toRequestDtoFromRequest(request));
            }
        }
        eventRepository.save(event.get());
        return requestResultList;
    }
}
