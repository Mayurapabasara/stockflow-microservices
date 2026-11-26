package com.stockflow.product.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void returnsSanitizedConflictForDatabaseConstraintViolation() {
        ProblemDetail problem = handler.handleDataConflict(
                new DataIntegrityViolationException("database constraint details"));

        assertEquals(HttpStatus.CONFLICT.value(), problem.getStatus());
        assertEquals("Data conflict", problem.getTitle());
        assertEquals("The request conflicts with existing data.", problem.getDetail());
        assertFalse(problem.getDetail().contains("database"));
    }
}
