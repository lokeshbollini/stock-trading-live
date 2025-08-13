package com.stocktrade.controller;

import com.stocktrade.entity.User;
import com.stocktrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCashBalance(),
                user.getCreatedAt()
        );
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal User currentUser,
                                         @RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = userService.updateUserProfile(
                    currentUser.getId(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail()
            );
            
            UserProfileResponse profile = new UserProfileResponse(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    updatedUser.getCashBalance(),
                    updatedUser.getCreatedAt()
            );
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Profile update failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User currentUser,
                                          @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(
                    currentUser.getId(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(new SuccessResponse("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password change failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/me/cash/add")
    public ResponseEntity<?> addCash(@AuthenticationPrincipal User currentUser,
                                   @RequestBody CashTransactionRequest request) {
        try {
            User updatedUser = userService.addCash(currentUser.getId(), request.getAmount());
            return ResponseEntity.ok(new CashBalanceResponse(updatedUser.getCashBalance()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Add cash failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/me/cash")
    public ResponseEntity<CashBalanceResponse> getCashBalance(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new CashBalanceResponse(currentUser.getCashBalance()));
    }
    
    @PutMapping("/me/cash")
    public ResponseEntity<?> updateCashBalance(@AuthenticationPrincipal User currentUser,
                                             @RequestBody CashTransactionRequest request) {
        try {
            User updatedUser = userService.updateCashBalance(currentUser.getId(), request.getAmount());
            return ResponseEntity.ok(new CashBalanceResponse(updatedUser.getCashBalance()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Cash balance update failed: " + e.getMessage()));
        }
    }
    
    public static class UserProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private BigDecimal cashBalance;
        private java.time.LocalDateTime createdAt;
        
        public UserProfileResponse(Long id, String username, String email, String firstName, 
                                  String lastName, BigDecimal cashBalance, java.time.LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.cashBalance = cashBalance;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public BigDecimal getCashBalance() { return cashBalance; }
        public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }
        
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
    
    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        private String email;
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class CashTransactionRequest {
        private BigDecimal amount;
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    
    public static class CashBalanceResponse {
        private BigDecimal cashBalance;
        
        public CashBalanceResponse(BigDecimal cashBalance) {
            this.cashBalance = cashBalance;
        }
        
        public BigDecimal getCashBalance() { return cashBalance; }
        public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
