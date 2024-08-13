package com.example.simulascore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ExamenMejoredu extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "MejoreduInstructions";

    private Button btnEspanol, btnMatematicas, btnFCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examen_mejoredu);

        btnEspanol = findViewById(R.id.btn_espanol);

        btnEspanol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExamenMejoredu.this, ExamenMejorEduSeleccionarMateria.class);
                startActivity(intent);
            }
        });
    }
}
