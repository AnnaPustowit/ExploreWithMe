package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestServiceImpl;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestServiceImpl requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Request createRequest(@PathVariable @Positive Long userId, @RequestParam @Positive Long eventId) throws ConflictException, EntityNotFoundException {
        Request newRequest = requestService.createRequest(userId, eventId);
        log.info("Добавление запроса от текущего пользователя на участие в событии");
        return newRequest;
    }

    @GetMapping
    public List<Request> getRequests(@PathVariable Long userId) {
        List<Request> requests = requestService.getAllRequest(userId);
        log.info("Получение информации о заявках текущего пользователя на участие в чужих событиях");
        return requests;
    }

    @PatchMapping("/{requestId}/cancel")
    public Request updateRequestStatusToCancel(@PathVariable Long userId, @PathVariable Long requestId) {
        Request request = requestService.updateRequestStatusToCancel(userId, requestId);
        log.info("Отмена запроса пользователя на участие в событии");
        return request;
    }
}
