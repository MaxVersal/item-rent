package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    List<User> getUsers();

    User getUserById(Long id);

    User updateUser(User user, Long id);

    String deleteUser(Long id);
}
