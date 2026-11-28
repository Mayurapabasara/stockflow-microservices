package com.stockflow.notification.exception;
public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException() { super("Notification not found"); }
}
