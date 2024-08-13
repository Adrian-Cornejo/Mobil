package com.example.simulascore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Seleccionar_Materia_Retroalimentacion extends AppCompatActivity {

    private CardView cardMejorEdu, cardOlimpiada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seleccionar_materia_retroalimentacion);

        cardMejorEdu =(CardView) findViewById(R.id.cardMejoredu);
        cardOlimpiada =(CardView) findViewById(R.id.cardOlimpiada);


        cardOlimpiada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Seleccionar_Materia_Retroalimentacion.this , RetroalimentacionOlimpiada.class);
                startActivity(intent);

            }
        });
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Seleccionar_Materia_Retroalimentacion.this, home.class);
                startActivity(intent);
            }
        });
        cardMejorEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Seleccionar_Materia_Retroalimentacion.this, RetroalimentacionMejorEdu.class);
                startActivity(intent);
            }
        });
    }
}