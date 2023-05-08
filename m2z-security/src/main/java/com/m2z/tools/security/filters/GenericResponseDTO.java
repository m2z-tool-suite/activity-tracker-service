package com.m2z.tools.security.filters;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

class GenericResponseDTO {

    @Getter private final String message;
    @Getter private final int statusCode;
    @Getter
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime time;

    private GenericResponseDTO(String message, int statusCode, LocalDateTime time) {
        this.message = message;
        this.statusCode = statusCode;
        this.time = time;
    }

    public GenericResponseDTO(String message, int statusCode) {
        this(message, statusCode, LocalDateTime.now());
    }

    public static GenericResponseDTO ok() {
        return ok("Operation completed successfully");
    }

    public static GenericResponseDTO ok(final String message) {
        return createWithDefaultTime(message, HttpStatus.OK.value());
    }

    public static GenericResponseDTO created() {
        return created("Object created successfully");
    }

    public static GenericResponseDTO created(final String message) {
        return createWithDefaultTime(message, HttpStatus.CREATED.value());
    }

    public static GenericResponseDTO bad() {
        return createWithDefaultTime("Something went wrong", HttpStatus.BAD_REQUEST.value());
    }

    public static GenericResponseDTO notFound() {
        return createWithDefaultTime("Requested resource not found", HttpStatus.NOT_FOUND.value());
    }

    private static GenericResponseDTO createWithDefaultTime(final String message, final int statusCode) {
        return new GenericResponseDTO(message, statusCode);
    }
}
