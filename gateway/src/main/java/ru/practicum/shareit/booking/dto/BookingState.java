package ru.practicum.shareit.booking.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.BookingStatusException;

@Slf4j
public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static BookingState from(String stringState) {
		try {
			return BookingState.valueOf(stringState.toUpperCase());
		} catch (IllegalArgumentException exception) {
			log.info("словили исключение в дто классе");
			throw new BookingStatusException("Unknown state: " + stringState);
		}
	}
}
