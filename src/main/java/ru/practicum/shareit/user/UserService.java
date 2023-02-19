package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User postUser(User user);

    List<User> getUsers();

    User getUser(Long id);

    User patchUser(User user, Long id);

    String deleteUser(Long id);
}
