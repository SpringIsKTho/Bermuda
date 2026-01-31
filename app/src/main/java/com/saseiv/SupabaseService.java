package com.saseiv;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SupabaseService {
    @GET("rest/v1/peces?select=*")
    Call<List<Pez>> getPeces();
}
