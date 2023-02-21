package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.groups.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    @Email(message = "Некорректная почта", groups = Create.class)
    @NotNull(groups = Create.class)
    private String email;
}
