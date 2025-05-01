package org.example.product_demo.service;

import org.example.product_demo.exception.ApiException;
import org.example.product_demo.exception.ResourceNotFoundException;
import org.example.product_demo.exception.UserAlreadyExistsException;
import org.example.product_demo.model.User;
import org.example.product_demo.model.UserRole;
import org.example.product_demo.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warning("User not found with username: " + username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.info("User found: " + username);
        return user;
    }

    public User registerNewUser(User user) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty() && userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        // Default role is USER for self-registration
        user.setRole(UserRole.USER);

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Registering new user: " + user.getUsername());
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User createUserByAdmin(User user) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty() && userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Admin creating new user: " + user.getUsername() + " with role: " + user.getRole());
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(Long id, String username, String email, String password, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if the new username is already taken by another user
        if (username != null && !user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        // Check if the new email is already taken by another user
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())
                && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        if (username != null) {
            user.setUsername(username);
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setRole(role);

        logger.info("Updating user: " + user.getId() + " with role: " + role);
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("Deleted user with id: " + id);
    }

    public boolean validateCredentials(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        logger.info("Password validation for " + username + ": " + matches);
        return matches;
    }
}