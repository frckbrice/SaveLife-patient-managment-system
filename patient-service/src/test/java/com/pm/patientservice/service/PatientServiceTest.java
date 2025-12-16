package com.pm.patientservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Tests")
class PatientServiceTest {

  @Mock
  private PatientRepository patientRepository;

  @Mock
  private BillingServiceGrpcClient billingServiceGrpcClient;

  @Mock
  private KafkaProducer kafkaProducer;

  @InjectMocks
  private PatientService patientService;

  private Patient testPatient;
  private PatientRequestDTO patientRequestDTO;
  private UUID patientId;

  @BeforeEach
  void setUp() {
    patientId = UUID.randomUUID();
    testPatient = new Patient();
    testPatient.setId(patientId);
    testPatient.setName("John Doe");
    testPatient.setEmail("john.doe@example.com");
    testPatient.setDateOfBirth(LocalDate.of(1990, 1, 15));
    testPatient.setAddress("123 Main St");

    patientRequestDTO = new PatientRequestDTO();
    patientRequestDTO.setName("John Doe");
    patientRequestDTO.setEmail("john.doe@example.com");
    patientRequestDTO.setDateOfBirth("1990-01-15");
    patientRequestDTO.setAddress("123 Main St");
  }

  @Test
  @DisplayName("Should retrieve all patients successfully")
  void testGetPatients() {
    List<Patient> patients = List.of(testPatient);
    when(patientRepository.findAll()).thenReturn(patients);

    List<PatientResponseDTO> result = patientService.getPatients();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPatient.getName(), result.get(0).getName());
    verify(patientRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should create patient successfully")
  void testCreatePatient() {
    when(patientRepository.existsByEmail(patientRequestDTO.getEmail())).thenReturn(false);
    when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

    PatientResponseDTO result = patientService.createPatient(patientRequestDTO);

    assertNotNull(result);
    assertEquals(testPatient.getName(), result.getName());
    assertEquals(testPatient.getEmail(), result.getEmail());
    verify(patientRepository, times(1)).existsByEmail(patientRequestDTO.getEmail());
    verify(patientRepository, times(1)).save(any(Patient.class));
    verify(billingServiceGrpcClient, times(1)).createBillingAccount(
        eq(patientId.toString()), eq(testPatient.getName()), eq(testPatient.getEmail()));
    verify(kafkaProducer, times(1)).sendEvent(testPatient);
  }

  @Test
  @DisplayName("Should throw exception when email already exists")
  void testCreatePatientWithExistingEmail() {
    when(patientRepository.existsByEmail(patientRequestDTO.getEmail())).thenReturn(true);

    assertThrows(EmailAlreadyExistsException.class,
        () -> patientService.createPatient(patientRequestDTO));

    verify(patientRepository, times(1)).existsByEmail(patientRequestDTO.getEmail());
    verify(patientRepository, never()).save(any(Patient.class));
    verify(billingServiceGrpcClient, never()).createBillingAccount(any(), any(), any());
    verify(kafkaProducer, never()).sendEvent(any());
  }

  @Test
  @DisplayName("Should update patient successfully")
  void testUpdatePatient() {
    PatientRequestDTO updateRequest = new PatientRequestDTO();
    updateRequest.setName("Jane Doe");
    updateRequest.setEmail("jane.doe@example.com");
    updateRequest.setDateOfBirth("1992-05-20");
    updateRequest.setAddress("456 Oak Ave");

    when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
    when(patientRepository.existsByEmailAndIdNot(updateRequest.getEmail(), patientId))
        .thenReturn(false);
    when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

    PatientResponseDTO result = patientService.updatePatient(patientId, updateRequest);

    assertNotNull(result);
    verify(patientRepository, times(1)).findById(patientId);
    verify(patientRepository, times(1)).existsByEmailAndIdNot(
        updateRequest.getEmail(), patientId);
    verify(patientRepository, times(1)).save(any(Patient.class));
  }

  @Test
  @DisplayName("Should throw exception when patient not found for update")
  void testUpdatePatientNotFound() {
    when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

    assertThrows(PatientNotFoundException.class,
        () -> patientService.updatePatient(patientId, patientRequestDTO));

    verify(patientRepository, times(1)).findById(patientId);
    verify(patientRepository, never()).save(any(Patient.class));
  }

  @Test
  @DisplayName("Should delete patient successfully")
  void testDeletePatient() {
    patientService.deletePatient(patientId);

    verify(patientRepository, times(1)).deleteById(patientId);
  }
}

