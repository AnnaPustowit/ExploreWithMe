package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Sort;
import ru.practicum.event.service.EventService;
import ru.practicum.exeption.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) Sort sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) throws InvalidParameterException {
        List<EventFullDto> events = eventService.searchEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        log.info("Получение событий");
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByEventId(@PathVariable @Positive Long id,
                                          HttpServletRequest request) {
        EventFullDto event = eventService.getByEventId(id, request);
        log.info("Получить информации о событии по его id: {}", id);
        return event;
    }
}
