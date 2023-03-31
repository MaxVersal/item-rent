package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("should create a new user with valid input")
    void postUserWithValidInput() throws Exception {
        User user1 = new User(1L, "User1", "user1@example.com", Collections.emptySet());

        when(userService.postUser(any())).thenReturn(user1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"User1\", \"email\" : \"user1@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(user1.getId()))
                .andExpect(jsonPath("$.name").value(user1.getName()))
                .andExpect(jsonPath("$.email").value(user1.getEmail()));
    }

    @Test
    public void getUsersShouldReturnUsersList() throws Exception {
        User user1 = new User(1L, "User1", "user1@example.com", Collections.emptySet());
        User user2 = new User(2L, "User2", "user2@example.com", Collections.emptySet());

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));
    }

    @Test
    public void getUserByIdShouldReturnUser() throws Exception {
        User user = new User(1L, "User1", "user1@example.com", Collections.emptySet());

        when(userService.getUser(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void patchUserShouldReturnPatchedUser() throws Exception {
        User user = new User(1L, "User1", "user1@example.com", Collections.emptySet());
        User patchedUser = new User(1L, "User1patched", "user1patched@example.com", Collections.emptySet());

        when(userService.patchUser(patchedUser, user.getId())).thenReturn(patchedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content("{\"name\": \"User1patched\", \"email\": \"user1patched@example.com\"}"))
                .andExpect(status().isOk());

    }

    @Test
    public void deleteUserShouldReturnOk() throws Exception {
        Long userId = 1L;

        when(userService.deleteUser(anyLong())).thenReturn("User with id " + userId + " has been deleted");

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id " + userId + " has been deleted"));

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    public void deleteUserShouldReturnOkStatus() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("should return user by id")
    public void shouldReturnUserById() throws Exception {
        User user = new User(1L, "User1", "user1@example.com", Collections.emptySet());

        when(userService.getUser(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("should update user")
    public void shouldUpdateUser() throws Exception {
        User user = new User(1L, "User1", "updated@example.com", Collections.emptySet());
        when(userService.patchUser(any(), anyLong())).thenReturn(user);

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
