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
    public EventFullDto createEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        EventFullDto event = eventService.createEvent(userId, newEventDto);
        log.info("Добавление нового события: {}", newEventDto);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable @Positive Long eventId,
                                          @Valid @RequestBody UpdateEventDto updateEventDto,
                                          HttpServletRequest request) {
        EventFullDto event = eventService.updateByUser(userId, eventId, updateEventDto, request);
        log.info("Изменение информации о событии, добавленного текущим пользователем {}", updateEventDto);
        return event;
    }

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                HttpServletRequest request) {
        List<EventFullDto> events = eventService.getByUserId(userId, size, from, request);
        log.info("Получение событий, добавленных пользователем с id: {}", userId);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserAndEventId(@PathVariable Long userId,
                                                 @PathVariable @Positive Long eventId,
                                                 HttpServletRequest request) {
        EventFullDto event = eventService.getByUserAndEventId(userId, eventId, request);
        log.info("Получение полной информации о событии");
        return event;
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResultDto updateRequestsStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody @Valid RequestUpdateDto requestUpdateDto) {
        RequestUpdateResultDto request = requestService.updateRequestsStatus(userId, eventId, requestUpdateDto);
        log.info("Изменение статуса заявок на участие в событии");
        return request;
    }

    @GetMapping("/{eventId}/requests")
    public List<Request> getByUserAndEventId(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        List<Request> requests = requestService.getByUserAndEventId(userId, eventId);
        log.info("Получение информации о запросах на участие в событии текущего пользователя");
        return requests;
    }
}
