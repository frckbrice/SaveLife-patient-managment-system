package com.pm.analyticsservice.kafka;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import patient.events.PatientEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaConsumer Tests")
class KafkaConsumerTest {

  @InjectMocks
  private KafkaConsumer kafkaConsumer;

  private byte[] validEventBytes;
  private byte[] invalidEventBytes;

  @BeforeEach
  void setUp() {
    PatientEvent validEvent = PatientEvent.newBuilder()
        .setPatientId("12345")
        .setName("John Doe")
        .setEmail("john.doe@example.com")
        .build();
    validEventBytes = validEvent.toByteArray();
    invalidEventBytes = new byte[]{1, 2, 3, 4, 5};
  }

  @Test
  @DisplayName("Should consume valid patient event")
  void testConsumeValidEvent() {
    assertDoesNotThrow(() -> kafkaConsumer.consumeEvent(validEventBytes));
  }

  @Test
  @DisplayName("Should handle invalid event gracefully")
  void testConsumeInvalidEvent() {
    assertDoesNotThrow(() -> kafkaConsumer.consumeEvent(invalidEventBytes));
  }
}

