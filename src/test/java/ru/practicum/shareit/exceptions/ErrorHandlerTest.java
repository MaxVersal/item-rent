package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when RequestNotFoundException is thrown")
    void handleRequestNotFoundException() {
        RequestNotFoundException requestNotFoundException =
                new RequestNotFoundException("Request not found");
        ErrorResponse errorResponse =
                errorHandler.handleRequestNotFoundException(requestNotFoundException);
        assertEquals("Request not found", errorResponse.getError());
    }

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when SelfBookingException is thrown")
    void handleSelfBookingExceptionReturnsErrorResponseWithCorrectMessage() {
        String message = "message";
        SelfBookingException selfBookingException = new SelfBookingException(message);
        ErrorResponse errorResponse = errorHandler.handleSelfBookingException(selfBookingException);
        assertEquals(message, errorResponse.getError());
    }

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when NoSuchElementException is thrown")
    void handleNotFoundExceptionWhenNoSuchElementExceptionIsThrown() {
        NoSuchElementException exception = new NoSuchElementException("test");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);
        assertEquals("test", errorResponse.getError());
    }

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when IncorrectBookingException is thrown")
    void handleIncorrectBookingException() {
        IncorrectBookingException exception = new IncorrectBookingException("Incorrect booking");

        ErrorResponse errorResponse = errorHandler.handleIncorrectBooking(exception);

        assertEquals("Incorrect booking", errorResponse.getError());
    }

    @Test
    @DisplayName("should return ErrorResponse with IllegalArgumentException message")
    void handleIllegalArgumentExceptionReturnsErrorResponseWithMessage() {
        IllegalArgumentException exception =
                new IllegalArgumentException("IllegalArgumentException");
        ErrorResponse errorResponse = errorHandler.handleIllegalArgumentException(exception);
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when IncorrectItemException is thrown")
    void handleIncorrectItemExceptionReturnsErrorResponseWithCorrectMessage() {
        IncorrectItemException exception = new IncorrectItemException("Incorrect item");
        ErrorResponse errorResponse = errorHandler.handleIncorrectItemException(exception);
        assertEquals("Incorrect item", errorResponse.getError());
    }

    @Test
    @DisplayName("should return ErrorResponse with UserNotFoundException message")
    void handleUserNotFoundException() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("User not found");
        ErrorResponse errorResponse =
                errorHandler.handleUserNotFoundException(userNotFoundException);
        assertEquals("User not found", errorResponse.getError());
    }

    @Test
    @DisplayName(
            "should return ErrorResponse with correct message when WrongDataUpdateException is thrown")
    void handleIncorrectUserExceptionWhenWrongDataUpdateExceptionIsThrown() {
        String message = "message";
        WrongDataUpdateException exception = new WrongDataUpdateException(message);
        ErrorResponse errorResponse = errorHandler.handleIncorrectUserException(exception);
        assertEquals(message, errorResponse.getError());
    }
}