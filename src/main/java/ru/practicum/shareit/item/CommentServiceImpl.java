package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentsService {
    private final CommentsRepository commentsRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final ItemMapper mapper;

    public CommentDto postComment(Long itemId, Long requesterId, Comment comment) {
        Optional<User> user = userRepository.findById(requesterId);
        List<Booking> currentBookings = bookingRepository.findBookingsFromUserToItemWithStatus(itemId, requesterId).stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED) && booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (user.isPresent()) {
            if (!currentBookings.isEmpty()) {
                Item item = itemRepository.findById(itemId).get();
                comment.setUser(user.get());
                comment.setCommentTime(LocalDateTime.now());
                comment.setItem(item);
                item.getComments().add(comment);
                return mapper.toCommentDto(commentsRepository.save(comment));
            } else {
                throw new IllegalArgumentException("Нужно бронировать предмет для того, чтобы оставить отзыв");
            }
        } else {
            throw new UserNotFoundException("Не найден пользователь с данным id");
        }
    }
}
