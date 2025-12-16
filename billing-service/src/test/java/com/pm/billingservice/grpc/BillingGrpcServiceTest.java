package com.pm.billingservice.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import billing.BillingRequest;
import billing.BillingResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingGrpcService Tests")
class BillingGrpcServiceTest {

  @InjectMocks
  private BillingGrpcService billingGrpcService;

  @Mock
  private StreamObserver<BillingResponse> responseObserver;

  private BillingRequest billingRequest;

  @BeforeEach
  void setUp() {
    billingRequest = BillingRequest.newBuilder()
        .setPatientId("12345")
        .setName("John Doe")
        .setEmail("john.doe@example.com")
        .build();
  }

  @Test
  @DisplayName("Should create billing account successfully")
  void testCreateBillingAccount() {
    billingGrpcService.createBillingAccount(billingRequest, responseObserver);

    ArgumentCaptor<BillingResponse> responseCaptor =
        ArgumentCaptor.forClass(BillingResponse.class);
    verify(responseObserver).onNext(responseCaptor.capture());
    verify(responseObserver).onCompleted();

    BillingResponse response = responseCaptor.getValue();
    assertNotNull(response);
    assertEquals("12345", response.getAccountId());
    assertEquals("ACTIVE", response.getStatus());
  }
}

