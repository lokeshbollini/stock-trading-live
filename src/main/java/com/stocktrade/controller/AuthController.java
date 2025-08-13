package com.stocktrade.controller;

import com.stocktrade.dto.AuthRequest;
import com.stocktrade.dto.AuthResponse;
import com.stocktrade.dto.RegisterRequest;
import com.stocktrade.entity.User;
import com.stocktrade.security.JwtTokenProvider;
import com.stocktrade.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = (User) authentication.getPrincipal();
            
            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), 
                                                     user.getEmail(), user.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid username or password"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Username is already taken!"));
            }
            
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email is already in use!"));
            }
            
            User user = userService.createUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName()
            );
            
            String jwt = tokenProvider.generateToken(user.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), 
                                                     user.getEmail(), user.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                return ResponseEntity.ok(new TokenValidationResponse(true, username));
            } else {
                return ResponseEntity.ok(new TokenValidationResponse(false, null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new TokenValidationResponse(false, null));
        }
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
        
        public TokenValidationResponse(boolean valid, String username) {
            this.valid = valid;
            this.username = username;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
    }
}
