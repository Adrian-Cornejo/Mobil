package com.example.simulascore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultadosActivityMejorEdu extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private int idExamen;
    private String correoAlumno;
    private Button btn_regresar_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_mejor_edu);
        btn_regresar_menu= findViewById(R.id.btn_regresar_menu);

        idExamen = getIntent().getIntExtra("idExamen", -1);

        correoAlumno = obtenerCorreoAlumno();
        Log.d("MyApp", "Email: " + correoAlumno + ", Exam ID: " + idExamen);

        if (idExamen != -1 && correoAlumno != null) {
            // Usa el idExamen y el correoAlumno para obtener y mostrar los resultados
            obtenerResultados(idExamen, correoAlumno);
        } else {
            Toast.makeText(this, "ID del examen o correo no proporcionados", Toast.LENGTH_SHORT).show();
        }

        btn_regresar_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultadosActivityMejorEdu.this, RetroalimentacionMejorEduExamen.class);
                intent.putExtra("EXAM_ID", idExamen);
                intent.putExtra("email", correoAlumno);
                startActivity(intent);
            }
        });
    }



    private String obtenerCorreoAlumno() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_KEY, null);
    }

    private void obtenerResultados(int idExamen, String correoAlumno) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Obteniendo resultados...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/obtenerResultadosPorId.php?email=" + correoAlumno + "&idExamen=" + idExamen;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        if ("success".equals(status)) {
                            JSONArray resultadosArray = response.getJSONArray("data");
                            // Manejar y mostrar los resultados
                            mostrarResultados(resultadosArray);
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            progressDialog.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                String errorMessage = new String(networkResponse.data);
                Toast.makeText(this, "Error de red: " + errorMessage, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error de red: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void mostrarResultados(JSONArray resultadosArray) {
        try {
            JSONObject resultado = resultadosArray.getJSONObject(0);

            String datosGeneralesTexto = "ID: " + resultado.getInt("id") + "\n" +
                    "Código Alumno: " + resultado.getString("codigoAlumno") + "\n" +
                    "Fecha: " + resultado.getString("fecha") + "\n" +
                    "Puntaje General: " + resultado.getDouble("puntaje_general") + "\n" +
                    "Total Preguntas: " + resultado.getInt("total_preguntas") + "\n" +
                    "Correctas General: " + resultado.getInt("correctas_general");

            String espanolTexto = "Puntaje Español: " + resultado.getDouble("puntaje_espanol") + "\n" +
                    "Puntaje Comprensión: " + resultado.getDouble("puntaje_comprension") + "\n" +
                    "Calificación Español: " + resultado.getDouble("calificacionEspanol");

            String matematicasTexto = "Puntaje Matemáticas: " + resultado.getDouble("puntaje_matematicas") + "\n" +
                    "Puntaje Fracciones: " + resultado.getDouble("puntaje_fracciones") + "\n" +
                    "Calificación Matemáticas: " + resultado.getDouble("calificacionMatematicas");

            String fceTexto = "Puntaje FCE: " + resultado.getDouble("puntaje_fce") + "\n" +
                    "Calificación FCE: " + resultado.getDouble("calificacionFce");

            TextView tvDatosGenerales = findViewById(R.id.tv_datos_generales);
            TextView tvEspanol = findViewById(R.id.tv_espanol);
            TextView tvMatematicas = findViewById(R.id.tv_matematicas);
            TextView tvFCE = findViewById(R.id.tv_fce);

            tvDatosGenerales.setText(datosGeneralesTexto);
            tvEspanol.setText(espanolTexto);
            tvMatematicas.setText(matematicasTexto);
            tvFCE.setText(fceTexto);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar los resultados", Toast.LENGTH_SHORT).show();
        }
    }
}
