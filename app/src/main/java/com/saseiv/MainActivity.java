package com.saseiv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int PICK_AUDIO = 101;

    private Uri imageUri;
    private Uri audioUri;

    private RecyclerView recyclerView;
    private PezAdapter adapter;
    private List<Pez> listaPeces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.recyclerPeces);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PezAdapter(this, listaPeces);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        Log.d("TOKEN_DEBUG",
                prefs.getString("access_token", "NO TOKEN"));

        cargarPeces();

        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logoff) {
                logoutUser();
                return true;
            }
            return false;
        });

        fab.setOnClickListener(v -> mostrarDialogNuevoPez());
    }


    private void cargarPeces() {
        SupabaseService service =
                RetrofitClient.getClient(this)
                        .create(SupabaseService.class);

        service.getPeces().enqueue(new Callback<List<Pez>>() {
            @Override
            public void onResponse(Call<List<Pez>> call, Response<List<Pez>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPeces.clear();
                    listaPeces.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Error al cargar los peces",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pez>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarDialogNuevoPez() {

        View dialogView =
                LayoutInflater.from(this)
                        .inflate(R.layout.dialog_new_fish_layout, null);

        EditText nameFish = dialogView.findViewById(R.id.nameFish);
        EditText descFish = dialogView.findViewById(R.id.descFish);
        Button imgFish = dialogView.findViewById(R.id.imgFish);
        Button audioFish = dialogView.findViewById(R.id.audioFish);
        Button uploadFish = dialogView.findViewById(R.id.uploadFish);

        AlertDialog dialog =
                new MaterialAlertDialogBuilder(this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        imgFish.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        audioFish.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, PICK_AUDIO);
        });

        uploadFish.setOnClickListener(v -> {

            String nombre = nameFish.getText().toString().trim();
            String desc = descFish.getText().toString().trim();

            if (nombre.isEmpty() || desc.isEmpty() || imageUri == null) {
                Toast.makeText(this,
                        "Nombre, descripción e imagen obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs =
                    getSharedPreferences("supabase", MODE_PRIVATE);

            String userId = prefs.getString("user_id", null);

            if (userId == null) {
                Toast.makeText(this,
                        "Sesión inválida",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            subirImagenYContinuar(userId, nombre, desc);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void subirImagenYContinuar(
            String userId,
            String nombre,
            String desc
    ) {
        String imageName = "pez_" + System.currentTimeMillis() + ".jpg";

        uploadFile(imageUri, "images", imageName, new OnUploadComplete() {
            @Override
            public void onSuccess(String imageUrl) {

                // 🔹 Si NO hay audio
                if (audioUri == null) {
                    insertarPez(userId, nombre, desc, imageUrl, null);
                    return;
                }

                // 🔹 Si SÍ hay audio
                String audioName = "audio_" + System.currentTimeMillis() + ".mp3";

                uploadAudio(audioUri, "audios", audioName, new OnUploadComplete() {
                    @Override
                    public void onSuccess(String audioUrl) {
                        insertarPez(userId, nombre, desc, imageUrl, audioUrl);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(
                                MainActivity.this,
                                "Error subiendo audio",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }

            @Override
            public void onError() {
                Toast.makeText(
                        MainActivity.this,
                        "Error subiendo imagen",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void uploadAudio(
            Uri uri,
            String bucket,
            String filePath,
            OnUploadComplete callback
    ) {
        try {
            byte[] bytes = readBytesFromUri(uri);

            RequestBody body = RequestBody.create(
                    bytes,
                    okhttp3.MediaType.parse("audio/mpeg")
            );

            StorageService storage =
                    RetrofitClient.getClient(this)
                            .create(StorageService.class);

            storage.uploadFile(bucket, filePath, body)
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(
                                Call<ResponseBody> call,
                                Response<ResponseBody> response
                        ) {
                            if (response.isSuccessful()) {

                                String url =
                                        "https://nampxlakrtlwxpcfvzvn.supabase.co/" +
                                                "storage/v1/object/public/" +
                                                bucket + "/" + filePath;

                                callback.onSuccess(url);

                            } else {
                                try {
                                    String error = response.errorBody() != null
                                            ? response.errorBody().string()
                                            : "sin cuerpo";

                                    Log.e("AUDIO_UPLOAD_ERROR",
                                            "Código: " + response.code() + " → " + error);
                                } catch (Exception ignored) {}

                                callback.onError();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("UPLOAD_ERROR", t.getMessage(), t);
                            callback.onError();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    private void uploadFile(
            Uri uri,
            String bucket,
            String filePath,
            OnUploadComplete callback
    ) {
        try {
            byte[] bytes = readBytesFromUri(uri);

            RequestBody body = RequestBody.create(
                    bytes,
                    okhttp3.MediaType.parse("image/jpeg")
            );

            StorageService storage =
                    RetrofitClient.getClient(this)
                            .create(StorageService.class);

            storage.uploadFile(bucket, filePath, body)
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(
                                Call<ResponseBody> call,
                                Response<ResponseBody> response
                        ) {
                            if (response.isSuccessful()) {

                                String url =
                                        "https://nampxlakrtlwxpcfvzvn.supabase.co/" +
                                                "storage/v1/object/public/" +
                                                bucket + "/" + filePath;

                                callback.onSuccess(url);

                            } else {
                                try {
                                    String error = response.errorBody() != null
                                            ? response.errorBody().string()
                                            : "sin cuerpo";

                                    Log.e("IMAGE_UPLOAD_ERROR",
                                            "Código: " + response.code() + " → " + error);
                                } catch (Exception ignored) {}

                                callback.onError();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("UPLOAD_ERROR", t.getMessage(), t);
                            callback.onError();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    private void insertarPez(
            String userId,
            String nombre,
            String desc,
            String imageUrl,
            String audioUrl
    ) {

        SupabaseService service =
                RetrofitClient.getClient(this)
                        .create(SupabaseService.class);

        PezRequest request =
                new PezRequest(
                        userId,
                        nombre,
                        desc,
                        imageUrl,
                        audioUrl
                );

        service.insertPez(request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        Toast.makeText(MainActivity.this,
                                "Pez subido con éxito 🐟",
                                Toast.LENGTH_SHORT).show();

                        cargarPeces();
                        imageUri = null;
                        audioUri = null;
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this,
                                "Error insertando pez",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == PICK_AUDIO) {
                audioUri = data.getData();
                Toast.makeText(this, "Audio seleccionado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void logoutUser() {
        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openProfile(MenuItem menu) {
        startActivity(new Intent(this, ProfileScreen.class));
    }

    private byte[] readBytesFromUri(Uri uri) throws Exception {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }
    }



}