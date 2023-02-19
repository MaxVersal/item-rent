package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
@Qualifier("UserServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User postUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public User patchUser(User user, Long id) {
        return userRepository.updateUser(user, id);
    }

    @Override
    public String deleteUser(Long id) {
        return userRepository.deleteUser(id);
    }
}
