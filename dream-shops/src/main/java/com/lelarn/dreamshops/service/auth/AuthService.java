package com.lelarn.dreamshops.service.auth;

import com.lelarn.dreamshops.model.Role;
import com.lelarn.dreamshops.model.User;
import com.lelarn.dreamshops.repository.UserRepository;
import com.lelarn.dreamshops.request.LoginRequest;
import com.lelarn.dreamshops.request.RegisterRequest;
import com.lelarn.dreamshops.response.AuthResponse;
import com.lelarn.dreamshops.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final com.lelarn.dreamshops.security.UserDetailsServiceImpl userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER) // Default role for new users
                .active(true)
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        var jwtToken = jwtTokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        var jwtToken = jwtTokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}