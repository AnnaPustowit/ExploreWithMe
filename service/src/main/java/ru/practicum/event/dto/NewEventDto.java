package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    Long id;

    @NotEmpty
    @Size(max = 120, min = 3)
    String title;

    @NotEmpty
    @Size(max = 7000, min = 20)
    String description;

    @NotEmpty
    @Size(max = 2000, min = 20)
    String annotation;

    Long category;

    String eventDate;

    Location location;

    boolean paid;

    Integer participantLimit;

    Boolean requestModeration;
}
