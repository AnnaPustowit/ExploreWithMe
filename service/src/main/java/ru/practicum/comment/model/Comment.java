package ru.practicum.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotEmpty
    @Column(name = "text")
    String text;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    Event event;
    @Column(name = "created")
    LocalDateTime created;
    @Column(name = "comment_state")
    @Enumerated(EnumType.STRING)
    CommentState commentState;
}
