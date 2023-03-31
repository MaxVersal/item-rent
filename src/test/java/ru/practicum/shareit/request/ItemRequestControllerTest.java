package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequest;

    @BeforeEach
    public void setUp() {
        itemRequest = new ItemRequestDto();
        this.itemRequest.setId(1L);
        this.itemRequest.setDescription("test");
        this.itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    @DisplayName("should post request")
    public void shouldPostRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");


        when(itemRequestService.createRequest(any(), anyLong())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content("{\"description\" : \"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()));
    }

    @Test
    @DisplayName("should return request by Id")
    public void findOwnById() throws Exception {
        when(itemRequestService.getOwnById(anyLong())).thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequest.getDescription()))
                .andExpect((jsonPath("$[0].id").value(itemRequest.getId())));
    }

    @Test
    @DisplayName("get all")
    public void shouldReturnAllRequests() throws Exception {
        when(itemRequestService.getAllRequests()).thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequest.getDescription()))
                .andExpect((jsonPath("$[0].id").value(itemRequest.getId())));
    }

    @Test
    @DisplayName("get all with params")
    public void shouldReturnAllWithParams() throws Exception {
        when(itemRequestService.gellAllRequestsWithParams(any(), anyLong())).thenReturn(List.of(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "2")
                        .param("size", "2")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequest.getDescription()))
                .andExpect((jsonPath("$[0].id").value(itemRequest.getId())));
    }

    @Test
    @DisplayName("throw WrongPageDateException")
    public void shouldThrowExceptionWithWrongFromOrSize() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-2")
                        .param("size", "0")
                        .contentType("application/json"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("should return item by id")
    public void shouldReturnRequestById() throws Exception {
        when(itemRequestService.getRequestWithItems(anyLong(), anyLong())).thenReturn(itemRequest);

        mockMvc.perform(get("/requests/1")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()));
    }
}