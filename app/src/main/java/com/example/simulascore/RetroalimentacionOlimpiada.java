package com.example.simulascore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetroalimentacionOlimpiada extends AppCompatActivity {

    private static final String TAG = "RetroalimentacionMejorEdu";
    private TableLayout tableLayout;
    private String email; // Reemplaza con el email real del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retroalimentacion_olimpiada);

        tableLayout = findViewById(R.id.tableLayout);

        // Recuperar el email del alumno de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "No se encontró el email. Por favor, inicie sesión de nuevo.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            fetchExamenes(); // Llamada para obtener y mostrar los datos
        }
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RetroalimentacionOlimpiada.this, home.class);
                startActivity(intent);
            }
        });
    }

    private void fetchExamenes() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando exámenes...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/examenOlimpiadas/getResultadosOlimpiada.php?email=" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            if ("success".equals(response.getString("status"))) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject exam = data.getJSONObject(i);
                                    TableRow tableRow = new TableRow(RetroalimentacionOlimpiada.this);
                                    tableRow.addView(createTextView(exam.getString("id")));
                                    tableRow.addView(createTextView(exam.getString("fecha")));
                                    tableRow.addView(createTextView(exam.getString("puntaje_general")));
                                    tableRow.addView(createIconView(exam)); // Agregar el icono con la acción modificada
                                    tableLayout.addView(tableRow);
                                }
                            } else {
                                Toast.makeText(RetroalimentacionOlimpiada.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error al procesar la respuesta JSON", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Error en la solicitud: " + error.getMessage());
                Toast.makeText(RetroalimentacionOlimpiada.this, "Error al cargar exámenes", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.table_cell_background); // Usar un fondo definido
        textView.setHeight(116);
        return textView;
    }

    private View createIconView(final JSONObject exam) {
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.baseline_book_24);
        iconView.setLayoutParams(new TableRow.LayoutParams(100, 100));
        iconView.setPadding(8, 8, 8, 8);
        iconView.setOnClickListener(v -> {
            try {
                String examId = exam.getString("id");
                Intent intent = new Intent(RetroalimentacionOlimpiada.this, RetroalimentacionOlimpiadaExamen.class);
                intent.putExtra("EXAM_ID", examId);
                startActivity(intent);
            } catch (JSONException e) {
                Log.e(TAG, "Error al extraer el ID del examen", e);
            }
        });

        // Crear un contenedor para el ícono con el fondo aplicado
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );

        RelativeLayout iconContainer = new RelativeLayout(this);
        iconContainer.setLayoutParams(params);
        iconContainer.setPadding(8, 8, 8, 8);
        iconContainer.setBackgroundResource(R.drawable.table_cell_background); // Usar un fondo definido

        iconContainer.addView(iconView); // Agregar el ícono al contenedor

        return iconContainer;
    }
}
