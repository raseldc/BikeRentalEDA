package com.eventsourcing.es;


import com.eventsourcing.bankAccount.events.BikeCreatedEvent;
import com.eventsourcing.bankAccount.events.BikeRentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;


    private final static long sendTimeout = 3000;

    @Value(value = "${order.kafka.topics.bank-account-event-store:bank-account-event-store}")
    private String bankAccountTopicName;

    @Override
    @NewSpan
    public void publish(@SpanTag("events") List<Event> events) {
        final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(events.toArray(new Event[]{}));
        final ProducerRecord<String, byte[]> record = new ProducerRecord<>(bankAccountTopicName, eventsBytes);
        if (events.get(0).getEventType().equals(BikeRentEvent.Bike_Rent_SagaUpdateStatusReadUpdate2)) {
            publishSaga(events);
            return;
        }
        else if (events.get(0).getEventType().equals(BikeRentEvent.Bike_Rent_PaymentCompleteFeedToSaga)) {
            publishSaga(events);
            return;
        }
        try {
            kafkaTemplate.send(record).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("publishing kafka record value >>>>> {}", new String(record.value()));

        } catch (Exception ex) {
            log.error("(KafkaEventBus) publish get timeout", ex);
            throw new RuntimeException(ex);
        }
    }

    //initialize saga event
    @Override
    @NewSpan
    public void publishSaga(@SpanTag("events") List<Event> events) {
        final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(events.toArray(new Event[]{}));
        final ProducerRecord<String, byte[]> record = new ProducerRecord<>("SagaTopic", eventsBytes);

        try {
            kafkaTemplate.send(record).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("publishing kafka record value >>>>> {}", new String(record.value()));

        } catch (Exception ex) {
            log.error("(KafkaEventBus) publish get timeout", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    @NewSpan
    public void publishSagaTest(@SpanTag("events") String data) {

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        Event e = new Event();
        e.setData(dataBytes);

         List<Event> events = new ArrayList<>();
        events.add(e);


        final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(events.toArray(new Event[]{}));
        final ProducerRecord<String, byte[]> record = new ProducerRecord<>("SagaTopic", eventsBytes);

        try {
            kafkaTemplate.send(record).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("publishing kafka record value >>>>> {}", new String(record.value()));

        } catch (Exception ex) {
            log.error("(KafkaEventBus) publish get timeout", ex);
            throw new RuntimeException(ex);
        }
    }
}
