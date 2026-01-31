package com.saseiv;

public class LoginResponse {
    public String access_token;
    public String token_type;
    public User user;

    public static class User {
        public String id;
        public String email;
    }
}
