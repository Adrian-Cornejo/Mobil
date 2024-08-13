package com.example.simulascore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

public class home extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "HomeActivity";
    private TextView tvWelcome;
    private ImageView ivProfile;

    private FrameLayout btnMejoredu, btnOlimpiada;
    private boolean isMejoreduAssigned = false;
    private boolean isOlimpiadaAssigned = false;
    private ImageView iv_edit ,iv_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tv_welcome);
        ivProfile = findViewById(R.id.iv_profile);
        btnMejoredu = findViewById(R.id.btn_mejoredu);
        btnOlimpiada = findViewById(R.id.btn_olimpiada);
        iv_edit=(ImageView)findViewById(R.id.iv_edit);
        iv_notification =(ImageView)findViewById(R.id.iv_notification);

        // Recuperar el correo del alumno de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String token = sharedPreferences.getString(TOKEN_KEY, null);



        String pushSubscriptionId= OneSignal.getUser().getPushSubscription().getId();
        Log.d("OneSignal", "Push Subscription ID: " + pushSubscriptionId);

        if (email != null && token != null) {
            fetchAlumnoInfo(email);
            checkExamAssignments(email, "resultados_examen_mejoredu");
            checkExamAssignments(email, "resultados_examen_olimpiada");
        } else {
            tvWelcome.setText("Bienvenido, Alumno");
        }

        ImageButton btnToggleMenu = findViewById(R.id.btn_toggle_menu);
        btnToggleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        // Configurar los botones de las tarjetas
        btnMejoredu.setOnClickListener(v -> {
            if (!isMejoreduAssigned) {
                showErrorDialog("No hay asignación para el examen de Mejoredu.");
            } else {
                Intent intent = new Intent(home.this, ExamenMejoredu.class);
                startActivity(intent);
            }
        });

        btnOlimpiada.setOnClickListener(v -> {
            if (!isOlimpiadaAssigned) {
                showErrorDialog("No hay asignación para el examen de Olimpiada.");
            } else {
                Intent intent = new Intent(home.this, ExamenOlimpiada.class);
                startActivity(intent);
            }
        });

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,NotificacionesActivity.class);
                startActivity(intent);
            }
        });

        FrameLayout btnVerProgreso = findViewById(R.id.btn_ver_progreso);
        btnVerProgreso.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, SeleccionarExamenResultados.class);
                startActivity(intent);
        });

        FrameLayout btnVerRetroalimentacion = findViewById(R.id.btn_ver_retroalimentacion);
        btnVerRetroalimentacion.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, Seleccionar_Materia_Retroalimentacion.class);
                startActivity(intent);
        });
    }

    private void showPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_options, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.perfil) {

                Intent intent = new Intent(home.this,ProfileActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.configuracion) {
                // Handle configuración click
                return true;
            } else if (itemId == R.id.Cerrar) {
                logout();
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    private void fetchAlumnoInfo(String email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando información del alumno...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloAlumno/getAlumnoInfo.php?email=" + email;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            String nombre = data.getString("nombre");
                            String apellido = data.getString("apellido");
                            String codigoAlumno = data.getString("codigoAlumno");
                            String escuela = data.getString("escuela");
                            String codigoEscuela = data.getString("codigoEscuela");
                            String codigoProfesor = data.getString("codigoProfesor");
                            String correo = data.getString("correo");
                            String imageUrl = data.getString("urlImagen");

                            tvWelcome.setText("" + nombre + " " + apellido);

                            // Usar Glide para cargar la imagen de perfil
                            Glide.with(home.this)
                                    .load("https://simulascore.com" + imageUrl)
                                    .into(ivProfile);

                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Error: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
                }, error -> {
            progressDialog.dismiss();
            if (error.networkResponse != null) {
                String errorMsg = new String(error.networkResponse.data);
                Log.e(TAG, errorMsg);
            } else {
                Log.e(TAG, error.getMessage());
            }
            tvWelcome.setText("Bienvenido, Alumno");
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void logout() {
        // Eliminar token y correo electrónico de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.remove("email");
        editor.remove("rol");
        editor.apply();

        OneSignal.logout();

        // Navegar a la pantalla de inicio de sesión
        Intent intent = new Intent(home.this, EstudianteLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Elimina la pila de actividades
        startActivity(intent);
        finish();
    }

    private void checkExamAssignments(String email, String tipoExamen) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando asignaciones...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloAlumno/verificarAsignacion.php?email=" + email + "&tipoExamen=" + tipoExamen;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            int asignado = jsonResponse.getInt("asignado");
                            if (tipoExamen.equals("resultados_examen_mejoredu")) {
                                isMejoreduAssigned = (asignado == 1);
                            } else if (tipoExamen.equals("resultados_examen_olimpiada")) {
                                isOlimpiadaAssigned = (asignado == 1);
                            }
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Error: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
                }, error -> {
            progressDialog.dismiss();
            if (error.networkResponse != null) {
                String errorMsg = new String(error.networkResponse.data);
                Log.e(TAG, errorMsg);
            } else {
                Log.e(TAG, error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void showErrorDialog(String message) {
        // Crear y mostrar el diálogo personalizado
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert_dialog);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button dialogButton = dialog.findViewById(R.id.dialog_button);

        dialogTitle.setText("Alerta");
        dialogMessage.setText(message);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
