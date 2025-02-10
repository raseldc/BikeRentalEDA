package com.eventsourcing.workers;

import com.eventsourcing.bankAccount.commands.RentBikeCommand;
import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.BikeDocumentState;
import com.eventsourcing.bankAccount.dto.RentBikeRequestConductor;
import com.eventsourcing.bankAccount.dto.RentBikeRequestDTO;
import com.eventsourcing.bankAccount.events.BikeCreatedEvent;
import com.eventsourcing.bankAccount.events.BikeRentEvent;
import com.eventsourcing.configuration.KafkaConfigProperties;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.Event;
import com.eventsourcing.es.EventBus;
import com.eventsourcing.es.RentalStatus;
import com.eventsourcing.es.SerializerUtils;
import com.eventsourcing.service.BookingRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@AllArgsConstructor
@Component
@ComponentScan(basePackages = {"io.orkes"})
//@ComponentScan(basePackages = {"com.netflix.conductor"})
public class ConductorWorkers {
    /**
     * Note: Using this setting, up to 5 tasks will run in parallel, with tasks being polled every 200ms
     */
    @WorkerTask(value = "book_ride", threadCount = 3, pollingInterval = 300)
    public TaskResult checkForBookingRideTask(BookingRequest bookingRequest) {
        System.out.println("Got It -------------------->");

        TaskResult result = new TaskResult();

        return result;
    }
    private final EventBus eventBus;

    @Autowired
    MongoService mongoService;

    @WorkerTask(value = "state_bd_update", threadCount = 3, pollingInterval = 300)
    public TaskResult bikeStatusUpdate(RentBikeRequestConductor rentBikeRequestDTO) {


        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(rentBikeRequestDTO.getBikeId())), BikeDocumentState.class).stream().findFirst();

        if (documentOptional.isEmpty())
            return null;

        final var document = documentOptional.get();
        // var stautus = RentalStatus.valueOf(rentBikeRequestDTO.status());
        document.setStatus(RentalStatus.REQUESTED);

        Map<String, Object> output = new HashMap<>();


        TaskResult result = new TaskResult();


        if (rentBikeRequestDTO != null) {

            final var aggregateID = UUID.randomUUID().toString();
            var command  = new RentBikeCommand(aggregateID, rentBikeRequestDTO.getBikeId(), rentBikeRequestDTO.getBikeType(), rentBikeRequestDTO.getLocation(),
                    rentBikeRequestDTO.getStartDate(), rentBikeRequestDTO.getEndDate());

            final var aggregate = new BikeAggregate(command.aggregateID());
            aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());

            final List<Event> aggregateEvents = new ArrayList<>(aggregate.getChanges());
            output.put("bikeId", rentBikeRequestDTO.getBikeId() +";"+aggregateID+";"+ BikeRentEvent.Bike_Rent_SagaStart);

//            final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(rentBikeRequestDTO.getBikeId());
//            output.put("eventsBytes", getBikeAggregateEvent(rentBikeRequestDTO));
//            output.put("eventsBytes1", aggregate.getChanges());


            result.setOutputData(output);

            result.setStatus(TaskResult.Status.COMPLETED);
        } else {
            output.put("bookingId", null);

            result.setStatus(TaskResult.Status.FAILED);
        }


        mongoService.getPrimaryMongoTemplate().save(document);
        result.setOutputData(output);




        return result;


    }

    private  byte[] getBikeAggregateEvent(RentBikeRequestConductor rentBikeRequestDTO) {
        final var aggregateID = UUID.randomUUID().toString();
        var command  = new RentBikeCommand(aggregateID, rentBikeRequestDTO.getBikeId(), rentBikeRequestDTO.getBikeType(), rentBikeRequestDTO.getLocation(),
                rentBikeRequestDTO.getStartDate(), rentBikeRequestDTO.getEndDate());

        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());
        final List<Event> aggregateEvents = new ArrayList<>(aggregate.getChanges());
      //  eventBus.publish(aggregate.getChanges());
        final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(aggregateEvents.toArray(new Event[]{}));
        return eventsBytes;
    }

    @WorkerTask(value = "worker2", threadCount = 3, pollingInterval = 300)
//    @KafkaListener(topics = "SagaTopic", groupId = "es_microservice")
//    public TaskResult worker2(@Payload byte[] data, ConsumerRecordMetadata meta, Acknowledgment ack) {
    public TaskResult worker2(RentBikeRequestConductor rentBikeRequestDTO) {

        String bikeid = "234";

//        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(rentBikeRequestDTO.getBikeId())), BikeDocumentState.class).stream().findFirst();

      //  final Event[] events = SerializerUtils.deserializeEventsFromJsonBytes(data);

        Map<String, Object> output = new HashMap<>();


        TaskResult result = new TaskResult();
      //  final byte[] eventsBytes = SerializerUtils.serializeToJsonBytes(events);
        output.put("bikeId", bikeid);
//        if(rentBikeRequestDTO != null) {
//            output.put("bikeId", rentBikeRequestDTO.getBikeId());
//            result.setOutputData(output);
//            result.setStatus(TaskResult.Status.COMPLETED);
//        } else {
//            output.put("bookingId", null);
//            result.setStatus(TaskResult.Status.FAILED);
//        }

            result.setStatus(TaskResult.Status.COMPLETED);
        result.setOutputData(output);
        return result;
    }


    @WorkerTask(value = "query_db_pending", threadCount = 3, pollingInterval = 300)
//    @EventHandler("ORDER_STATUS_CHANGED")
    public TaskResult checkForBookingRideTask(RentBikeRequestConductor rentBikeRequestDTO) {
        List<Integer> a = new ArrayList<>();
        a.remove(a.size()-1);
        final var aggregateID = UUID.randomUUID().toString();
        RentBikeCommand command = new RentBikeCommand(aggregateID, rentBikeRequestDTO.getBikeId(), "", "", "", "");

        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());
        var event = aggregate.getChanges();


        TaskResult result = new TaskResult();
        Map<String, Object> output = new HashMap<>();
        result.addOutputData("events", event);

        return result;
    }


    private final KafkaConfigProperties kafkaConfigProperties;
    final private ConsumerFactory<String, byte[]> consumerFactory ;
    @KafkaListener(topics = "SagaTopic", groupId = "es_microservice")
    @WorkerTask(value = "kafka_consumer_task", threadCount = 3, pollingInterval = 300)
    public TaskResult kafkaConsumer(Task task)
//    public TaskResult bankAccountMongoProjectionListener(@Payload byte[] data, ConsumerRecordMetadata meta, Acknowledgment ack)
    {



        try {
            TaskResult result = new TaskResult(task);
            KafkaConsumer<String, byte[]> consumer = null;
            Map<String, Object> inputData = task.getInputData();
            String bootstrapServers = (String) inputData.get("bootstrapServers");
            String topic = (String) inputData.get("topic");
            String groupId = (String) inputData.get("groupId");

            // Configure Kafka Consumer
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("group.id", groupId);
            props.put("key.deserializer", StringDeserializer.class);
            props.put("value.deserializer", ByteArrayDeserializer.class);
            props.put("auto.offset.reset", "latest");
            props.put("enable.auto.commit", kafkaConfigProperties.getEnableAutoCommit());



            // Create consumer
            consumer = new KafkaConsumer<>(props);


            consumer.subscribe(Collections.singletonList(topic));


            // Poll for records
            ConsumerRecords<String, byte[] > records = consumer.poll(Duration.ofMillis(90000));

          //  List<Map<String, byte[]>> processedMessages = new ArrayList<>();

            for (ConsumerRecord<String,byte[]> record : records) {
                Map<String, Object> message = new HashMap<>();
                message.put("offset", record.offset());
                message.put("partition", record.partition());
                message.put("key", record.key());
                message.put("value", record.value());
                message.put("timestamp", record.timestamp());

                // Add any custom processing logic here
                // For example, parsing JSON:
                try {
                    ObjectMapper mapper = new ObjectMapper();
//                    var v = SerializerUtils.serializeToJsonBytes(record.value());
                    final Event[] events = SerializerUtils.deserializeEventsFromJsonBytes(record.value());
//                    JsonNode jsonNode = mapper.readTree(record.value());
//                    message.put("processedData", jsonNode);
                } catch (Exception e) {
                    e.printStackTrace();
                    message.put("parseError", e.getMessage());
                }

              //  processedMessages.add(message);
            }

            // Set the processed data as output
           // result.getOutputData().put("processedMessages", processedMessages);
            result.setStatus(TaskResult.Status.COMPLETED);

        } catch (Exception e) {
            //result.setStatus(TaskResult.Status.FAILED);
           // result.setReasonForIncompletion("Error processing Kafka messages: " + e.getMessage());
        } finally {
//            if (consumer != null) {
//             //   consumer.close();
//            }
        }


        return null;
    }


}
