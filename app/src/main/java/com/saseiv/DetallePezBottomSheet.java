package com.saseiv;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetallePezBottomSheet extends BottomSheetDialogFragment {

    private Pez pez;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView txtTiempoActual, txtDuracionTotal;
    private LinearLayout linearLayout;
    private Handler handler = new Handler();
    private ImageButton btnPlay;

    public DetallePezBottomSheet(Pez pez) {
        this.pez = pez;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.bottom_sheet_detalle_pez,
                container,
                false
        );

        ImageView imagen = view.findViewById(R.id.imgDetalle);
        TextView nombre = view.findViewById(R.id.txtNombreDetalle);
        TextView descripcion = view.findViewById(R.id.txtDescripcionDetalle);
        TextView txtAudioNoDisponible = view.findViewById(R.id.txtAudioNoDisponible);
        btnPlay = view.findViewById(R.id.btnPlayAudio);

        seekBar = view.findViewById(R.id.seekBar);
        txtTiempoActual = view.findViewById(R.id.txtTiempoActual);
        txtDuracionTotal = view.findViewById(R.id.txtDuracionTotal);
        linearLayout = view.findViewById(R.id.linearTiempos);

        nombre.setText(pez.getNombre());
        descripcion.setText(pez.getDescripcion());

        Glide.with(requireContext())
                .load(pez.getImagen_url())
                .into(imagen);

        if (pez.getAudio_url() == null || pez.getAudio_url().isEmpty()) {
            txtAudioNoDisponible.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        } else {
            btnPlay.setOnClickListener(v -> {
                if (mediaPlayer == null) {
                    reproducirAudio(pez.getAudio_url());
                    btnPlay.setImageResource(R.drawable.ic_pause);
                }
                else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    handler.removeCallbacksAndMessages(null);
                    btnPlay.setImageResource(R.drawable.ic_play);
                }
                else {
                    mediaPlayer.start();
                    actualizarSeekBar();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                }
            });
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        View bottomSheet = getDialog().findViewById(
                com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet != null) {
            ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(params);
        }
    }

    private void reproducirAudio(String url) {
        try {

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            int duracion = mediaPlayer.getDuration();
            seekBar.setMax(duracion);
            txtDuracionTotal.setText(formatearTiempo(duracion));

            actualizarSeekBar();

            mediaPlayer.setOnCompletionListener(mp -> {
                handler.removeCallbacksAndMessages(null);

                mp.seekTo(0);
                seekBar.setProgress(0);
                txtTiempoActual.setText("0:00");

                btnPlay.setImageResource(R.drawable.ic_play);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarSeekBar() {

        if (mediaPlayer == null) return;

        int posicionActual = mediaPlayer.getCurrentPosition();

        // Evita el frame final visual
        if (posicionActual >= mediaPlayer.getDuration()) {
            return;
        }

        seekBar.setProgress(posicionActual);
        txtTiempoActual.setText(formatearTiempo(posicionActual));

        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(this::actualizarSeekBar, 500);
        }
    }

    private String formatearTiempo(int milisegundos) {
        int segundos = milisegundos / 1000;
        int minutos = segundos / 60;
        segundos = segundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_Bermuda_BottomSheet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            handler.removeCallbacksAndMessages(null);
            btnPlay.setImageResource(R.drawable.ic_play);
        }
    }
}
