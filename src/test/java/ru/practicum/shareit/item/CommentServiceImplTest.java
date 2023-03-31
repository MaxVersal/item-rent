package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommentServiceImplTest {
    @MockBean
    private CommentsRepository commentsRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("should throw UserNotFoundException when the user does not exist")
    void createCommentWhenUserNotFound() {
        Long itemId = 1L;
        Long requesterId = 2L;
        Comment comment = new Comment("comment", new User());
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> commentService.createComment(itemId, requesterId, comment));
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when the user has not booked the item")
    void createCommentWhenUserHasNotBookedItem() {
        Long itemId = 1L;
        Long requesterId = 1L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(itemId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        Comment comment = new Comment("comment", new User());
        when(bookingRepository.findBookingsFromUserToItemWithStatus(itemId, requesterId))
                .thenReturn(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(itemId, requesterId, comment));
    }

    @Test
    @DisplayName("should create a comment when the user exists and has a valid booking")
    void createCommentWhenUserExistsAndHasValidBooking() {
        Long itemId = 1L;
        Long requesterId = 1L;
        Comment comment = new Comment("comment", new User());
        User user = new User();
        user.setId(requesterId);
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setEnd(LocalDateTime.now().minusDays(1));
        Item item = new Item();
        item.setId(itemId);
        item.setComments(new HashSet<>());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsFromUserToItemWithStatus(itemId, requesterId))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentsRepository.save(comment)).thenReturn(comment);
        CommentDto commentDto = commentService.createComment(itemId, requesterId, comment);
        assertEquals(commentDto.getAuthorName(), user.getName());
        assertEquals(commentDto.getText(), comment.getText());
    }
}