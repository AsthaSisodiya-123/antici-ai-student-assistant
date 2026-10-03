package com.anticai.studentassistant.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/register")
    Call<LoginResponse> register(
            @Body RegisterRequest request
    );

    @POST("api/auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );
}