package com.stockflow.notification.service;
import java.util.UUID;
import com.stockflow.notification.dto.NotificationResponse;
import com.stockflow.notification.event.model.*;
import org.springframework.data.domain.*;
public interface NotificationService {
    void lowStock(String sourceKey, LowStockEvent event);
    void saleCompleted(SaleCompletedEvent event);
    void goodsReceived(GoodsReceivedEvent event);
    Page<NotificationResponse> getAll(Pageable pageable);
    NotificationResponse getById(UUID id);
    NotificationResponse retry(UUID id);
    void deliver(UUID id);
}
