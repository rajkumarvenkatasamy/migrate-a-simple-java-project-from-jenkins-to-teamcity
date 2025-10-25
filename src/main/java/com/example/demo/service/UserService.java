package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public UserService() {
        // Initialize with some sample data
        users.add(new User(counter.incrementAndGet(), "John Doe", "john.doe@example.com", "USER"));
        users.add(new User(counter.incrementAndGet(), "Jane Smith", "jane.smith@example.com", "ADMIN"));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User createUser(User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);
        return user;
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = getUserById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    public long getUserCount() {
        return users.size();
    }
}
