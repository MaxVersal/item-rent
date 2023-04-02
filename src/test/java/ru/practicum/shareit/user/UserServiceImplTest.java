package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testPostUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        // При вызове userRepository.save(user) возвращает user с заполненным id
        when(userRepository.save(user)).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.postUser(user);

        // Проверяем, что результат не является null
        assertNotNull(result);

        // Проверяем, что метод userRepository.save() был вызван ровно 1 раз с аргументом user
        verify(userRepository, times(1)).save(user);

        // Проверяем, что идентификатор добавленного пользователя не равен null
        assertNotNull(result.getId());
        assertEquals(1L, result.getId().longValue());
        assertEquals("John Doe", result.getName());
        assertEquals("johndoe@example.com", result.getEmail());
    }

    @Test
    public void testPostUserDuplicateEmail() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("johndoe@example.com");

        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("johndoe@example.com");

        // Если вызвать userRepository.save(user), то будет выброшено исключение DataIntegrityViolationException
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenThrow(DataIntegrityViolationException.class);

        userService.postUser(user1);

        // Проверяем, что метод userRepository.save() был вызван ровно 1 раз
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetUser() {
        // Создаем пользователя для поиска
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        // Определяем поведение userRepository.findById(id)
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Проверяем возвращаемое значение и вызов метода userRepository.findById(id)
        assertEquals(user, userService.getUser(1L));
        assertThrows(UserNotFoundException.class, () -> userService.getUser(2L));

        verify(userRepository, times(3)).findById(anyLong());
    }


    @Test
    public void testGetUserByInvalidId() {
        Long invalidUserId = 100L;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(invalidUserId);
        });
    }

    @Test
    public void testPatchUserNameAndEmail() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("johndoe@example.com");

        User updatedUser = new User();
        updatedUser.setName("Jane Doe");
        updatedUser.setEmail("janedoe@example.com");

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        User result = userService.patchUser(updatedUser, existingUser.getId());

        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());

        verify(userRepository, times(2)).findById(existingUser.getId());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testPatchUserNameOnly() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("johndoe@example.com");

        User updatedUser = new User();
        updatedUser.setName("Jane Doe");

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        User result = userService.patchUser(updatedUser, existingUser.getId());

        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());

        verify(userRepository, times(2)).findById(existingUser.getId());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testPatchUserEmailOnly() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("johndoe@example.com");

        User updatedUser = new User();
        updatedUser.setEmail("janedoe@example.com");

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        User result = userService.patchUser(updatedUser, existingUser.getId());

        assertEquals(existingUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());

        verify(userRepository, times(2)).findById(existingUser.getId());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testPatchUserNotFound() {
        Long nonExistingId = 1L;

        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            userService.patchUser(new User(), nonExistingId);
        });

        verify(userRepository, times(2)).findById(nonExistingId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        // Вызываем метод deleteUser
        userService.deleteUser(1L);

        // Проверяем, что метод userRepository.deleteById(id) был вызван
        verify(userRepository, times(1)).deleteById(anyLong());
    }

}
