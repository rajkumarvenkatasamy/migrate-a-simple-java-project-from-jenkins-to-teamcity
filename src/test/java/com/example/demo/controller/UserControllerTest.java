package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetAppInfo() throws Exception {
        mockMvc.perform(get("/api/users/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.environment").exists());
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "john@example.com", "USER"),
                new User(2L, "Jane Smith", "jane@example.com", "ADMIN")
        );
        
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com", "USER");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        User newUser = new User(3L, "New User", "new@example.com", "USER");
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        String userJson = """
                {
                    "name": "New User",
                    "email": "new@example.com",
                    "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New User")));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "Updated Name", "updated@example.com", "ADMIN");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(Optional.of(updatedUser));

        String userJson = """
                {
                    "name": "Updated Name",
                    "email": "updated@example.com",
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        when(userService.updateUser(eq(999L), any(User.class))).thenReturn(Optional.empty());

        String userJson = """
                {
                    "name": "Updated Name",
                    "email": "updated@example.com",
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        when(userService.deleteUser(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserCount() throws Exception {
        when(userService.getUserCount()).thenReturn(5L);

        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
