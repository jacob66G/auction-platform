package com.example.auction_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse(
                400,
                "Validation failed for the request",
                errors.toString(),
                LocalDateTime.now()
        );

        log.warn("Validation failed: {}", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationImagesException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationImagesException ex) {
        ErrorResponse response = new ErrorResponse(
                400,
                ex.getMessage(),
                "Validation failed for the request.",
                LocalDateTime.now()
        );

        log.warn("Bad request exception - {}: {}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            InvalidPasswordException.class,
            InvalidAuctionDurationException.class,
            InsufficientAmountException.class,
            InvalidBidderException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse(
                400,
                ex.getMessage(),
                "BAD REQUEST",
                LocalDateTime.now()
        );

        log.warn("Bad request exception - {}: {}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            DuplicateNameException.class,
            BidTooLowException.class,
            AuctionNotEditableException.class,
            ActionNotActiveException.class,
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            HasAssociationException.class,

    })
    ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse(
                409,
                ex.getMessage(),
                "CONFLICT",
                LocalDateTime.now()
        );

        log.warn("Conflict exception - {}: {}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                404,
                ex.getMessage(),
                "Resource not found",
                LocalDateTime.now()
        );

        log.warn("Resource not found: {}", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleAccessDenied() {
        ErrorResponse response = new ErrorResponse(
                401,
                "You do not have the necessary permissions to access this resource.",
                "Access denied",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        ErrorResponse response = new ErrorResponse(
                401,
                "Authentication is required to access this resource.",
                "Unauthorized",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                500,
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );

        log.error("Unexpected exception occurred at {} {}: ",
                request.getMethod(), request.getRequestURI(), ex);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
