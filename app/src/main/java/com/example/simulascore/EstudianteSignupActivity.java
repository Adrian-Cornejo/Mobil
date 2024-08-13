package com.example.simulascore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class EstudianteSignupActivity extends AppCompatActivity {

    EditText etNombre, etApellido, etCodigoProfesor, etEmail, etPassword, etConfirmPassword;
    Button btnSignup;
    private static final String TAG = "EstudianteSignup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante_signup);

        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etCodigoProfesor = findViewById(R.id.et_codigoProfesor);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarAlumno();
            }
        });
    }

    private void registrarAlumno() {
        final String nombre = etNombre.getText().toString().trim();
        final String apellido = etApellido.getText().toString().trim();
        final String codigoProfesor = etCodigoProfesor.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");

        if (nombre.isEmpty()) {
            etNombre.setError("Por favor, complete este campo");
            return;
        } else if (apellido.isEmpty()) {
            etApellido.setError("Por favor, complete este campo");
            return;
        } else if (codigoProfesor.isEmpty()) {
            etCodigoProfesor.setError("Por favor, complete este campo");
            return;
        } else if (email.isEmpty()) {
            etEmail.setError("Por favor, complete este campo");
            return;
        } else if (password.isEmpty()) {
            etPassword.setError("Por favor, complete este campo");
            return;
        } else if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Por favor, complete este campo");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        }

        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloAlumno/insertarAlumno.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            // Imprimir en la consola
                            Log.d(TAG, "Status: " + status);
                            Log.d(TAG, "com.example.simulascore.MessagesA: " + message);
                            if (status.equalsIgnoreCase("success")) {
                                navigateToLogin();
                            } else {
                                showCustomAlertDialog("Error", message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showCustomAlertDialog("Error", "Error en la respuesta del servidor");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error.networkResponse != null) {
                    String errorMsg = new String(error.networkResponse.data);
                    Log.e("Volley Error", errorMsg);
                    showCustomAlertDialog("Error", errorMsg);
                } else {
                    showCustomAlertDialog("Error", error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", nombre);
                params.put("last_name", apellido);
                params.put("codigoProfesor", codigoProfesor);
                params.put("email", email);
                params.put("password", password);
                params.put("confirm_password", confirmPassword);
                Log.d("RegisterParams", params.toString()); // Log para verificar los parámetros
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(EstudianteSignupActivity.this, EstudianteLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showCustomAlertDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null);
        dialog.setContentView(view);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMessage = view.findViewById(R.id.dialog_message);
        Button dialogButton = view.findViewById(R.id.dialog_button);

        dialogTitle.setText(title);
        dialogMessage.setText(message);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Asegurarse de que el diálogo utiliza todo el ancho de la pantalla
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
