package com.stockflow.sales.exception;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException e) { return problem(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException e) { return problem(HttpStatus.BAD_REQUEST, e.getMessage()); }
    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    ProblemDetail conflict(Exception e) { return problem(HttpStatus.CONFLICT,
        e instanceof IllegalStateException ? e.getMessage() : "Conflicting sales data"); }
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail forbidden(AccessDeniedException e) { return problem(HttpStatus.FORBIDDEN, "Access denied"); }
    @ExceptionHandler(DependencyUnavailableException.class)
    ProblemDetail unavailable(DependencyUnavailableException e) { return problem(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException e) {
        var detail = problem(HttpStatus.BAD_REQUEST, "Request validation failed");
        detail.setProperty("errors", e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (a, b) -> a)));
        return detail;
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail malformed(Exception e) { return problem(HttpStatus.BAD_REQUEST, "Invalid request value or payload"); }
    private ProblemDetail problem(HttpStatus status, String message) { return ProblemDetail.forStatusAndDetail(status, message); }
}
