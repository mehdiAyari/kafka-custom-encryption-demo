package com.kafka.custom.encryption.demo;

import com.kafka.custom.encryption.demo.consumer.PersonEventConsumer;
import com.kafka.custom.encryption.demo.service.EncryptionServiceInterface;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "kafka.topic.name=person_event_test"
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class KafkaCustomEncryptionDemoApplicationTests {

    private final EmbeddedKafkaBroker embeddedKafkaBroker;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final TestRestTemplate restTemplate;
    @SpyBean
    private final PersonEventConsumer personEventConsumer;
    @SpyBean
    private final EncryptionServiceInterface encryptionService;

    @BeforeEach
    public void setUp() {
        // Wait for the partitions to be assigned
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    public void testPersonEventHttpProducerConsumerWithEncryption() throws Exception {
        // Create a sample PersonEvent
        Map<String, Object> personEventMap = new HashMap<>();
        personEventMap.put("idPerson", 1);
        personEventMap.put("action", "CREATE");
        personEventMap.put("name", "John");
        personEventMap.put("lastname", "Doe");

        // Create a CountDownLatch to wait for the message to be processed
        CountDownLatch latch = new CountDownLatch(1);

        // Register a callback to count down the latch when a message is consumed
        Mockito.doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(personEventConsumer).consume(Mockito.any(ConsumerRecord.class));


        // Send HTTP POST request
        ResponseEntity<Void> response = restTemplate.postForEntity("/produce", personEventMap, Void.class);

        // Check if the request was successful
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Capture the ConsumerRecord using ArgumentCaptor
        ArgumentCaptor<ConsumerRecord<String, PersonEvent>> captor =
                ArgumentCaptor.forClass((Class) ConsumerRecord.class);

        boolean messageProcessed = latch.await(10, TimeUnit.SECONDS); // Adjust timeout as needed

        // Assert that the message was processed
        assertTrue(messageProcessed, "Message was not processed within the expected time");

        verify(personEventConsumer, timeout(5000).times(1)).consume(captor.capture());

        // Extract the captured ConsumerRecord
        ConsumerRecord<String, PersonEvent> receivedRecord = captor.getValue();
        PersonEvent receivedEvent = receivedRecord.value();

        // Verify the consumed event
        String receivedAction = receivedEvent.getAction().toString();
        String receivedName = receivedEvent.getName().toString();
        String receivedLastname = receivedEvent.getLastname().toString();

        // Verify the consumed event
        assertNotNull(receivedRecord);
        assertEquals(personEventMap.get("idPerson"), receivedEvent.getIdPerson());
        assertEquals(personEventMap.get("action"), receivedAction);
        assertEquals(personEventMap.get("name"), receivedName);
        assertEquals(personEventMap.get("lastname"), receivedLastname);

        verify(encryptionService, times(1)).encrypt(any(byte[].class));
        verify(encryptionService, times(1)).decrypt(any(byte[].class));
    }

}
