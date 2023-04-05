package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.groups.Create;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    @Column(name = "text")
    String text;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", updatable = false, insertable = false)
    Item item;

    @Column(name = "publication_date")
    LocalDateTime commentTime;

    public Comment(@NonNull String text, User user) {
        this.text = text;
        this.user = user;
    }
}
