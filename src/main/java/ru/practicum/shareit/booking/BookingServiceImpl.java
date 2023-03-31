package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAccept;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    private final BookingMapper mapper;

    public BookingDto createBooking(BookingAccept bookingAccept, Long bookerId) {
        if (!userRepository.findById(bookerId).isPresent()) {
            throw new UserNotFoundException("Не найден пользователь");
        }
        User booker = userRepository.findById(bookerId).get();
        Booking booking = mapper.toEntity(bookingAccept);
        booking.setBooker(booker);
        if (itemRepository.findById(bookingAccept.getItemId()).isPresent()) {
            booking.setItem(itemRepository.findById(bookingAccept.getItemId()).get());
        } else {
            throw new ItemNotFoundException("Не найден предмет с указанным id");
        }
        if (userRepository.findById(bookerId).get().getItems().contains(booking.getItem())) {
            throw new SelfBookingException("Нельзя бронировать вещь у себя же!");
        }
        try {
            checkBooking(booking);
            booking.setStatus(Status.WAITING);
            return mapper.toDto(bookingRepository.save(booking));
        } catch (Exception e) {
            throw new IncorrectBookingException(e.getMessage());
        }
    }

    public BookingDto patchBooking(Long requesterId, Boolean approved, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).get();
        if (userRepository.findById(requesterId).get().getItems().contains(booking.getItem())
                && !booking.getBooker().getId().equals(requesterId)) {
            if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
                throw new IllegalArgumentException("Статус уже обновлен");
            } else {
                if (approved) {
                    booking.setStatus(Status.APPROVED);
                } else {
                    booking.setStatus(Status.REJECTED);
                }
            }
        } else {
            throw new NoSuchElementException("Обновлять статус вещи может только ее владелец");
        }
        return mapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long bookingId, Long requesterId) {
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getBooker().getId().equals(requesterId)
                || (userRepository.findById(requesterId).get().getItems().contains(booking.getItem()))) {
            return mapper.toDto(booking);
        } else {
            throw new NoSuchElementException("Недоступно для просмотра");
        }
    }

    public List<BookingDto> getBookings(Long requesterId, String state) {
        if (userRepository.findById(requesterId).isPresent()) {
            switch (state) {
                case "ALL":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "FUTURE":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "PAST":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "WAITING":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.WAITING))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "REJECTED":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.REJECTED))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "APPROVED":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.APPROVED))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "CURRENT":
                    return bookingRepository.findAllByUserId(requesterId).stream()
                            .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                default:
                    throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
            }
        } else {
            throw new UserNotFoundException("Не найден пользователь с указанным id");
        }

    }

    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        if (userRepository.findById(ownerId).isPresent()) {
            switch (state) {
                case "ALL":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "FUTURE":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "PAST":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "WAITING":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.WAITING))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "REJECTED":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.REJECTED))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "APPROVED":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .map(mapper::toDto)
                            .filter(bookingDto -> bookingDto.getStatus().equals(Status.APPROVED))
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                case "CURRENT":
                    return bookingRepository.findBookingsForOwner(ownerId).stream()
                            .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now()))
                            .map(mapper::toDto)
                            .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                            .collect(Collectors.toList());
                default:
                    throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
            }
        } else {
            throw new UserNotFoundException("Не найден пользователь");
        }

    }

    @Override
    public ItemDto setLastAndNextBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : bookingRepository.findAllForItem(itemDto.getId())) {
            if (itemDto.getLastBooking() == null && booking.getStart().isBefore(now) && booking.getStatus().equals(Status.APPROVED)) {
                itemDto.setLastBooking(mapper.toBookingItem(booking));
            } else if (itemDto.getNextBooking() == null && booking.getStart().isAfter(now) && booking.getStatus().equals(Status.APPROVED)) {
                itemDto.setNextBooking(mapper.toBookingItem(booking));
            } else if (itemDto.getLastBooking() != null
                    && booking.getStart().isBefore(now)
                    && booking.getStart().isAfter(itemDto.getLastBooking().getStart())
                    && booking.getStatus().equals(Status.APPROVED)) {
                itemDto.setLastBooking(mapper.toBookingItem(booking));
            } else if (itemDto.getNextBooking() != null
                    && booking.getStart().isAfter(now)
                    && itemDto.getNextBooking().getStart().isAfter(booking.getStart())
                    && booking.getStatus().equals(Status.APPROVED)) {
                itemDto.setNextBooking(mapper.toBookingItem(booking));
            }
        }
        return itemDto;
    }

    public List<BookingDto> findAllBookingsWithParametres(Long requesterId, Pageable pageable) {
        List<BookingDto> current =  bookingRepository.findPageAllByUserId(requesterId, pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return current;
    }

    public List<BookingDto> findAllBookingsForOwnerWithParametres(Long ownerId, Pageable pageable) {
        List<BookingDto> current = bookingRepository.findPageBookingsForOwner(ownerId, pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return current;
    }

    public void checkBooking(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())
                || booking.getEnd().isBefore(LocalDateTime.now())
                || booking.getStart().equals(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongDataUpdateException("Некорректное время");
        } else if (!booking.getItem().getAvailable()) {
            throw new ItemAlreadyInUseException("Предмет уже забронирован");
        }
    }
}
