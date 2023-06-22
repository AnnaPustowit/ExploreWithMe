package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.Statistics;

public class StatsMapper {
    public static Statistics toStatistics(EndpointHitDto endpointHitDto) {
        if (endpointHitDto != null) {
            return Statistics.builder()
                    .app(endpointHitDto.getApp())
                    .uri(endpointHitDto.getUri())
                    .ip(endpointHitDto.getIp())
                    .timestamp(endpointHitDto.getTimestamp())
                    .build();
        } else {
            return null;
        }
    }

}
