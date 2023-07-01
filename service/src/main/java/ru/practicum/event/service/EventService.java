package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Sort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long id, NewEventDto eventNewDto);

    List<EventFullDto> searchEventsPublic(String text, String categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from, Integer size,
                                          HttpServletRequest request);

    EventFullDto getByEventId(Long id, HttpServletRequest request);

    List<EventFullDto> getByUserId(Long id, Integer size, Integer from, HttpServletRequest request);

    EventFullDto getByUserAndEventId(Long userId, Long eventId, HttpServletRequest request);

    EventFullDto updateByUser(Long userId, Long eventId, UpdateEventDto updateEventDto, HttpServletRequest request);

    List<EventFullDto> searchEventsByAdmin(String usersId, String states, String categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer size, Integer from,
                                           HttpServletRequest request);

    EventFullDto updateByAdmin(Long eventId, UpdateEventDto updateEventDto, HttpServletRequest request);

}
