package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
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
    @DisplayName("should return an empty list when no requests are found with the given parameters")
    void getAllRequestsWithParamsWhenNoRequestsFound() {
        Long requesterId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(requestRepository.findAll(requesterId, pageable)).thenReturn(List.of());
        List<ItemRequestDto> requests =
                itemRequestService.gellAllRequestsWithParams(pageable, requesterId);
        assertTrue(requests.isEmpty());
    }

    @Test
    @DisplayName("should return all requests with pagination when requesterId is provided")
    void getAllRequestsWithParamsWhenRequesterIdIsProvided() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test");
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test");
        itemRequestDto.setCreated(LocalDateTime.now());
        when(requestRepository.findAll(1L, PageRequest.of(0, 10))).thenReturn(List.of(itemRequest));
        when(requestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);
        List<ItemRequestDto> result =
                itemRequestService.gellAllRequestsWithParams(PageRequest.of(0, 10), 1L);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    @DisplayName("should throw RequestNotFoundException when the requestId is not found")
    void getRequestWithItemsWhenRequestIdNotFoundThenThrowRequestNotFoundException() {
        Long requesterId = 1L;
        Long requestId = 2L;
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getRequestWithItems(requesterId, requestId));
    }

    @Test
    @DisplayName("should throw UserNotFoundException when the requesterId is not found")
    void getRequestWithItemsWhenRequesterIdNotFoundThenThrowUserNotFoundException() {
        Long requesterId = 1L;
        Long requestId = 2L;
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());
        Throwable exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> itemRequestService.getRequestWithItems(requesterId, requestId));
        assertEquals("Не найден пользователь с указанным id", exception.getMessage());
    }

    @Test
    @DisplayName(
            "should return the request with items when the requesterId and requestId are valid")
    void getRequestWithItemsWhenRequesterIdAndRequestIdAreValid() {
        Long requesterId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(requesterId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(requestId);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);
        ItemRequestDto actual = itemRequestService.getRequestWithItems(requesterId, requestId);
        assertEquals(itemRequestDto, actual);
    }

    @Test
    @DisplayName("should return all requests sorted by creation date")
    void getAllRequestsSortedByCreationDate() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.of(2020, 1, 1, 0, 0));
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.of(2020, 1, 2, 0, 0));
        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setCreated(LocalDateTime.of(2020, 1, 3, 0, 0));
        when(requestRepository.findAll())
                .thenReturn(List.of(itemRequest1, itemRequest2, itemRequest3));
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setCreated(LocalDateTime.of(2020, 1, 1, 0, 0));
        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setCreated(LocalDateTime.of(2020, 1, 2, 0, 0));
        ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setCreated(LocalDateTime.of(2020, 1, 3, 0, 0));
        when(requestMapper.toDto(itemRequest1)).thenReturn(itemRequestDto1);
        when(requestMapper.toDto(itemRequest2)).thenReturn(itemRequestDto2);
        when(requestMapper.toDto(itemRequest3)).thenReturn(itemRequestDto3);
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests();
        assertEquals(3, allRequests.size());
        assertEquals(itemRequestDto1, allRequests.get(0));
        assertEquals(itemRequestDto2, allRequests.get(1));
        assertEquals(itemRequestDto3, allRequests.get(2));
    }

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