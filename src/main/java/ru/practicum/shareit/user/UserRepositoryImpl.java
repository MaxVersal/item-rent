package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongDataUpdateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final ItemRepository itemRepository;

    private static Map<Long, User> users = new HashMap();

    private Long newId = 1L;


    @Override
    public User addUser(User user) {
        validateEmail(user, newId);
        user.setId(newId++);
        users.put(user.getId(), user);
        itemRepository.addUser(user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new UserNotFoundException("Пользователь с данным id не найден");
    }

    @Override
    public User updateUser(User user, Long id) {
        User currentUser = getUserById(id);
        if (user.getName() != null) currentUser.setName(user.getName());
        if (user.getEmail() != null) {
            validateEmail(user, id);
            currentUser.setEmail(user.getEmail());
        }
        users.put(currentUser.getId(), currentUser);
        return currentUser;
    }

    @Override
    public String deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return "Пользователь удален";
        } else {
            throw new UserNotFoundException("Данного пользователя не существует");
        }
    }

    private static void validateEmail(User user, Long id) {
        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail()) && !user1.getId().equals(id)) {
                throw new WrongDataUpdateException("Пользователь с таким email уже существует");
            }
        }
    }

}
