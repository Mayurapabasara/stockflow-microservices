package com.stockflow.purchase.exception;
public class DependencyUnavailableException extends RuntimeException {
    public DependencyUnavailableException(String message, Throwable cause) { super(message, cause); }
}
