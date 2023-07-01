package ru.practicum.event.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    Float lat;
    Float lon;
}
