package com.example.simulascore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.app_name);

        // Crear la animación
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(300);

        // Iniciar la animación
        logo.startAnimation(fadeIn);
        appName.startAnimation(fadeIn);

        // Mantener la pantalla de inicio por 5 segundos y luego iniciar la actividad principal
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}
