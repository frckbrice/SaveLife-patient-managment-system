package com.pm.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthService authService;

  private User testUser;
  private LoginRequestDTO loginRequestDTO;
  private String validToken;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setEmail("test@example.com");
    testUser.setPassword("$2a$10$encodedPassword");
    testUser.setRole("USER");

    loginRequestDTO = new LoginRequestDTO();
    loginRequestDTO.setEmail("test@example.com");
    loginRequestDTO.setPassword("password123");

    validToken = "valid.jwt.token";
  }

  @Test
  @DisplayName("Should authenticate user with valid credentials")
  void testAuthenticateWithValidCredentials() {
    when(userService.findByEmail(loginRequestDTO.getEmail()))
        .thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(loginRequestDTO.getPassword(), testUser.getPassword()))
        .thenReturn(true);
    when(jwtUtil.generateToken(testUser.getEmail(), testUser.getRole()))
        .thenReturn(validToken);

    Optional<String> result = authService.authenticate(loginRequestDTO);

    assertTrue(result.isPresent());
    assertEquals(validToken, result.get());
    verify(userService, times(1)).findByEmail(loginRequestDTO.getEmail());
    verify(passwordEncoder, times(1)).matches(
        loginRequestDTO.getPassword(), testUser.getPassword());
    verify(jwtUtil, times(1)).generateToken(testUser.getEmail(), testUser.getRole());
  }

  @Test
  @DisplayName("Should return empty when user not found")
  void testAuthenticateUserNotFound() {
    when(userService.findByEmail(loginRequestDTO.getEmail()))
        .thenReturn(Optional.empty());

    Optional<String> result = authService.authenticate(loginRequestDTO);

    assertFalse(result.isPresent());
    verify(userService, times(1)).findByEmail(loginRequestDTO.getEmail());
    verify(passwordEncoder, never()).matches(anyString(), anyString());
    verify(jwtUtil, never()).generateToken(anyString(), anyString());
  }

  @Test
  @DisplayName("Should return empty when password is incorrect")
  void testAuthenticateIncorrectPassword() {
    when(userService.findByEmail(loginRequestDTO.getEmail()))
        .thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(loginRequestDTO.getPassword(), testUser.getPassword()))
        .thenReturn(false);

    Optional<String> result = authService.authenticate(loginRequestDTO);

    assertFalse(result.isPresent());
    verify(userService, times(1)).findByEmail(loginRequestDTO.getEmail());
    verify(passwordEncoder, times(1)).matches(
        loginRequestDTO.getPassword(), testUser.getPassword());
    verify(jwtUtil, never()).generateToken(anyString(), anyString());
  }

  @Test
  @DisplayName("Should validate valid token")
  void testValidateValidToken() {
    when(jwtUtil.validateToken(validToken)).thenReturn(null);

    boolean result = authService.validateToken(validToken);

    assertTrue(result);
    verify(jwtUtil, times(1)).validateToken(validToken);
  }

  @Test
  @DisplayName("Should return false for invalid token")
  void testValidateInvalidToken() {
    String invalidToken = "invalid.token";
    when(jwtUtil.validateToken(invalidToken)).thenThrow(new JwtException("Invalid token"));

    boolean result = authService.validateToken(invalidToken);

    assertFalse(result);
    verify(jwtUtil, times(1)).validateToken(invalidToken);
  }
}

