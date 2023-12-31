package ru.practicum.event.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;

import ru.practicum.event.model.Location;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Sort;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.request.model.ConfirmedRequest;
import ru.practicum.request.repository.RequestRepository;

import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private static final String RANGE_START = "2000-01-01 00:01:01";
    private static final String RANGE_END = "2099-01-01 23:59:59";
    private static final String APP_NAME = "ewm-service";
    private static final String URI = "/events/";
    private static final long HOURS_BEFORE_START = 2L;
    private static final long ADMIN_HOURS_BEFORE_START = 1L;
    private static final long MINUTE_LATER_NOW = 1L;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statClient;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId);
        }
        Optional<Category> category = categoryRepository.findById(newEventDto.getCategory());
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет категории с id: " + newEventDto.getCategory());
        }
        if (LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter).isAfter(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
            Location location = locationRepository.save(newEventDto.getLocation());
            LocationDto locationDto = LocationMapper.toLocationDtoFromLocation(newEventDto.getLocation());
            Event event = eventRepository.save(EventMapper.toEventFromEventNewDto(user.get(), location, newEventDto, category.get()));

            UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(userRepository.getReferenceById(userId));
            Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
            event.setConfirmedRequests(confirmedRequests);
            return EventMapper.toEventFullDtoWhenCreate(event, locationDto, userShortDto, 0L);
        } else {
            throw new InvalidParameterException("Некорректная дата события");
        }
    }

    @Override
    public List<EventFullDto> getByUserId(Long userId, Integer size, Integer from, HttpServletRequest request) {
        PageRequest pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorIdOrderByIdDesc(userId, pageable);
        if (events.size() == 0) {
            return List.of();
        }

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(
                    EventMapper.toEventFullDtoWhenSearch(event, getEventViewsMap(events, eventIds), getConfirmedRequestsMap(eventIds))
            );
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByUserAndEventId(Long userId, Long eventId, HttpServletRequest request) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + eventId);
        }
        if (!event.get().getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Нет события с id: " + eventId + "созданного пользователем c id: " + userId);
        }

        String eventUri = URI + event.get().getId();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), true);
        Long views = viewStatsDtos.size() == 0 ? 0 : viewStatsDtos.get(0).getHits();

        event.get().setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventId));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event.get(), views);
        return eventFullDto;

    }

    @Override
    public List<EventFullDto> searchEventsPublic(String text, String categoriesString, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                 Sort sort, Integer from, Integer size, HttpServletRequest request) {
        PageRequest pageable = PageRequest.of(from / size, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart != null && rangeEnd != null) {
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            if (startTime.isAfter(endTime)) {
                throw new InvalidParameterException("Время начала не должно быть позже времени окончания");
            }
        } else {
            startTime = LocalDateTime.now().plusMinutes(MINUTE_LATER_NOW);
            endTime = LocalDateTime.parse(RANGE_END, dateTimeFormatter);
        }
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        List<Event> events = new ArrayList<>();
        List<Long> categories = Util.getListLongFromString(categoriesString);
        if (sort == null || sort.equals(Sort.VIEWS)) {
            events = eventRepository.findEventsByParamsOrderById(text, categories, paid, startTime, endTime, onlyAvailable, pageable);
        } else if (sort != null && sort.equals(Sort.EVENT_DATE)) {
            events = eventRepository.findEventsByParamsOrderByDate(text, categories, paid, startTime, endTime, onlyAvailable, pageable);
        } else {
            throw new InvalidParameterException("Неверный параметр sort:" + sort);
        }

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(
                    EventMapper.toEventFullDtoWhenSearch(event, getEventViewsMap(events, eventIds), getConfirmedRequestsMap(eventIds))
            );
        }

        statClient.saveStats(toEndpointHit(APP_NAME, request));
        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByEventId(Long eventId, HttpServletRequest request) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty() || !event.get().getState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException("Нет события с id: " + eventId);
        }

        String eventUri = URI + event.get().getId();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), true);
        Long views = viewStatsDtos.size() == 0 ? 0 : viewStatsDtos.get(0).getHits();

        event.get().setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(eventId));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event.get(), views);
        statClient.saveStats(toEndpointHit(APP_NAME, request));
        return eventFullDto;
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventDto eventUpdateDto, HttpServletRequest request) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + eventId);
        }
        if (!event.get().getInitiator().getId().equals(userId)) {
            throw new InvalidParameterException("Пользователь не может изменять чужие события");
        }

        if (event.get().getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить событие в статусе PUBLISHED");
        }
        LocalDateTime startTime;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            startTime = event.get().getEventDate();
        } else {
            startTime = LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter);
        }
        if (startTime.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
            throw new InvalidParameterException("Дата и время на которые намечено событие не может быть раньше, чем через 2 часа от текущего момента");
        }

        checkAndUpdateEvent(eventUpdateDto, event.get());
        if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
            if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.get().setState(EventState.PENDING);
            }
            if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.get().setState(EventState.CANCELED);
            }
        }

        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);
        event.get().setConfirmedRequests(confirmedRequest);

        String eventUri = URI + event.get().getId();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), true);
        Long views = viewStatsDtos.size() == 0 ? 0 : viewStatsDtos.get(0).getHits();
        return EventMapper.toEventFullDto(eventRepository.save(event.get()), views);
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(String usersString, String statesString, String categoriesString,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer size, Integer from, HttpServletRequest request) {
        PageRequest pageable = PageRequest.of(from / size, size);
        List<Event> events;

        List<Long> usersIds = Util.getListLongFromString(usersString);
        List<Long> categories = Util.getListLongFromString(categoriesString);
        List<EventState> states = Util.getListEventStateFromString(statesString);

        events = eventRepository.searchEventsByAdmin(usersIds, states, categories, rangeStart, rangeEnd, pageable);

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(
                    EventMapper.toEventFullDtoWhenSearch(event, getEventViewsMap(events, eventIds), getConfirmedRequestsMap(eventIds))
            );
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventDto updateEventDto, HttpServletRequest request) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + event);
        }
        if (event.get().getState().equals(EventState.PUBLISHED) && updateEventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Нельзя опубликовать уже опубликованное событие.");
        }
        if (event.get().getState().equals(EventState.CANCELED) && updateEventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Нельзя опубликовать событие в состоянии CANCELED.");
        }
        if (event.get().getState().equals(EventState.PUBLISHED) && updateEventDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            throw new ConflictException("Нельзя отменить событие в состоянии PUBLISHED.");
        }

        LocalDateTime startTime;
        if (Optional.ofNullable(updateEventDto.getEventDate()).isEmpty()) {
            startTime = event.get().getEventDate();
        } else {
            startTime = LocalDateTime.parse(updateEventDto.getEventDate(), dateTimeFormatter);
        }
        if (startTime.isAfter(LocalDateTime.now().plusHours(ADMIN_HOURS_BEFORE_START)) && event.get().getState().equals(EventState.PENDING)) {
            checkAndUpdateEvent(updateEventDto, event.get());
            if (Optional.ofNullable(updateEventDto.getStateAction()).isPresent()) {
                if (updateEventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.get().setState(EventState.PUBLISHED);
                    event.get().setPublishedOn(LocalDateTime.now());
                }
                if (updateEventDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.get().setState(EventState.CANCELED);
                }
            }
            Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);
            event.get().setConfirmedRequests(confirmedRequest);

            String eventUri = URI + event.get().getId();
            List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), true);

            Long views = viewStatsDtos.size() == 0 ? 0 : viewStatsDtos.get(0).getHits();

            EventFullDto ed = EventMapper.toEventFullDto(eventRepository.save(event.get()), views);
            return ed;
        } else {
            throw new InvalidParameterException("Некорректные данные: статус или время начала события");
        }
    }


    private Map<Long, Long> getEventHitsMap(List<ViewStatsDto> hitDtos, List<Long> eventIds) {
        Map<Long, Long> eventIdHitsMap = new HashMap<>(); // eventId : hits
        if (hitDtos.size() == 0) {
            for (Long eventId : eventIds) {
                eventIdHitsMap.put(eventId, 0L);
            }
            return eventIdHitsMap;
        }
        for (ViewStatsDto viewStatsDto : hitDtos) {
            eventIdHitsMap.put(Long.parseLong(viewStatsDto.getUri().replace("/events/", "")), viewStatsDto.getHits());
        }
        return eventIdHitsMap;
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        List<ConfirmedRequest> confirmedRequests = requestRepository.getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> confirmedRequestsMap = new HashMap<>();
        if (confirmedRequests.size() > 0) {
            confirmedRequestsMap = confirmedRequests.stream()
                    .collect(Collectors.toMap(
                            cr -> cr.getEventId(),
                            cr -> cr.getCountConfirmedRequest()
                    ));
        }
        return confirmedRequestsMap;
    }

    Map<Long, Long> getEventViewsMap(List<Event> events, List<Long> eventIds) {
        List<String> uriEventList = events.stream()
                .map(e -> URI + e.getId().toString())
                .collect(toList());
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, uriEventList, true);
        Map<Long, Long> eventViewsMap = getEventHitsMap(viewStatsDtos, eventIds);
        return eventViewsMap;
    }

    private void checkAndUpdateEvent(UpdateEventDto updateEventDto, Event event) {
        if (Optional.ofNullable(updateEventDto.getTitle()).isPresent()) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (Optional.ofNullable(updateEventDto.getDescription()).isPresent()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (Optional.ofNullable(updateEventDto.getAnnotation()).isPresent()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (Optional.ofNullable(updateEventDto.getCategory()).isPresent()) {
            Optional<Category> category = categoryRepository.findById(updateEventDto.getCategory());
            if (category.isEmpty()) {
                throw new EntityNotFoundException("Нет категории с id: " + updateEventDto.getCategory());
            }
            event.setCategory(category.get());
        }
        if (Optional.ofNullable(updateEventDto.getEventDate()).isPresent()) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventDto.getEventDate(), dateTimeFormatter);
            event.setEventDate(eventDate);
        }
        if (Optional.ofNullable(updateEventDto.getLocation()).isPresent()) {
            event.setLocation(locationRepository.save(updateEventDto.getLocation()));
        }
        if (Optional.ofNullable(updateEventDto.getPaid()).isPresent()) {
            event.setPaid(Boolean.parseBoolean(updateEventDto.getPaid()));
        }
        if (Optional.ofNullable(updateEventDto.getParticipantLimit()).isPresent()) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (Optional.ofNullable(updateEventDto.getRequestModeration()).isPresent()) {
            event.setRequestModeration(Boolean.parseBoolean(updateEventDto.getRequestModeration()));
        }
    }

    public static EndpointHitDto toEndpointHit(String app, HttpServletRequest request) {
        return new EndpointHitDto(
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }
}
