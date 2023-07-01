package ru.practicum.event.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.ShortEventDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
public class EventMapper {
    public static Event toEventFromEventNewDto(User user, Location location, NewEventDto newEventDto, Category category) {
        return new Event(
                newEventDto.getId(),
                newEventDto.getTitle(),
                newEventDto.getDescription(),
                newEventDto.getAnnotation(),
                EventState.valueOf("PENDING"),
                category,
                LocalDateTime.now(),
                LocalDateTime.parse(newEventDto.getEventDate().replaceAll(" ", "T")),
                null,
                0,
                location,
                user,
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration()
        );
    }

    public static EventFullDto toEventFullDtoWhenCreate(
            Event event, LocationDto locationDto, UserShortDto userShortDto, Long views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                event.getState(),
                event.getCategory(),
                event.getCreatedOn(),
                event.getEventDate(),
                event.getPublishedOn(),
                event.getConfirmedRequests(),
                locationDto,
                userShortDto,
                event.isPaid(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                views
        );
    }

    public static ShortEventDto toShortEventDto(
            Event event) {
        return new ShortEventDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().toString(),
                UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                0L
        );
    }

    public static EventFullDto toEventFullDto(Event event, Long views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                event.getState(),
                event.getCategory(),
                event.getCreatedOn(),
                event.getEventDate(),
                event.getPublishedOn(),
                event.getConfirmedRequests(),
                LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                views
        );
    }

    public static EventFullDto toEventFullDtoWhenSearch(Event event,
                                                        Map<Long, Long> eventViewsMap,
                                                        Map<Long, Long> confirmedRequestsMap) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                event.getState(),
                event.getCategory(),
                event.getCreatedOn(),
                event.getEventDate(),
                event.getPublishedOn(),
                confirmedRequestsMap.containsKey(event.getId()) ? confirmedRequestsMap.get(event.getId()).intValue() : 0,
                LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                eventViewsMap.get(event.getId())
        );
    }
}
