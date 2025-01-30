package com.example.login_app_backend.services;

import com.example.login_app_backend.dtos.AuthenticateDTO;
import com.example.login_app_backend.dtos.TokenDTO;
import com.example.login_app_backend.entities.User;
import com.example.login_app_backend.infra.security.TokenService;
import com.example.login_app_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    public TokenDTO login(AuthenticateDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authenticate = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User) authenticate.getPrincipal());

        return new TokenDTO(token);
    }

    public void register(AuthenticateDTO data) {
        if (userRepository.findByEmail(data.email()) != null) {
            throw new RuntimeException("User already exists.");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.email(), encryptedPassword);
        userRepository.save(newUser);
    }
}
