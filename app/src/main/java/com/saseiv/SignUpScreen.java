package com.saseiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpScreen extends AppCompatActivity {

    private EditText editEmail, editPassword, editPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
    }

    public void openMain(View view) {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirm = editPasswordConfirm.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(email, password);
    }

    public void openLogin(View view) {
        finish();
    }

    private void registerUser(String email, String password) {

        AuthService service =
                RetrofitClient.getClient(this).create(AuthService.class);

        RegisterRequest request = new RegisterRequest(email, password);

        service.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(SignUpScreen.this,
                            "Usuario registrado correctamente",
                            Toast.LENGTH_LONG).show();
                    finish();

                } else {
                    try {
                        String error = response.errorBody().string();
                        android.util.Log.e("SIGNUP_ERROR", error);

                        Toast.makeText(SignUpScreen.this,
                                "Error: " + error,
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(SignUpScreen.this,
                                "Error desconocido",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(SignUpScreen.this,
                        "Error de conexión",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}