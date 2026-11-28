package com.stockflow.reporting.event.consumer;
import com.stockflow.reporting.event.model.GoodsReceivedEvent;
import com.stockflow.reporting.service.PurchaseReportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name="stockflow.kafka.enabled",havingValue="true",matchIfMissing=true)
public class PurchaseEventConsumer {
    private final EventPayloadReader reader; private final PurchaseReportService service;
    public PurchaseEventConsumer(EventPayloadReader reader,PurchaseReportService service){this.reader=reader;this.service=service;}
    @KafkaListener(topics="${stockflow.kafka.topics.purchase-received:purchase.received}",groupId="${stockflow.kafka.group-id:reporting-service}")
    public void onEvent(String payload){service.record(reader.read(payload,GoodsReceivedEvent.class));}
}
