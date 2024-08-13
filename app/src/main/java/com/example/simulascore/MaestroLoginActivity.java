package com.example.simulascore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MaestroLoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvCreateAccount;

    private static final String TAG = "MaestroLoginActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String EMAIL_KEY = "email";
    private static final String ROL = "rol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvCreateAccount = findViewById(R.id.tv_create_account);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroLoginActivity.this, MaestroSingupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarSesion() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");

        if (email.isEmpty()) {
            etEmail.setError("Por favor, ingrese un correo");
            return;
        } else if (password.isEmpty()) {
            etPassword.setError("Por favor, ingrese una contraseña");
            return;
        }

        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloMaestro/loginMaestro.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if ("success".equals(status)) {
                                String token = jsonResponse.getString("token");
                                saveToken(email, token, "Maestro");
                                navigateToHome();
                            } else {
                                showCustomAlertDialog("Error", message);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            showCustomAlertDialog("Error", "Error en la respuesta del servidor");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Volley Error: " + error.toString());
                showCustomAlertDialog("Error", "Error de conexión");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void saveToken(String email, String token, String rol) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.putString(EMAIL_KEY, email);
        editor.putString(ROL, rol);
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(MaestroLoginActivity.this, homeMaestro.class);
        startActivity(intent);
        finish();
    }

    private void showCustomAlertDialog(String title, String message) {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null);
        dialog.setContentView(view);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMessage = view.findViewById(R.id.dialog_message);
        Button dialogButton = view.findViewById(R.id.dialog_button);

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
