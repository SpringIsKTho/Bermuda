package com.saseiv;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

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
                .load(getDrawable(R.drawable.saiyanfish))
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(this.getResources().getColor(R.color.transparent)))
                .into(mPic);
    }

    public void openContact(View v){
        startActivity(new Intent(ProfileScreen.this, contact.class));
    }

    public void openPrivacidad(View v){
        startActivity(new Intent(ProfileScreen.this, PrivacidadActivity.class));
    }
}