package com.example.simulascore;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssingExamOlimpiada extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";

    private EditText editTextDateGeneral, editTextTimeGeneral;
    private EditText editTextDateIndividual, editTextTimeIndividual;
    private Button buttonAssignGroup, buttonAssignSelected;
    private RecyclerView recyclerViewStudents;
    private StudentsAdapter studentsAdapter;
    private RequestQueue requestQueue;
    private String correoMaestro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assing_exam_olimpiada);

        editTextDateGeneral = findViewById(R.id.editTextDateGeneral);
        editTextTimeGeneral = findViewById(R.id.editTextTimeGeneral);
        editTextDateIndividual = findViewById(R.id.editTextDateIndividual);
        editTextTimeIndividual = findViewById(R.id.editTextTimeIndividual);
        buttonAssignGroup = findViewById(R.id.buttonAssignGroup);
        buttonAssignSelected = findViewById(R.id.buttonAssignSelected);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        requestQueue = Volley.newRequestQueue(this);

        // Recuperar el correo del profesor de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        correoMaestro = sharedPreferences.getString(EMAIL_KEY, null);

        // Setup RecyclerView
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentsAdapter = new StudentsAdapter(new ArrayList<>());
        recyclerViewStudents.setAdapter(studentsAdapter);

        // Load students
        loadStudents();

        // Set up date and time pickers for general assignment
        editTextDateGeneral.setOnClickListener(v -> showDatePickerDialog(editTextDateGeneral));
        editTextTimeGeneral.setOnClickListener(v -> showTimePickerDialog(editTextTimeGeneral));

        // Set up date and time pickers for individual assignment
        editTextDateIndividual.setOnClickListener(v -> showDatePickerDialog(editTextDateIndividual));
        editTextTimeIndividual.setOnClickListener(v -> showTimePickerDialog(editTextTimeIndividual));

        // Assign exams to the group
        buttonAssignGroup.setOnClickListener(v -> assignExamToGroup());

        // Assign exams to selected students
        buttonAssignSelected.setOnClickListener(v -> assignExamToSelectedStudents());
    }

    private void loadStudents() {
        if (correoMaestro == null) {
            Toast.makeText(this, "Correo del maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://simulascore.com/apis/moduloMaestro/getAlumnosPorMaestro.php?correoMaestro=" + correoMaestro;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray studentsArray = response.getJSONArray("data");
                                List<Student> students = new ArrayList<>();
                                for (int i = 0; i < studentsArray.length(); i++) {
                                    JSONObject studentObject = studentsArray.getJSONObject(i);
                                    String code = studentObject.getString("codigoAlumno");
                                    String name = studentObject.getString("nombre");
                                    String surname = studentObject.getString("apellido");
                                    String email = studentObject.getString("correo");
                                    students.add(new Student(code, name, surname, email));
                                }
                                studentsAdapter.updateStudents(students);
                            } else {
                                Toast.makeText(AssingExamOlimpiada.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AssingExamOlimpiada.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(AssingExamOlimpiada.this, "Error fetching students", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
            editText.setText(formattedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void assignExamToGroup() {
        String date = editTextDateGeneral.getText().toString();
        String time = editTextTimeGeneral.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://simulascore.com/apis/moduloExamenes/asignarExamenGrupo.php";

        Map<String, String> params = new HashMap<>();
        params.put("correoMaestro", correoMaestro);
        params.put("tipoExamen", "resultados_examen_olimpiada");
        params.put("fechaLimite", date);
        params.put("horaLimite", time);

        Log.d("AssignExamOlimpiada", "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AssignExamOlimpiada", "Response: " + response.toString());

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (status.equals("success")) {
                                Toast.makeText(AssingExamOlimpiada.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AssingExamOlimpiada.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("AssignExamOlimpiada", "JSON parsing error: " + e.getMessage());
                            Toast.makeText(AssingExamOlimpiada.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("AssignExamOlimpiada", "Raw response: " + responseBody);

                                if (responseBody.startsWith("<br")) {
                                    Log.e("AssignExamOlimpiada", "Received HTML response instead of JSON");
                                    Toast.makeText(AssingExamOlimpiada.this, "Error del servidor: Respuesta no válida", Toast.LENGTH_SHORT).show();
                                } else {
                                    JSONObject data = new JSONObject(responseBody);
                                    String message = data.optString("message");
                                    Log.e("AssignExamOlimpiada", "Server error: " + message);
                                    Toast.makeText(AssingExamOlimpiada.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("AssignExamOlimpiada", "Error parsing server error response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("AssignExamOlimpiada", "Network error: " + error.getMessage());
                            Toast.makeText(AssingExamOlimpiada.this, "Error assigning exam to group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        requestQueue.add(request);
    }

    private void assignExamToSelectedStudents() {
        String date = editTextDateIndividual.getText().toString();
        String time = editTextTimeIndividual.getText().toString();
        List<Student> selectedStudents = studentsAdapter.getSelectedStudents();

        if (date.isEmpty() || time.isEmpty() || selectedStudents.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione al menos un alumno", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://simulascore.com/apis/moduloExamenes/asignarExamenIndividual.php";

        List<String> codigosAlumnos = new ArrayList<>();
        for (Student student : selectedStudents) {
            codigosAlumnos.add(student.getCode());
        }

        Map<String, Object> params = new HashMap<>();
        params.put("correoMaestro", correoMaestro);
        params.put("tipoExamen", "resultados_examen_olimpiada");
        params.put("fechaLimite", date);
        params.put("horaLimite", time);
        params.put("codigosAlumnos", new JSONArray(codigosAlumnos));

        Log.d("AssignExamOlimpiada", "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AssignExamOlimpiada", "Response: " + response.toString());

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (status.equals("success")) {
                                Toast.makeText(AssingExamOlimpiada.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AssingExamOlimpiada.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("AssignExamOlimpiada", "JSON parsing error: " + e.getMessage());
                            Toast.makeText(AssingExamOlimpiada.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("AssignExamOlimpiada", "Raw response: " + responseBody);
                                JSONObject data = new JSONObject(responseBody);
                                String message = data.optString("message");
                                Log.e("AssignExamOlimpiada", "Server error: " + message);
                                Toast.makeText(AssingExamOlimpiada.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("AssignExamOlimpiada", "Error parsing server error response: " + e.getMessage());
                                Log.e("AssignExamOlimpiada", "Response content: " + error.networkResponse.data.toString());
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("AssignExamOlimpiada", "Network error: " + error.getMessage());
                            Toast.makeText(AssingExamOlimpiada.this, "Error assigning exam to students", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        requestQueue.add(request);
    }
}
