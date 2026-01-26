package com.billbuddies.billbuddies_backend.exception;

import com.billbuddies.billbuddies_backend.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleGroupNotFound(
            GroupNotFoundException ex
    ) {
        log.warn("GroupNotFoundException: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MemberAlreadyInGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberAlreadyInGroup(
            MemberAlreadyInGroupException ex
    ) {
        log.warn("MemberAlreadyInGroupException: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex
    ) {
        log.error("Unhandled exception occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Something went wrong. Please try again later."
                ));
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotFound(
            MemberNotFoundException ex
    ) {
        log.warn("MemberNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(404, ex.getMessage()));
    }

    @ExceptionHandler(MemberNotInGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotInGroup(
            MemberNotInGroupException ex
    ) {
        log.warn("MemberNotInGroupException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, ex.getMessage()));
    }
    @ExceptionHandler(GroupAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleGroupAlreadyExists(
            GroupAlreadyExistsException ex
    ) {
        log.warn("GroupAlreadyExistsException: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }
    @ExceptionHandler(CcpCannotBeAddedException.class)
    public ResponseEntity<ErrorResponseDto> handleCcpCannotBeAdded(
            CcpCannotBeAddedException ex) {

        log.warn("CcpCannotBeAddedException handled: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(CcpCannotBeRemovedException.class)
    public ResponseEntity<ErrorResponseDto> handleCcpCannotBeRemoved(
            CcpCannotBeRemovedException ex) {

        log.warn("CcpCannotBeRemovedException handled: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }
}
