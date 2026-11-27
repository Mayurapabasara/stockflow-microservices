package com.stockflow.reporting.exception;
import org.springframework.http.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class,MethodArgumentTypeMismatchException.class})
    ProblemDetail badRequest(Exception e){return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        e instanceof IllegalArgumentException?e.getMessage():"Invalid request value");}
}
