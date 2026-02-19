package com.saseiv;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.saseiv.R;

public class DetallePezActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView txtTiempoActual, txtDuracionTotal;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pez);

        ImageView imagen = findViewById(R.id.imgDetalle);
        TextView nombre = findViewById(R.id.txtNombreDetalle);
        TextView descripcion = findViewById(R.id.txtDescripcionDetalle);
        TextView txtAudioNoDisponible = findViewById(R.id.txtAudioNoDisponible);
        Button btnPlay = findViewById(R.id.btnPlayAudio);

        seekBar = findViewById(R.id.seekBar);
        txtTiempoActual = findViewById(R.id.txtTiempoActual);
        txtDuracionTotal = findViewById(R.id.txtDuracionTotal);

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

        // Permitir mover el seekbar manualmente
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void reproducirAudio(String url) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            int duracion = mediaPlayer.getDuration();
            seekBar.setMax(duracion);

            txtDuracionTotal.setText(formatearTiempo(duracion));

            actualizarSeekBar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarSeekBar() {
        if (mediaPlayer != null) {
            int posicionActual = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(posicionActual);
            txtTiempoActual.setText(formatearTiempo(posicionActual));

            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(this::actualizarSeekBar, 500);
            }
        }
    }

    private String formatearTiempo(int milisegundos) {
        int segundos = milisegundos / 1000;
        int minutos = segundos / 60;
        segundos = segundos % 60;

        return String.format("%d:%02d", minutos, segundos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
