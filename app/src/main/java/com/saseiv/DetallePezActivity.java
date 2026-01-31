package com.saseiv;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetallePezActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pez);

        ImageView imagen = findViewById(R.id.imgDetalle);
        TextView nombre = findViewById(R.id.txtNombreDetalle);
        TextView descripcion = findViewById(R.id.txtDescripcionDetalle);
        TextView txtAudioNoDisponible = findViewById(R.id.txtAudioNoDisponible);
        Button btnPlay = findViewById(R.id.btnPlayAudio);

        String nombrePez = getIntent().getStringExtra("nombre");
        String descripcionPez = getIntent().getStringExtra("descripcion");
        String imagenUrl = getIntent().getStringExtra("imagen");
        String audioUrl = getIntent().getStringExtra("audio");

        nombre.setText(nombrePez);
        descripcion.setText(descripcionPez);

        Glide.with(this)
                .load(imagenUrl)
                .into(imagen);

        if (audioUrl == null || audioUrl.isEmpty()) {
            txtAudioNoDisponible.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        } else {
            btnPlay.setOnClickListener(v -> reproducirAudio(audioUrl));
        }
    }

    private void reproducirAudio(String url) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}