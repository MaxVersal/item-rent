package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User postUser(User user);

    List<User> getUsers();

    User getUser(Long id);

    User patchUser(User user, Long id);

    ResponseEntity<String> deleteUser(Long id);
}
