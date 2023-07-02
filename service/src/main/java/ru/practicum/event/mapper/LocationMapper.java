package ru.practicum.event.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@NoArgsConstructor
public class LocationMapper {
    public static LocationDto toLocationDtoFromLocation(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
