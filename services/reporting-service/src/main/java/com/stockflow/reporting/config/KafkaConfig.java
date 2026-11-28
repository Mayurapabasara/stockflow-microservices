package com.stockflow.reporting.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
@ConditionalOnProperty(name="stockflow.kafka.enabled",havingValue="true",matchIfMissing=true)
public class KafkaConfig {
    @Bean DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String,String> template){
        var recoverer=new DeadLetterPublishingRecoverer(template,(record,error)->new TopicPartition(record.topic()+".DLT",record.partition()));
        recoverer.setFailIfSendResultIsError(true);
        var handler=new DefaultErrorHandler(recoverer,new FixedBackOff(1000L,2L));
        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
    @Bean NewTopic salesTopic(@Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}")String n){return topic(n);}
    @Bean NewTopic inventoryTopic(@Value("${stockflow.kafka.topics.stock-updated:inventory.stock.updated}")String n){return topic(n);}
    @Bean NewTopic purchaseTopic(@Value("${stockflow.kafka.topics.purchase-received:purchase.received}")String n){return topic(n);}
    @Bean NewTopic salesDlt(@Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}")String n){return topic(n+".DLT");}
    @Bean NewTopic inventoryDlt(@Value("${stockflow.kafka.topics.stock-updated:inventory.stock.updated}")String n){return topic(n+".DLT");}
    @Bean NewTopic purchaseDlt(@Value("${stockflow.kafka.topics.purchase-received:purchase.received}")String n){return topic(n+".DLT");}
    private NewTopic topic(String n){return TopicBuilder.name(n).partitions(3).replicas(1).build();}
}
