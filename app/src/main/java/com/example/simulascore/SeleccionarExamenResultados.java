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

public class SeleccionarExamenResultados extends AppCompatActivity {

    private CardView cardMejorEdu, cardOlimpiada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seleccionar_examen_resultados);
        cardMejorEdu =(CardView) findViewById(R.id.cardMejoredu);
        cardOlimpiada =(CardView) findViewById(R.id.cardOlimpiada);

        cardOlimpiada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarExamenResultados.this , verProgresoOlimpiada.class);
                startActivity(intent);

            }
        });

        cardMejorEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarExamenResultados.this, verProgreso.class);
                startActivity(intent);
            }
        });
    }
}