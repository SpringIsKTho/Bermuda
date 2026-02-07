package com.saseiv;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SupabaseService {
    @GET("rest/v1/posts?select=*")
    Call<List<Pez>> getPeces();

    @POST("rest/v1/posts")
    Call<Void> insertPez(@Body PezRequest pez);
}
