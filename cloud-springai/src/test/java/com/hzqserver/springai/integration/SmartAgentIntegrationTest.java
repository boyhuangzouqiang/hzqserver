package com.hzqserver.springai.integration;

import com.hzqserver.springai.SpringAIApplication;
import com.hzqserver.springai.agent.TaskExecutionAgent;
import com.hzqserver.springai.service.ChatAgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SpringAIApplication.class)
public class SmartAgentIntegrationTest {

    @Autowired
    private ChatAgentService chatAgentService;

    @Autowired
    private TaskExecutionAgent taskExecutionAgent;

    @Test
    public void testServicesInjection() {
        assertNotNull(chatAgentService, "ChatAgentService should be injected");
        assertNotNull(taskExecutionAgent, "TaskExecutionAgent should be injected");
    }

}