package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.groups.Create;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final BookingService bookingService;

    private final CommentsService commentsService;

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long id,
                            @RequestBody @Validated(Create.class) ItemDto item) {
        return itemService.addItem(item, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @RequestBody ItemDto item) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        if (itemService.getItemsByOwnerId(requesterId).contains(itemService.getItemById(itemId))) {
            return bookingService.setLastAndNextBooking(itemService.getItemById(itemId));
        } else {
            return itemService.getItemById(itemId);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text,
                                    @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.searchItem(text, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        List<ItemDto> dtos = itemService.getItemsByOwnerId(ownerId);
        for (ItemDto itemDto : dtos) {
            bookingService.setLastAndNextBooking(itemDto);
        }
        return dtos;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @PathVariable Long itemId,
                                  @RequestBody @Validated(Create.class) Comment comment) {
        return commentsService.createComment(itemId, ownerId, comment);
    }
}
