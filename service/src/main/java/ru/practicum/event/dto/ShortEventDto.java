package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortEventDto {
    private Long id;
    private String annotation; // краткое описание
    private CategoryDto category; // категория
    private Integer confirmedRequests; // одобренные заяавки
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid; // платно ли
    private String title;
    private Long views; // количество просмотров
}
