package com.example.simulascore;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    public TextView perfiles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        perfiles= findViewById(R.id.Perfiles);

        // Verificar si el token JWT está guardado
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        String rol = sharedPreferences.getString("rol", null);
        perfiles.setText(rol);

        if (token != null && rol != null) {
            Intent intent;
            if ("Estudiante".equals(rol)) {
                intent = new Intent(MainActivity.this, home.class);
                startActivity(intent);
                finish();
            } else if ("Maestro".equals(rol)) {
                intent = new Intent(MainActivity.this, homeMaestro.class);
                startActivity(intent);
                finish();
            } else {
                // Tratar otros casos o roles no definidos
                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            // Redireccionar a la pantalla de login si el token o el rol no están presentes

        }





        // Configurar el clic para Estudiante
        FrameLayout estudianteFrame = findViewById(R.id.frame_estudiante);
        estudianteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EstudianteLoginActivity.class);
                startActivity(intent);
            }
        });

        // Configurar el clic para Maestro
        FrameLayout maestroFrame = findViewById(R.id.frame_maestro);
        maestroFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MaestroLoginActivity.class);
                startActivity(intent);
            }
        });



    }
}