package com.saseiv;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//Retrofit es un generador de HTTP para que pueda ser reconocido por Supabase
//Supabase enviará un link HTTP, que será interpretado por Retrofit.
public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL =
            "https://nampxlakrtlwxpcfvzvn.supabase.co/";
    private static final String API_KEY =
            "sb_publishable_vD86_ffuHHqFYXaqNTOvJg_KHwfZl-s";

    public static Retrofit getClient(Context context) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {

                    SharedPreferences prefs =
                            context.getSharedPreferences("supabase", Context.MODE_PRIVATE);

                    String accessToken =
                            prefs.getString("access_token", null);

                    Request.Builder builder =
                            chain.request().newBuilder()
                                    .addHeader("apikey", API_KEY);

                    // 🔑 SOLO si hay sesión
                    if (accessToken != null) {
                        builder.addHeader(
                                "Authorization",
                                "Bearer " + accessToken
                        );
                    }

                    return chain.proceed(builder.build());
                })
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}