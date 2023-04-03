package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                           @RequestBody @Validated(Create.class) ItemDto item) {
        return itemClient.postItem(id, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestBody ItemDto item) {
        return itemClient.patchItem(ownerId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemClient.getItemById(requesterId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @Positive @RequestParam(required = false, defaultValue = "20") Integer size) {
        return itemClient.getItemsBySearch(ownerId, text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getItems(ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid CommentDto comment) {
        return itemClient.postComment(ownerId, itemId, comment);
    }
}
