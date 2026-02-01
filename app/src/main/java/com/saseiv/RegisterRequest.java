package com.saseiv;

public class RegisterRequest {
    String email;
    String password;

    public RegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
