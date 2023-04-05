package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User currentUser = userRepository.findById(requesterId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с указанным id"));
        ItemRequest itemRequest = requestMapper.toEntity(itemRequestDto);
        itemRequest.setRequester(currentUser);
        return requestMapper.toDto(requestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getOwnById(Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() -> new UserNotFoundException("Не найден пользователь"));
        return requestMapper.toDtos(requestRepository.findAllByRequesterId(user.getId()));
    }

    public ItemRequestDto getRequestWithItems(Long requesterId, Long requestId) {
        userRepository.findById(requesterId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с указанным id"));
        return requestMapper.toDto(requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Не найден запрос")
        ));
    }

    public List<ItemRequestDto> gellAllRequestsWithParams(Pageable pageable, Long requesterId) {
        return requestRepository.findAll(requesterId, pageable).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }
}
