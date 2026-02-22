package com.saseiv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    private EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        editEmail = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        checkSession();
    }

    public void openMain(View view) {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        loginUser(email, password);
    }


    public void openSignUp(View view) {
        startActivity(new Intent(this, SignUpScreen.class));
    }

    private void loginUser(String email, String password) {

        AuthService service =
                RetrofitClient.getClient(this).create(AuthService.class);

        LoginRequest request = new LoginRequest(email, password);

        service.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse login = response.body();

                    saveSession(login.access_token, login.user.id);

                    Toast.makeText(LoginScreen.this, "Login correcto", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginScreen.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginScreen.this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginScreen.this, "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveSession(String token, String userId) {

        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        prefs.edit()
                .putString("access_token", token)
                .putString("user_id", userId)
                .apply();
    }

    private void checkSession() {

        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        String token = prefs.getString("access_token", null);

        if (token != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}   