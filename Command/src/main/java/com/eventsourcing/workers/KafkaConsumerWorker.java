//package com.eventsourcing.workers;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.netflix.conductor.client.worker.Worker;
//import com.netflix.conductor.common.metadata.tasks.Task;
//import com.netflix.conductor.common.metadata.tasks.TaskResult;
//import java.time.Duration;
//import java.util.*;
//
//import lombok.AllArgsConstructor;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Component;
//
//@AllArgsConstructor
//@Component
//@ComponentScan(basePackages = {"com.netflix.conductor"})
//public class KafkaConsumerWorker implements Worker {
//
//
//
//    public String getTaskDefName() {
//        return "kafka_consumer_task";
//    }
//
//    @Override
//    public TaskResult execute(Task task) {
//        TaskResult result = new TaskResult(task);
//        KafkaConsumer<String, String> consumer = null;
//
//        try {
//            Map<String, Object> inputData = task.getInputData();
//            String bootstrapServers = (String) inputData.get("bootstrapServers");
//            String topic = (String) inputData.get("topic");
//            String groupId = (String) inputData.get("groupId");
//
//            // Configure Kafka Consumer
//            Properties props = new Properties();
//            props.put("bootstrap.servers", bootstrapServers);
//            props.put("group.id", groupId);
//            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//            props.put("auto.offset.reset", "latest");
//            props.put("enable.auto.commit", "true");
//
//            // Create consumer
//            consumer = new KafkaConsumer<>(props);
//            consumer.subscribe(Collections.singletonList(topic));
//
//            // Poll for records
//            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
//
//            List<Map<String, Object>> processedMessages = new ArrayList<>();
//
//            for (ConsumerRecord<String, String> record : records) {
//                Map<String, Object> message = new HashMap<>();
//                message.put("offset", record.offset());
//                message.put("partition", record.partition());
//                message.put("key", record.key());
//                message.put("value", record.value());
//                message.put("timestamp", record.timestamp());
//
//                // Add any custom processing logic here
//                // For example, parsing JSON:
//                try {
//                    ObjectMapper mapper = new ObjectMapper();
//                    JsonNode jsonNode = mapper.readTree(record.value());
//                    message.put("processedData", jsonNode);
//                } catch (Exception e) {
//                    message.put("parseError", e.getMessage());
//                }
//
//                processedMessages.add(message);
//            }
//
//            // Set the processed data as output
//            result.getOutputData().put("processedMessages", processedMessages);
//            result.setStatus(TaskResult.Status.COMPLETED);
//
//        } catch (Exception e) {
//            result.setStatus(TaskResult.Status.FAILED);
//            result.setReasonForIncompletion("Error processing Kafka messages: " + e.getMessage());
//        } finally {
//            if (consumer != null) {
//                consumer.close();
//            }
//        }
//
//        return result;
//    }
//}