package com.gramirez.quotes.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleQuoteNotFoundException_ShouldReturnNotFoundStatus() {
        String errorMessage = "Quote not found with id: 1";
        QuoteNotFoundException exception = new QuoteNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleQuoteNotFoundException(exception);
        ErrorResponse errorResponse = responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleAuthorNotFoundException_ShouldReturnNotFoundStatus() {
        String errorMessage = "Author not found with id: 1";
        AuthorNotFoundException exception = new AuthorNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleAuthorNotFoundException(exception);
        ErrorResponse errorResponse = responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatus() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        FieldError fieldError1 = new FieldError("quote", "text", "Text cannot be empty");
        FieldError fieldError2 = new FieldError("quote", "author", "Author cannot be null");

        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<Object> responseEntity = exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Text cannot be empty", errors.get("text"));
        assertEquals("Author cannot be null", errors.get("author"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorStatus() {
        String exceptionMessage = "Database connection failed";
        Exception exception = new RuntimeException(exceptionMessage);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleGlobalException(exception);
        ErrorResponse errorResponse = responseEntity.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("An unexpected error occurred: " + exceptionMessage, errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void errorResponse_Constructor_ShouldSetAllFields() {
        int status = 404;
        String message = "Resource not found";
        LocalDateTime timestamp = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(status, message, timestamp);

        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(timestamp, errorResponse.getTimestamp());
    }
}
