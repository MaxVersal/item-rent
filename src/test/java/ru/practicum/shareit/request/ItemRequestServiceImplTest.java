package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("should throw a UserNotFoundException when the requester ID is not found")
    void getOwnByIdThrowsUserNotFoundException() {
        Long requesterId = 1L;
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getOwnById(requesterId));
    }

    @Test
    @DisplayName("should return an empty list when the requester has no item requests")
    void getOwnByIdReturnsEmptyListWhenNoItemRequests() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterId(1L)).thenReturn(List.of());
        List<ItemRequestDto> result = itemRequestService.getOwnById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return a list of item requests for the given requester ID")
    void getOwnByIdReturnsListOfItemRequests() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test");
        itemRequestDto.setCreated(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestMapper.toDtos(any())).thenReturn(List.of(itemRequestDto));
        List<ItemRequestDto> result = itemRequestService.getOwnById(1L);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    @DisplayName("should throw UserNotFoundException when requesterId is not found")
    void createRequestWhenRequesterIdNotFoundThenThrowUserNotFoundException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test");
        itemRequestDto.setCreated(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.createRequest(itemRequestDto, 1L));
    }

    @Test
    @DisplayName("should create a new request with valid requesterId and itemRequestDto")
    void createRequestWithValidRequesterIdAndItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна книга");
        itemRequestDto.setCreated(LocalDateTime.now());

        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@mail.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна книга");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestMapper.toEntity(itemRequestDto)).thenReturn(itemRequest);
        when(requestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(requestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actual = itemRequestService.createRequest(itemRequestDto, 1L);

        assertEquals(itemRequestDto, actual);
    }
}