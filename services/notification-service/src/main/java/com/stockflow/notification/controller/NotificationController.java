package com.stockflow.notification.controller;
import java.util.UUID;
import com.stockflow.notification.dto.NotificationResponse;
import com.stockflow.notification.service.NotificationService;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }
    @GetMapping public Page<NotificationResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public NotificationResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping("/{id}/retry") public NotificationResponse retry(@PathVariable UUID id) { return service.retry(id); }
}
