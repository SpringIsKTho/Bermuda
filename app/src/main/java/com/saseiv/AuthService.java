package com.saseiv;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//Llama a las tablas dentro del supabase
public interface AuthService {

    @POST("auth/v1/token?grant_type=password")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/v1/signup")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
