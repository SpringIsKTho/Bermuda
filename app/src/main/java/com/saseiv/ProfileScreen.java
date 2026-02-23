package com.saseiv;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProfileScreen extends AppCompatActivity {

    private ImageView mPic;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_screen);

        linearLayout = findViewById(R.id.lenguaje);
        mPic = findViewById(R.id.profilePicture);
        Glide.with(this)
                .load(getDrawable(R.drawable.circulobotonselectedfish))
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(this.getResources().getColor(R.color.transparent)))
                .into(mPic);

        linearLayout.setOnClickListener(v -> chooseLanguage());
    }

    public void openContact(View v){
        startActivity(new Intent(ProfileScreen.this, contact.class));
    }

    public void openPrivacidad(View v){
        startActivity(new Intent(ProfileScreen.this, PrivacidadActivity.class));
    }

    public void chooseLanguage(){
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_language_layout, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView option1 = view.findViewById(R.id.option1);
        TextView option2 = view.findViewById(R.id.option2);

        option1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setAppLocale("en");
                Toast.makeText(ProfileScreen.this, "Language changed.", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        option2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setAppLocale("es");
                Toast.makeText(ProfileScreen.this, "Lenguaje cambiado.", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}