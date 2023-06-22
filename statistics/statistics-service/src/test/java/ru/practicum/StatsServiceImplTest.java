package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Statistics;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = {"db.name=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceImplTest {
    private final StatsService service;
    private final StatsRepository repository;
    private EndpointHitDto dto;
    private EndpointHitDto dto2;
    private EndpointHitDto dto3;

    @BeforeEach
    void setUp() {
        LocalDateTime date1 = LocalDateTime.of(2023, 6, 19, 11, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2023, 6, 19, 11, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2023, 6, 19, 11, 0, 0);
        dto = new EndpointHitDto("test", "/events/1", "1.1.1.1",
                date1);
        dto2 = new EndpointHitDto("test", "/events/2", "1.1.1.1",
                date2);
        dto3 = new EndpointHitDto("test", "/events/2", "1.1.1.1",
                date3);
    }

    @Test
    void saveStat() {
        service.createHit(dto);
        Statistics result = repository.findById(1L).get();

        assertThat(result, notNullValue());
        assertThat(result.getApp(), equalTo(dto.getApp()));
        assertThat(result.getUri(), equalTo(dto.getUri()));
        assertThat(result.getIp(), equalTo(dto.getIp()));
    }

    @Test
    void getStats() {
        service.createHit(dto);
        service.createHit(dto2);
        service.createHit(dto3);
        List<ViewStatsDto> result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2023, 6, 19, 0, 0, 0),
                LocalDateTime.of(2023, 6, 19, 23, 0, 0),
                List.of(), false));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getHits());
        assertEquals(1, result.get(1).getHits());
        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2023, 6, 19, 0, 0, 0),
                LocalDateTime.of(2023, 6, 19, 23, 0, 0),
                List.of("/events/2"), false));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());
        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2023, 6, 19, 0, 0, 0),
                LocalDateTime.of(2023, 6, 19, 23, 0, 0),
                List.of(), true));

        assertEquals(2, result.size());
        assertEquals("/events/1", result.get(0).getUri());//here
        assertEquals(1, result.get(0).getHits());
        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(1, result.get(1).getHits());

        result = new ArrayList<>(service.getStats(
                LocalDateTime.of(2023, 6, 19, 0, 0, 0),
                LocalDateTime.of(2023, 6, 19, 23, 0, 0),
                List.of("/events/2"), true));

        assertEquals(1, result.size());
        assertEquals("/events/2", result.get(0).getUri());
        assertEquals(1, result.get(0).getHits());
    }
}
