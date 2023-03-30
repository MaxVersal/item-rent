package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests")
@Entity
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "decription")
    String description;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User requester;

    @Column(name = "created")
    LocalDateTime created;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    Set<Item> items;
}
