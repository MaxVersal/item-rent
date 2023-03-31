package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.exceptions.IncorrectItemException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private CommentsService commentsService;

    @Test
    @DisplayName("should post item")
    public void shouldReturnItemAfterAdding() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(true);
        item.setDescription("test");
        Long requesterId = 1L;

        when(itemService.addItem(item, requesterId)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content("{\"id\": \"1\", \"name\": \"test\", \"description\" : \"test\", \"available\" : \"true\"}")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
    }

    @Test
    @DisplayName("error without header")
    public void shouldThrowExceptionWithoutHeader() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(true);
        item.setDescription("test");

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content("{\"id\": \"1\", \"name\": \"test\", \"description\" : \"test\", \"available\" : \"true\"}")
                )
                .andExpect(status().is5xxServerError());

    }

    @Test
    @DisplayName("should throw exception without necessary fields")
    public void shouldThrowExceptionWithoutFields() throws Exception {
        ItemDto item = new ItemDto();

        when(itemService.addItem(item, 1L)).thenThrow(IncorrectItemException.class);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content("{ \"name\": \"test\", \"description\" : \"test\"}")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DisplayName("should return item with id")
    public void shouldDeleteItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(true);
        item.setDescription("test");

        when(itemService.getItemById(1L)).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
    }

    @Test
    @DisplayName("should throw exception when item not found")
    public void shouldThrowItemNotFoundException() throws Exception {
        when(itemService.getItemById(anyLong())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("should patch item")
    public void shouldPatchItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(false);
        item.setDescription("test");

        ItemDto patchItem = new ItemDto();
        patchItem.setAvailable(false);

        when(itemService.updateItem(patchItem, 1L, 1L)).thenReturn(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content("{\"available\" : \"false\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
    }

    @Test
    @DisplayName("should search item")
    public void shouldSearchItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(false);
        item.setDescription("test");

        when(itemService.searchItem("te", 1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .contentType("application/json")
                        .param("text", "te")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
                .andExpect(jsonPath("$[0].description").value(item.getDescription()))
                .andExpect(jsonPath("$[0].name").value(item.getName()));
    }

    @Test
    @DisplayName("should return items by user id")
    public void shouldReturnItemByUserId() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test");
        item.setAvailable(false);
        item.setDescription("test");
        item.setNextBooking(new BookingForItem(1L, 1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        when(itemService.getItemsByOwnerId(any())).thenReturn(List.of(item));
        when(bookingService.setLastAndNextBooking(item)).thenReturn(item);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
                .andExpect(jsonPath("$[0].description").value(item.getDescription()))
                .andExpect(jsonPath("$[0].name").value(item.getName()));

    }

    @Test
    @DisplayName("should post comment")
    public void shouldPostCommet() throws Exception {
        when(commentsService.createComment(any(), anyLong(), any())).thenReturn(new CommentDto(1L,
                "test",
                LocalDateTime.of(2024, 12, 12, 5, 5),
                "test"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\" : \"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.authorName").value("test"))
                .andExpect(jsonPath("$.text").value("test"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}
