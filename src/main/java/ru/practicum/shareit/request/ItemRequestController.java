package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.WrongPageDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    @Autowired
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                        @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsById(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.getOwnById(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestWithItems(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                              @PathVariable Long requestId) {
        return itemRequestService.getRequestWithItems(requesterId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                       @RequestParam(required = false) Integer from,
                                       @RequestParam(required = false) Integer size) {
        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new WrongPageDataException("В параметрах не могут быть указаны отрицательные числа!");
            } else {
                Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
                return itemRequestService.gellAllRequestsWithParams(page, requesterId);
            }
        } else {
            return itemRequestService.getAllRequests();
        }
    }
}
