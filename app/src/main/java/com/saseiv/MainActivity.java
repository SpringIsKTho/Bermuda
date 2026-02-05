package com.saseiv;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int PICK_AUDIO = 101;
    private Uri imageUri, audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);

        bottomAppBar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.logoff) {
                logoutUser();
                return true;
            }

            return false;
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog();
            }
        });

    }

    private void getDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_new_fish_layout, null);
        EditText nameFish = dialogView.findViewById(R.id.nameFish);
        EditText descFish = dialogView.findViewById(R.id.descFish);
        Button imgFish = dialogView.findViewById(R.id.imgFish);
        Button audioFish = dialogView.findViewById(R.id.audioFish);
        Button uploadFish = dialogView.findViewById(R.id.uploadFish);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this).setView(dialogView).setCancelable(true).create();

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

            if(nombre.isEmpty() || desc.isEmpty() || imageUri == null){
                Toast.makeText(this, "Rellena los campos con un asterisco.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Pez subido con exito.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null){
            if(requestCode == PICK_IMAGE){
                imageUri = data.getData();
                Toast.makeText(this,"Imagen seleccionada", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == PICK_AUDIO){
                audioUri = data.getData();
                Toast.makeText(this, "Audio seleccionado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logoutUser() {

        SharedPreferences prefs = getSharedPreferences("SESSION", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openProfile(MenuItem menu){
        Intent intent = new Intent(MainActivity.this, ProfileScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}