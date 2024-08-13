package com.example.simulascore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MensajeriaActivityMaestro extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private String correoMaestro;
    private RequestQueue requestQueue;
    private SearchView searchView; // Usando androidx.appcompat.widget.SearchView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria_maestro);

        // Inicializa la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Encuentra el SearchView de la interfaz
        searchView = findViewById(R.id.searchView);

        // Configura los colores del texto dentro del SearchView
        int searchTextViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(searchTextViewId);
        if (searchText != null) {
            searchText.setTextColor(Color.BLACK);
            searchText.setHintTextColor(Color.GRAY);
        }

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerView_alumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recuperar el correo del profesor de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        correoMaestro = sharedPreferences.getString(EMAIL_KEY, null);

        if (correoMaestro == null) {
            Toast.makeText(this, "Correo del maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicialización de la lista de estudiantes
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Student student) {
                // Aquí puedes implementar la lógica para enviar un mensaje al estudiante
                enviarMensaje(student);
            }
        });

        recyclerView.setAdapter(studentAdapter);

        // Cargar los datos de los estudiantes
        loadStudents();

        // Configurar el filtrado de búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                studentAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                studentAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    // Método para cargar los alumnos desde un API o base de datos
    private void loadStudents() {
        if (correoMaestro == null) {
            Toast.makeText(this, "Correo del maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://simulascore.com/apis/moduloMaestro/getAlumnosPorMaestro.php?correoMaestro=" + correoMaestro;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
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
                            studentAdapter.updateStudents(students);
                        } else {
                            Toast.makeText(MensajeriaActivityMaestro.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MensajeriaActivityMaestro.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MensajeriaActivityMaestro.this, "Error al obtener los estudiantes", Toast.LENGTH_SHORT).show();
                });

        // Agregar la solicitud a la cola
        requestQueue.add(request);
    }


    // Método para manejar el envío de mensajes
    private void enviarMensaje(Student student) {
        Intent intent = new Intent(MensajeriaActivityMaestro.this, EnviarMensajeActivity.class);
        intent.putExtra("codigoAlumno", student.getCode());
        intent.putExtra("nombreAlumno", student.getName());
        intent.putExtra("apellidoAlumno", student.getSurname());
        startActivity(intent);
    }


}
