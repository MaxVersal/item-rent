package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User postUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        } else {
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
                        user1.setEmail(user.getEmail());
                    }
                    userRepository.save(user1);
                });
        return userRepository.findById(id).get();
    }

    @Override
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "Успешное удаление";
    }
}
