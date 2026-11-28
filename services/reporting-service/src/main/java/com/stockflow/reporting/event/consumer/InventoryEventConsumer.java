package com.stockflow.reporting.event.consumer;
import com.stockflow.reporting.event.model.StockUpdatedEvent;
import com.stockflow.reporting.service.InventoryReportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name="stockflow.kafka.enabled",havingValue="true",matchIfMissing=true)
public class InventoryEventConsumer {
    private final EventPayloadReader reader; private final InventoryReportService service;
    public InventoryEventConsumer(EventPayloadReader reader,InventoryReportService service){this.reader=reader;this.service=service;}
    @KafkaListener(topics="${stockflow.kafka.topics.stock-updated:inventory.stock.updated}",groupId="${stockflow.kafka.group-id:reporting-service}")
    public void onEvent(String payload){service.record(reader.read(payload,StockUpdatedEvent.class));}
}
