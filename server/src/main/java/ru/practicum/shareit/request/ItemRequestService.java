package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requesterId);

    List<ItemRequestDto> getOwnById(Long requesterId);

    ItemRequestDto getRequestWithItems(Long requesterId, Long requestId);

    List<ItemRequestDto> gellAllRequestsWithParams(Pageable pageable, Long requesteId);
}
