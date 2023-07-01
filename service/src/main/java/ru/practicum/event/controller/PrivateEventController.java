package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto addEvent(@PathVariable @Positive Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Создание события: {}", newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable @Positive Long eventId,
                                          @Valid @RequestBody UpdateEventDto updateEventDto,
                                          HttpServletRequest request) {
        log.info("Обновление информации о событии: {}", updateEventDto);
        return eventService.updateByUser(userId, eventId, updateEventDto, request);
    }

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                HttpServletRequest request) {
        log.info("Получение событий пользователя с id: {}", userId);
        return eventService.getByUserId(userId, size, from, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserAndEventId(@PathVariable Long userId,
                                                 @PathVariable @Positive Long eventId,
                                                 HttpServletRequest request) {
        log.info("Получение информации по eventId: {}, userId: {}", eventId, userId);
        return eventService.getByUserAndEventId(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResultDto updateRequestsStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody @Valid RequestUpdateDto requestUpdateDto) {
        log.info("Обновление статуса заявки на участие для отмены");
        return requestService.updateRequestsStatus(userId, eventId, requestUpdateDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<Request> getByUserAndEventId(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("Получение списка запросов запросов пользователя по событиям");
        return requestService.getByUserAndEventId(userId, eventId);
    }
}
