package com.example.simulascore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultadosEspanol extends AppCompatActivity {

    private static final String TAG = "verProgreso";
    private TableLayout tableLayout;
    private LineChart lineChart;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_progreso);

        tableLayout = findViewById(R.id.tableLayout);
        lineChart = findViewById(R.id.lineChart);

        // Recuperar el email del alumno de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "No se encontró el email. Por favor, inicie sesión de nuevo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchExamenes();
    }

    private void fetchExamenes() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando exámenes...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/getResultados.php?email=" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                JSONArray data = response.getJSONArray("data");
                                List<Entry> entries = new ArrayList<>();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject exam = data.getJSONObject(i);
                                    String fecha = exam.getString("fecha");
                                    String espanol = exam.getString("puntaje_espanol");
                                    String comprension = exam.getString("puntaje_comprension");
                                    String calificacion = exam.getString("calificacionEspanol");

                                    TableRow tableRow = new TableRow(ResultadosEspanol.this);
                                    tableRow.addView(createTextView(fecha));
                                    tableRow.addView(createTextView(espanol));
                                    tableRow.addView(createTextView(comprension));
                                    tableRow.addView(createTextView(calificacion));
                                    tableLayout.addView(tableRow);

                                    // Agregar datos al gráfico
                                    Date date = sdf.parse(fecha);
                                    if (date != null && calificacion != null && !calificacion.equals("null") && !calificacion.isEmpty()) {
                                        entries.add(new Entry(i, Float.parseFloat(calificacion), date.getTime()));
                                    }
                                }

                                setupLineChart(entries);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(ResultadosEspanol.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | java.text.ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error al procesar la respuesta JSON: " + e.getMessage());
                            Toast.makeText(ResultadosEspanol.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Error en la respuesta de la solicitud: " + error.getMessage());
                Toast.makeText(ResultadosEspanol.this, "Error al cargar exámenes", Toast.LENGTH_SHORT).show();
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
        return textView;
    }

    private void setupLineChart(List<Entry> entries) {
        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataSet = new LineDataSet(entries, "Pun General");
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(true); // Mostrar etiquetas en cada punto

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.LINE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) entries.get((int) value).getData();
                return sdf.format(new Date(millis));
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-90); // Rotar las etiquetas 90 grados hacia arriba

        lineChart.invalidate(); // Refresh the chart
    }
}
