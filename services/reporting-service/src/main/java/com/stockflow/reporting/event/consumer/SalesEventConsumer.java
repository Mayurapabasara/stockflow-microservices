package com.stockflow.reporting.event.consumer;
import com.stockflow.reporting.event.model.SaleCompletedEvent;
import com.stockflow.reporting.service.SalesReportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name="stockflow.kafka.enabled",havingValue="true",matchIfMissing=true)
public class SalesEventConsumer {
    private final EventPayloadReader reader; private final SalesReportService service;
    public SalesEventConsumer(EventPayloadReader reader,SalesReportService service){this.reader=reader;this.service=service;}
    @KafkaListener(topics="${stockflow.kafka.topics.sale-completed:sale.fulfilled}",groupId="${stockflow.kafka.group-id:reporting-service}")
    public void onEvent(String payload){service.record(reader.read(payload,SaleCompletedEvent.class));}
}
