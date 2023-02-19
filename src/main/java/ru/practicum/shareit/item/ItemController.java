package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long id,
                            @RequestBody ItemDto item) {
        return itemService.postItem(item, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @RequestBody ItemDto item) {
        return itemService.updateItem(item,ownerId,itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.searchItem(text, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }


}
