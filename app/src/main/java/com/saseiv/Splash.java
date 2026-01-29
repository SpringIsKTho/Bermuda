package com.saseiv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView titulo;
    private boolean burbujasMostradas = false;

    private ImageView bubble;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.lonleyfish);
        titulo = findViewById(R.id.Titulo);
        bubble = findViewById(R.id.lonleybuble);

        iniciarAnimacion();
        openApp();
    }

    private void iniciarAnimacion() {

        // Asegura que el layout ya está medido
        logo.post(() -> {

            // Empieza fuera por la izquierda
            logo.setX(-logo.getWidth());

            // Mueve el pez
            logo.animate()
                    .translationX(titulo.getX() - logo.getWidth() - 10)
                    .setDuration(2200) // velocidad
                    .withEndAction(() -> {
                        // Se queda quieto y aparecen las burbujas
                        bubble.setX(logo.getX() + logo.getWidth() - 10);
                        bubble.setY(logo.getY() - 10);
                        bubble.setVisibility(View.VISIBLE);
                    })
                    .start();
        });
    }



    private void openApp() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, LoginScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, 3000);
    }
}
