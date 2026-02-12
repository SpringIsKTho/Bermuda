package com.saseiv;

//Se envía de forma JSON gracias a Retrofit al servidor.
public class LoginRequest {
    String email;
    String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
