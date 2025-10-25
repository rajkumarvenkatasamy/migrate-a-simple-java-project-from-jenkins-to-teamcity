package com.example.demo.service;

import com.example.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testGetUserById() {
        Optional<User> user = userService.getUserById(1L);
        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    @Test
    void testGetUserById_NotFound() {
        Optional<User> user = userService.getUserById(999L);
        assertFalse(user.isPresent());
    }

    @Test
    void testCreateUser() {
        User newUser = new User(null, "Test User", "test@example.com", "USER");
        User createdUser = userService.createUser(newUser);
        
        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("USER", createdUser.getRole());
        assertEquals(3, userService.getUserCount());
    }

    @Test
    void testUpdateUser() {
        User updatedData = new User(null, "Updated Name", "updated@example.com", "ADMIN");
        Optional<User> updatedUser = userService.updateUser(1L, updatedData);
        
        assertTrue(updatedUser.isPresent());
        assertEquals("Updated Name", updatedUser.get().getName());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
        assertEquals("ADMIN", updatedUser.get().getRole());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedData = new User(null, "Updated Name", "updated@example.com", "ADMIN");
        Optional<User> updatedUser = userService.updateUser(999L, updatedData);
        
        assertFalse(updatedUser.isPresent());
    }

    @Test
    void testDeleteUser() {
        boolean deleted = userService.deleteUser(1L);
        assertTrue(deleted);
        assertEquals(1, userService.getUserCount());
    }

    @Test
    void testDeleteUser_NotFound() {
        boolean deleted = userService.deleteUser(999L);
        assertFalse(deleted);
        assertEquals(2, userService.getUserCount());
    }

    @Test
    void testGetUserCount() {
        long count = userService.getUserCount();
        assertEquals(2, count);
    }
}
