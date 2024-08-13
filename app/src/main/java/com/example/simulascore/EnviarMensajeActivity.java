package com.example.simulascore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnviarMensajeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<MessagesA> messageList;

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";

    private String codigoAlumno;
    private String nombre;
    private String apellido;
    private String correoMaestro;
    private TextView user_code,user_name;
    private  EditText editTextMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensaje);

        // Recuperar el código del alumno desde el Intent
        Intent intent = getIntent();
        codigoAlumno = intent.getStringExtra("codigoAlumno");
        nombre = intent.getStringExtra("nombreAlumno");
        apellido = intent.getStringExtra("apellidoAlumno");
        user_code =findViewById(R.id.user_code);
        user_code.setText(codigoAlumno);
        user_name =findViewById(R.id.user_name);
        user_name.setText(nombre+" "+apellido);

        // Configurar el RecyclerView
        recyclerViewMessages = findViewById(R.id.recyclerView_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de mensajes y el adaptador
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        // Recuperar el correo del maestro de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        correoMaestro = sharedPreferences.getString(EMAIL_KEY, null);

        if (correoMaestro == null) {
            Toast.makeText(this, "Correo del maestro no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar los mensajes desde la base de datos
        cargarMensajes();

         editTextMessage = findViewById(R.id.editText_message);
        ImageButton buttonSend = findViewById(R.id.button_send);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contenido = editTextMessage.getText().toString().trim();
                if (!contenido.isEmpty()) {
                    enviarMensaje(codigoAlumno, contenido);
                } else {
                    Toast.makeText(EnviarMensajeActivity.this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cargarMensajes() {
        String url = "https://simulascore.com/apis/moduloMensajeria/getMensajes.php?codigoAlumno=" + codigoAlumno;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    String idEmisor = jsonObject.getString("id_emisor");
                                    String idReceptor = jsonObject.getString("id_receptor");
                                    String contenido = jsonObject.getString("contenido");
                                    String fechaHora = jsonObject.getString("fecha_hora");
                                    boolean isSent = jsonObject.getInt("isSent") == 1;

                                    MessagesA message = new MessagesA(idEmisor, idReceptor, contenido, fechaHora, isSent);
                                    messageList.add(message);
                                }
                                messageAdapter.notifyDataSetChanged();

                                // Desplazar al último mensaje
                                recyclerViewMessages.scrollToPosition(messageList.size() - 1);

                            } else {
                                Toast.makeText(EnviarMensajeActivity.this, "No se encontraron mensajes", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EnviarMensajeActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EnviarMensajeActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
    private void enviarMensaje(String codigoAlumno, String contenido) {
        String url = "https://simulascore.com/apis/moduloMensajeria/guardarMensajeMaestro.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if ("success".equals(status)) {
                              //  Toast.makeText(EnviarMensajeActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                                // Después de enviar el mensaje, limpiamos el campo de texto
                                editTextMessage.setText("");
                                // Recargar mensajes para ver el nuevo mensaje enviado
                                cargarMensajes();
                            } else {
                                Toast.makeText(EnviarMensajeActivity.this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EnviarMensajeActivity.this, "Error de respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EnviarMensajeActivity.this, "Error al enviar mensaje: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo_alumno", codigoAlumno);  // Código del alumno que recibe el mensaje
                params.put("contenido", contenido);  // Contenido del mensaje
                params.put("codigo_maestro", correoMaestro);  // Correo del maestro que envía el mensaje

                return params;
            }
        };

        queue.add(postRequest);
    }


}
