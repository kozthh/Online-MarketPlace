package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final RegisterRequest request =
            new RegisterRequest("Jane", "Doe", "jane@example.com", "password123");

    @Test
    void registersNewUserWithBuyerRole() {
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-password");

        User saved = new User();
        saved.setId(1L);
        saved.setFirstName(request.firstName());
        saved.setLastName(request.lastName());
        saved.setEmail(request.email());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.register(request);

        assertEquals(1L, response.id());
        assertEquals("Jane", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("jane@example.com", response.email());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Set.of(Role.BUYER), captor.getValue().getRoles());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void storesHashedPassword() {
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertNotEquals(request.password(), captor.getValue().getPasswordHash());
        assertEquals("hashed-password", captor.getValue().getPasswordHash());
    }

    @Test
    void normalizesEmailBeforeLookupAndSave() {
        RegisterRequest mixedCaseRequest =
                new RegisterRequest("Jane", "Doe", "  Jane@Example.COM  ", "password123");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode(mixedCaseRequest.password())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(mixedCaseRequest);

        verify(userRepository).existsByEmail("jane@example.com");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("jane@example.com", captor.getValue().getEmail());
    }
}
