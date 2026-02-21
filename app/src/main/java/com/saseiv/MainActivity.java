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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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


    private Uri imageUri;
    private Uri audioUri;
    private RecyclerView recyclerView;
    private PezAdapter adapter;
    private List<Pez> listaPeces = new ArrayList<>();
    private SwipeRefreshLayout swipeLayout;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> audioPickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.recyclerPeces);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new PezAdapter(this, listaPeces);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        Log.d("TOKEN_DEBUG",
                prefs.getString("access_token", "NO TOKEN"));

        cargarPeces();
        swipeLayout = findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(mOnRefreshListener);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logoff) {
                MaterialAlertDialogBuilder builder =
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Cerrar sesión")
                                .setMessage("¿Estás seguro?")
                                .setPositiveButton("Sí", (dialog, which) -> logoutUser())
                                .setNegativeButton("No", null);

                AlertDialog dialog = builder.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getColor(R.color.blue_700));

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getColor(R.color.blue_700));
                return true;
            }
            return false;
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Toast.makeText(this,
                                "Imagen seleccionada",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        audioUri = uri;
                        Toast.makeText(this,
                                "Audio seleccionado",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        fab.setOnClickListener(v -> mostrarDialogNuevoPez());
    }


    protected SwipeRefreshLayout.OnRefreshListener
        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            final ConstraintLayout mLayout = findViewById(R.id.main);
            cargarPeces();
            Toast.makeText(MainActivity.this, "Peces recargados", Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
        }
    };

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
        imageUri = null;
        audioUri = null;

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

        imgFish.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*")
        );

        audioFish.setOnClickListener(v ->
                audioPickerLauncher.launch("audio/*")
        );

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
                                "Pez subido con éxito.",
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



    private void logoutUser() {
        SharedPreferences prefs =
                getSharedPreferences("supabase", MODE_PRIVATE);

        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void openProfile() {
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