package com.eventsourcing.service;

import io.orkes.conductor.client.WorkflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.orkes.conductor.client.OrkesClients;
import io.orkes.conductor.client.ApiClient;

@Configuration
public class ConductorConfig {
    @Bean
    public WorkflowClient workflowClient() {

        var apiClient = new ApiClient("https://developer.orkescloud.com/api", "4zh7bc8c8dfd-dbbd-11ef-8aad-762f7138a6de", "wYwQW8z0Ug7RVLzEHlACOAFngH5pQgsmXNNVhzQaY8pNnqvB");


        OrkesClients orkesClients = new  OrkesClients(new ApiClient("https://developer.orkescloud.com/api", "4zh7bc8c8dfd-dbbd-11ef-8aad-762f7138a6de", "wYwQW8z0Ug7RVLzEHlACOAFngH5pQgsmXNNVhzQaY8pNnqvB"));
        return orkesClients.getWorkflowClient();
    }
}
