package com.example.hybridchatserver.user;

import com.example.hybridchatserver.presence.PresenceService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PresenceService presenceService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @GetMapping("/contacts")
    public List<ContactDto> getContacts(Principal principal) {
        String currentUsername = principal.getName();
        
        return userRepository.findAll().stream()
            .filter(user -> !user.getUsername().equals(currentUsername)) 
            .map(user -> new ContactDto(
                user.getUsername(),
                presenceService.isUserOnline(user.getUsername())
            ))
            .collect(Collectors.toList());
    }

    @Data
    static class RegistrationRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class ContactDto {
        private String username;
        private boolean online;
    }
}
