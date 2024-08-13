package com.example.simulascore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "ProfileActivity";

    private TextView profileName;
    private TextView profileEmail;
    private TextView profileSchool;
    private TextView profileTeacherCode;
    private TextView profileWorkCode;
    private ImageView profileImage;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profileSchool = findViewById(R.id.profile_institution);
        profileTeacherCode = findViewById(R.id.profile_teacher_code);
        profileWorkCode = findViewById(R.id.profile_work_code);
        profileImage = findViewById(R.id.profile_image);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);

        if (email != null) {
            fetchAlumnoInfo(email);
        } else {
            profileName.setText("Nombre del Usuario");
            profileEmail.setText("correo@ejemplo.com");
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, home.class);
                startActivity(intent);
            }
        });
    }

    private void fetchAlumnoInfo(String email) {
        String url = "https://simulascore.com/apis/moduloAlumno/getAlumnoInfo.php?email=" + email;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            String nombre = data.getString("nombre");
                            String apellido = data.getString("apellido");
                            String codigoAlumno = data.getString("codigoAlumno");
                            String escuela = data.getString("escuela");
                            String codigoProfesor = data.getString("codigoProfesor");
                            String imageUrl = "https://simulascore.com" + data.getString("urlImagen").replace("\\", "/");

                            profileName.setText(nombre + " " + apellido);
                            profileEmail.setText(email);
                            profileSchool.setText(escuela);
                            profileTeacherCode.setText(codigoProfesor);
                            profileWorkCode.setText(codigoAlumno);

                            Glide.with(ProfileActivity.this)
                                    .load(imageUrl)
                                    .into(profileImage);
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Error: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
                }, error -> {
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

}
