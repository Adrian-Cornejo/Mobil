package com.example.simulascore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import org.json.JSONException;
import org.json.JSONObject;

public class homeMaestro extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "HomeMaestroActivity";
    private TextView tvWelcome;
    private ImageView ivProfile;
    private ImageView iv_edit;
    private FrameLayout btn_Asignar, btn_AsignarOlim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_maestro);

        tvWelcome = findViewById(R.id.tv_welcome);
        ivProfile = findViewById(R.id.iv_profile);
        iv_edit = findViewById(R.id.iv_edit);
        btn_Asignar = findViewById(R.id.btn_Asignar);
        btn_AsignarOlim = findViewById(R.id.btn_AsignarOlim);
        // Recuperar el correo del profesor de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (email != null && token != null) {
            fetchProfesorInfo(email);
        } else {
            tvWelcome.setText("Bienvenido, Profesor");
        }

        ImageButton btnToggleMenu = findViewById(R.id.btn_toggle_menu);
        btnToggleMenu.setOnClickListener(this::showPopupMenu);

        iv_edit.setOnClickListener(v -> {

        });

        btn_Asignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeMaestro.this, AssignExamsActivity.class);
                startActivity(intent);
            }
        });

        btn_AsignarOlim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeMaestro.this, AssingExamOlimpiada.class);
                startActivity(intent);
            }
        });

        FrameLayout btnMensajeria = findViewById(R.id.btn_mensajeria);
        btnMensajeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeMaestro.this, MensajeriaActivityMaestro.class);
                startActivity(intent);
            }
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

    private void fetchProfesorInfo(String email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando información del profesor...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloMaestro/getProfesorInfo.php?email=" + email;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            String nombre = data.getString("nombre");
                            String apellido = data.getString("apellido");
                            String imageUrl = data.getString("urlImagen");

                            tvWelcome.setText("" + nombre + " " + apellido);

                            // Usar Glide para cargar la imagen de perfil
                            Glide.with(homeMaestro.this)
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
            tvWelcome.setText("Bienvenido, Profesor");
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

        // Navegar a la pantalla de inicio de sesión
        Intent intent = new Intent(homeMaestro.this, MaestroLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Elimina la pila de actividades
        startActivity(intent);
        finish();
    }
}
