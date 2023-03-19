package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongDataUpdateException;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    private static Map<Long, String> emails = new HashMap<>();

    private static Long newId = 1L;

    @Override
    @Transactional
    public User postUser(User user) {
////        emails.put(newId, user.getEmail());
////        try {
////            validateEmail(user.getEmail(), newId);
////        } catch (WrongDataUpdateException e) {
////            emails.remove(newId);
////            throw new WrongDataUpdateException("Почта занята!");
//        }
        newId++;
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        try {
            return userRepository.findById(id).get();
        } catch (Exception e) {
            throw new UserNotFoundException("Пользователь с указанным id не найден");
        }
    }

    @Override
    public User patchUser(User user, Long id) {
        userRepository
                .findById(id)
                .ifPresent(user1 -> {
                    if (user.getName() != null) {
                        user1.setName(user.getName());
                    }
                    if (user.getEmail() != null) {
                        //validateEmail(user.getEmail(), id);
                        emails.put(id, user.getEmail());
                        user1.setEmail(user.getEmail());
                    }
                    userRepository.save(user1);
                });
        return userRepository.findById(id).get();
    }

    @Override
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        emails.remove(id);
        return "Успешное удаление";
    }

    private void validateEmail(String email, Long id) {
        for (Map.Entry<Long, String> current : emails.entrySet()) {
            if (current.getValue().equals(email) && !current.getKey().equals(id)) {
                throw new WrongDataUpdateException("Почта уже занята");
            }
        }
    }
}
