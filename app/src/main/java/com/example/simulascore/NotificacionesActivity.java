package com.example.simulascore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<UserNotification> notificationsList;
    private ImageView message_icon;

    // Claves para SharedPreferences
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_KEY = "token";

    // Variable para almacenar el correo del alumno
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        // Recuperar el correo del alumno de SharedPreferences al iniciar la actividad
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);

        // Imprimir el correo en el log
        if (email != null) {
            Log.d("NotificacionesActivity", "Correo del alumno: " + email);
        } else {
            Log.e("NotificacionesActivity", "Correo del alumno no encontrado en SharedPreferences");
        }

        recyclerView = findViewById(R.id.recyclerView_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(notificationsList);
        recyclerView.setAdapter(notificationsAdapter);

        message_icon = findViewById(R.id.message_icon);

        message_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMessageDialog();
            }
        });

        // Cargar notificaciones desde el servidor
        loadNotifications();
    }

    private void loadNotifications() {
        // Usar el correo recuperado para construir la URL
        String url = "https://simulascore.com/apis/moduloMensajeria/getNotificaciones.php?email=" + email;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject jsonObject = dataArray.getJSONObject(i);

                                    String contenido = jsonObject.getString("contenido");
                                    String nombre = jsonObject.getString("nombre");
                                    String apellido = jsonObject.getString("apellido");
                                    String fechaHora = jsonObject.getString("fecha_hora");

                                    UserNotification notification = new UserNotification(contenido, nombre + " " + apellido, fechaHora);
                                    notificationsList.add(notification);
                                }

                                notificationsAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NotificacionesActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NotificacionesActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotificacionesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void showSendMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_send_message, null);
        builder.setView(dialogView);

        EditText editTextMessage = dialogView.findViewById(R.id.editTextMessage);
        Button buttonSendMessage = dialogView.findViewById(R.id.buttonSendMessage);

        AlertDialog alertDialog = builder.create();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    if (email != null) {
                        enviarMensaje(email, message);  // Llama al método para enviar el mensaje con el correo recuperado
                    } else {
                        Toast.makeText(NotificacionesActivity.this, "Correo del alumno no encontrado", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(NotificacionesActivity.this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    private void enviarMensaje(String correoAlumno, String contenido) {
        String url = "https://simulascore.com/apis/moduloMensajeria/guardarMensaje.php";  // Cambia esto a la URL de tu endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if ("success".equals(status)) {
                                Toast.makeText(NotificacionesActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotificacionesActivity.this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("MyApp", "Error de respuesta", e);
                            Toast.makeText(NotificacionesActivity.this, "Error de respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NotificacionesActivity.this, "Error al enviar mensaje: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo_alumno", correoAlumno);  // Correo del alumno que envía el mensaje
                params.put("contenido", contenido);  // Contenido del mensaje

                return params;
            }
        };

        queue.add(postRequest);
    }
}
