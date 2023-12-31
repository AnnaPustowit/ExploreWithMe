package ru.practicum.service;

import ru.practicum.ViewStatsDto;
import ru.practicum.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void createHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
