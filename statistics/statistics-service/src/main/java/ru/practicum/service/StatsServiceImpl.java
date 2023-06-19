package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exeption.ValidateException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Statistics;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void createHit(EndpointHitDto endpointHitDto) {
        Statistics statistics = StatsMapper.toStatistics(endpointHitDto);
        statsRepository.save(statistics);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validate(start, end);
        if (uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsUniqueIp(start, end);
            }git commit
            return statsRepository.getStats(start, end);
        }
        if (unique) {
            return statsRepository.getStatsForUriListAndUniqueIp(start, end, uris);
        }
        return statsRepository.getStatsForUriList(start, end, uris);
    }

    private void validate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end))
            throw new ValidateException("Время начала должно быть раньше времени конца");
    }
}
