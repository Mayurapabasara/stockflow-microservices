package com.stockflow.notification.exception;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException.class)
    ProblemDetail notFound(NotificationNotFoundException e) { return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail conflict(IllegalStateException e) { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage()); }
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail forbidden(AccessDeniedException e) { return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied"); }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail badRequest(Exception e) { return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request value"); }
}
