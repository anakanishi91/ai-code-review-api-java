package com.example.aicodereviewapi.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.aicodereviewapi.entity.User;
import com.example.aicodereviewapi.exception.EmailAlreadyExistsException;
import com.example.aicodereviewapi.repository.UserRepository;
import com.example.aicodereviewapi.service.user.impl.UserServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    private User user;

    private User guestUser;

    @BeforeEach
    void setUp() {
        user =
                User.builder()
                        .id(UUID.randomUUID().toString())
                        .email("test@example.com")
                        .password("encodedPassword")
                        .isGuest(false)
                        .build();

        guestUser = User.builder().id(UUID.randomUUID().toString()).isGuest(true).build();
    }

    @Test
    void shouldSaveUserWhenRegisterWithEmailNotExists() {
        String rawPassword = "password123";

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.register(user.getEmail(), rawPassword);

        assertNotNull(savedUser);
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertFalse(savedUser.isGuest());
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisterWithEmailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        EmailAlreadyExistsException ex =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.register(user.getEmail(), "password123"));

        assertEquals("username already exists", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldSaveUserWhenCreateGuestUser() {
        when(userRepository.save(any(User.class))).thenReturn(guestUser);

        User savedUser = userService.createGuestUser();

        assertNotNull(savedUser);
        assertTrue(savedUser.isGuest());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnUserWhenFindByIdUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User found = userService.findById(user.getId());

        assertEquals(user.getEmail(), found.getEmail());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenFindByIdUserNotFound() {
        when(userRepository.findById("unknownId")).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> userService.findById("unknownId"));

        assertEquals("user not found", ex.getMessage());
    }

    @Test
    void shouldReturnUserWhenFindByEmailUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User found = userService.findByEmail(user.getEmail());

        assertEquals(user.getId(), found.getId());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenFindByEmailUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class, () -> userService.findByEmail("unknown@example.com"));

        assertEquals("user not found", ex.getMessage());
    }
}
