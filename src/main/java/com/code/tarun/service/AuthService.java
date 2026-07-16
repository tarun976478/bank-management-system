package com.code.tarun.service;

import com.code.tarun.dto.AuthResponse;
import com.code.tarun.dto.LoginRequest;
import com.code.tarun.dto.RegisterRequest;
import com.code.tarun.entity.Role;
import com.code.tarun.entity.Users;
import com.code.tarun.repository.UserRepository;
import com.code.tarun.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository repo;
    private final JwtService service;

    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {
        if(repo.existsByName(request.getName())) {
            return new AuthResponse(null,"Username is already taken!");
        }

        Users user = new Users();
        user.setName(request.getName());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        repo.save(user);


        String token = service.generateToken(user.getName());
        return new AuthResponse(token,"User registered successfully");
    }
    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        String token = service.generateToken(request.getUsername());

        return new AuthResponse(token,"Login Successful!");
    }
}
