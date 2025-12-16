package com.pm.patientservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PatientController.class)
@DisplayName("PatientController Tests")
class PatientControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PatientService patientService;

  @Autowired
  private ObjectMapper objectMapper;

  private PatientRequestDTO patientRequestDTO;
  private PatientResponseDTO patientResponseDTO;
  private UUID patientId;

  @BeforeEach
  void setUp() {
    patientId = UUID.randomUUID();

    patientRequestDTO = new PatientRequestDTO();
    patientRequestDTO.setName("John Doe");
    patientRequestDTO.setEmail("john.doe@example.com");
    patientRequestDTO.setDateOfBirth("1990-01-15");
    patientRequestDTO.setAddress("123 Main St");

    patientResponseDTO = new PatientResponseDTO();
    patientResponseDTO.setId(patientId);
    patientResponseDTO.setName("John Doe");
    patientResponseDTO.setEmail("john.doe@example.com");
    patientResponseDTO.setDateOfBirth("1990-01-15");
    patientResponseDTO.setAddress("123 Main St");
  }

  @Test
  @DisplayName("Should get all patients successfully")
  void testGetPatients() throws Exception {
    when(patientService.getPatients()).thenReturn(List.of(patientResponseDTO));

    mockMvc.perform(get("/patients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
  }

  @Test
  @DisplayName("Should create patient successfully")
  void testCreatePatient() throws Exception {
    when(patientService.createPatient(any(PatientRequestDTO.class)))
        .thenReturn(patientResponseDTO);

    mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patientRequestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  @DisplayName("Should update patient successfully")
  void testUpdatePatient() throws Exception {
    when(patientService.updatePatient(eq(patientId), any(PatientRequestDTO.class)))
        .thenReturn(patientResponseDTO);

    mockMvc.perform(put("/patients/" + patientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patientRequestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"));
  }

  @Test
  @DisplayName("Should delete patient successfully")
  void testDeletePatient() throws Exception {
    mockMvc.perform(delete("/patients/" + patientId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return bad request for invalid patient data")
  void testCreatePatientWithInvalidData() throws Exception {
    PatientRequestDTO invalidRequest = new PatientRequestDTO();
    invalidRequest.setName(""); // Invalid: empty name

    mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }
}

