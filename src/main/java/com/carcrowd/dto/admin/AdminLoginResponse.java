package com.carcrowd.dto.admin;

public class AdminLoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private AdminUserDTO admin;

    public AdminLoginResponse() {}

    public AdminLoginResponse(String token, String tokenType, AdminUserDTO admin) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.admin = admin;
    }

    public AdminLoginResponse(String token, Long adminId, String email, String fullName, String role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.admin = new AdminUserDTO(adminId, email, fullName, role, true, java.time.LocalDateTime.now());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public AdminUserDTO getAdmin() {
        return admin;
    }

    public void setAdmin(AdminUserDTO admin) {
        this.admin = admin;
    }

    // Convenience getters for backwards compatibility
    public Long getAdminId() {
        return admin != null ? admin.getId() : null;
    }

    public String getEmail() {
        return admin != null ? admin.getEmail() : null;
    }

    public String getFullName() {
        return admin != null ? admin.getFullName() : null;
    }

    public String getRole() {
        return admin != null ? admin.getRole() : null;
    }
}
