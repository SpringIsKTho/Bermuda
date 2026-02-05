package com.saseiv;

//Es la respuesta que enviará Supabase tras hacer un login.
//access_token permite el inicio de sesión, mantenerla abierta y guardarla incluso al salir de la app.
public class LoginResponse {
    public String access_token;
    public String token_type;
    public User user;

    public static class User {
        public String id;
        public String email;
    }
}
