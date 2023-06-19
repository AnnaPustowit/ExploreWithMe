package ru.practicum.mapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.Statistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    public static Statistics toStatistics(EndpointHitDto endpointHitDto) {
        if (endpointHitDto != null) {
            return Statistics.builder()
                    .app(endpointHitDto.getApp())
                    .uri(endpointHitDto.getUri())
                    .ip(endpointHitDto.getIp())
                    .timestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
        } else {
            return null;
        }
    }

}
