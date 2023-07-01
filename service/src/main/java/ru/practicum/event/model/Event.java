package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotEmpty
    @Size(max = 120)
    @Size(min = 3)
    @Column(name = "title")
    String title;

    @NotEmpty
    @Size(max = 7000)
    @Size(min = 20)
    @Column(name = "description")
    String description;

    @NotEmpty
    @Size(max = 2000)
    @Size(min = 20)
    @Column(name = "annotation")
    String annotation;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_on")
    LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "event_date")
    LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "paid")
    boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "request_moderation")
    boolean requestModeration;
}
