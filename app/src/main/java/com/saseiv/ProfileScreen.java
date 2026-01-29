package com.saseiv;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ProfileScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_screen);

        ImageView mPic = findViewById(R.id.profilePicture);
        Glide.with(this)
                .load(getDrawable(R.drawable.saiyanfish))
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(this.getResources().getColor(R.color.transparent)))
                .into(mPic);
    }
}