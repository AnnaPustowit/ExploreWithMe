package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventDto {
    Long id;

    @Size(max = 120)
    @Size(min = 3)
    String title;

    @Size(max = 7000)
    @Size(min = 20)
    String description;

    @Size(max = 2000)
    @Size(min = 20)
    String annotation;

    Long category;

    String eventDate;

    Location location;

    String paid;

    Integer participantLimit;

    String requestModeration;

    StateAction stateAction;
}
