package com.example.simulascore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class ExamenMejorEduSeleccionarMateria extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "ExamenMejorEdu";

    private CardView cardEspanol, cardMatematicas, cardFCE;
    private int progresoEspanol = 1; // Considerando que 1 significa no contestado
    private int progresoMatematicas = 1;
    private int progresoFormacion = 1;

    private int color = 000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examen_mejor_edu_seleccionar_materia);

        cardEspanol = findViewById(R.id.cardEspanol);
        cardMatematicas = findViewById(R.id.cardMatematicas);
        cardFCE = findViewById(R.id.cardFCE);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);

        if (email != null) {
            verificarProgreso(email);
        } else {
            Toast.makeText(this, "Email not found in SharedPreferences", Toast.LENGTH_SHORT).show();
        }

        setupCardListeners();
    }

    private void setupCardListeners() {
        cardEspanol.setOnClickListener(v -> {
            if (progresoEspanol == 0) {
                showCompletedDialog();
            } else {
                Intent intent = new Intent(this, ExamenMejorEduEspanol.class);
                startActivity(intent);
            }
        });

        cardMatematicas.setOnClickListener(v -> {
            if (progresoMatematicas == 0) {
                showCompletedDialog();
            } else {
                Intent intent = new Intent(this, ExamenMejorEduMatematicas.class);
                startActivity(intent);
            }
        });

        cardFCE.setOnClickListener(v -> {
            if (progresoFormacion == 0) {
                showCompletedDialog();
            } else {
                Intent intent = new Intent(this, ExamenMejorEduFCE.class);
                startActivity(intent);
            }
        });
    }

    private void verificarProgreso(String email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando progreso...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloAlumno/verificarProgreso.php?email=" + email;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            updateCardsState(data);
                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("MyApp", "Error parsing JSON", e);
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void updateCardsState(JSONObject data) throws JSONException {
        progresoEspanol = data.getInt("progresoEspanol");
        progresoMatematicas = data.getInt("progresoMatematicas");
        progresoFormacion = data.getInt("progresoFormacion");

        // Cambia el color de la tarjeta Español si el progreso es 1
        if (progresoEspanol == 1) {
            cardEspanol.setCardBackgroundColor(getResources().getColor(R.color.student_color_secondary));
        }

        // Cambia el color de la tarjeta Matemáticas si el progreso es 1
        if (progresoMatematicas == 1) {
            cardMatematicas.setCardBackgroundColor(getResources().getColor(R.color.student_color_secondary));
        }

        // Cambia el color de la tarjeta Formación Cívica y Ética si el progreso es 1
        if (progresoFormacion == 1) {
            cardFCE.setCardBackgroundColor(getResources().getColor(R.color.student_color_secondary));
        }

        // Verifica si todos los progresos son 0
        if (progresoEspanol == 0 && progresoMatematicas == 0 && progresoFormacion == 0) {
            showFinishedExamDialog();
        }
    }

    private void showCompletedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exam_completion, null);
        builder.setView(dialogView);

        Button buttonOk = dialogView.findViewById(R.id.buttonOk);
        AlertDialog dialog = builder.create();

        buttonOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showFinishedExamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exam_finished, null);
        builder.setView(dialogView);

        Button buttonCalificar = dialogView.findViewById(R.id.buttonCalificar);
        AlertDialog dialog = builder.create();

        buttonCalificar.setOnClickListener(v -> {
            // Obtener el correo del alumno de SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String email = sharedPreferences.getString(EMAIL_KEY, null);

            if (email != null) {
                calificarExamen(email, dialog);
            } else {
                Toast.makeText(this, "Email not found in SharedPreferences", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void calificarExamen(String email, AlertDialog dialog) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Calificando examen...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/calificarExamen.php?email=" + email;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        if ("success".equals(status)) {
                            int score = response.getInt("score");
                            int idExamen = response.getInt("id");
                            Toast.makeText(this, "Examen calificado. Puntuación: " + score, Toast.LENGTH_SHORT).show();
                            // Navegar a la nueva actividad y pasar el id del examen

                            Intent intent = new Intent(this, ResultadosActivityMejorEdu.class);
                            intent.putExtra("idExamen", idExamen);
                            startActivity(intent);
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
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



}
