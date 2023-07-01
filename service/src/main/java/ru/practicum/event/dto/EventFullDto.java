package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    Long id;

    String title;

    String description;

    String annotation;

    EventState state;

    Category category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    Integer confirmedRequests;

    LocationDto location;

    UserShortDto initiator;

    boolean paid;

    Integer participantLimit;

    boolean requestModeration;

    Long views;
}
