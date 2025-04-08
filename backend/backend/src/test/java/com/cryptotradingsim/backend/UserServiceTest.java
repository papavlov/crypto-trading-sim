package com.cryptotradingsim.backend;

import com.cryptotradingsim.backend.models.User;
import com.cryptotradingsim.backend.repositories.UserRepository;
import com.cryptotradingsim.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(new User(1, "alice", 10000), new User(2, "bob", 9500));
        when(userRepository.getAllUsers()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
    }

    @Test
    void testGetUserById() {
        User mockUser = new User(1, "testuser", 10000);
        when(userRepository.getUserById(1)).thenReturn(mockUser);

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(10000, result.getBalance());
    }

    @Test
    void testCreateUser() {
        userService.createUser("newuser");

        verify(userRepository).createUser("newuser", 10000.0);
    }

    @Test
    void testResetBalance() {
        userService.resetBalance(1);

        verify(userRepository).updateBalance(1, 10000.0);
    }

    @Test
    void testUpdateBalanceForBuy_Success() {
        User user = new User(1, "buyer", 5000.0);
        when(userRepository.getUserById(1)).thenReturn(user);

        boolean result = userService.updateBalanceForBuy(1, 3000.0);

        assertTrue(result);
        verify(userRepository).updateBalance(1, 2000.0);
    }

    @Test
    void testUpdateBalanceForBuy_Failure() {
        User user = new User(1, "buyer", 2000.0);
        when(userRepository.getUserById(1)).thenReturn(user);

        boolean result = userService.updateBalanceForBuy(1, 3000.0);

        assertFalse(result);
        verify(userRepository, never()).updateBalance(anyInt(), anyDouble());
    }

    @Test
    void testUpdateBalanceForSell() {
        User user = new User(1, "seller", 1000.0);
        when(userRepository.getUserById(1)).thenReturn(user);

        userService.updateBalanceForSell(1, 500.0);

        verify(userRepository).updateBalance(1, 1500.0);
    }
}
