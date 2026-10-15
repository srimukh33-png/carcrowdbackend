package com.carcrowd.dto.customer;

public class GoogleUserPayload {
    private String sub;
    private String email;
    private boolean emailVerified;
    private String name;
    private String picture;

    public GoogleUserPayload() {}

    public GoogleUserPayload(String sub, String email, boolean emailVerified, String name, String picture) {
        this.sub = sub;
        this.email = email;
        this.emailVerified = emailVerified;
        this.name = name;
        this.picture = picture;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
