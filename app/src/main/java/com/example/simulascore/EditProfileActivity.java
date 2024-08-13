package com.example.simulascore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.simulascore.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName;
    private EditText etSurname;
    private ImageView profileImage;
    private Button btnSave, btnChangePassword;
    private Uri imageUri;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.et_name);
        etSurname = findViewById(R.id.et_surname);
        profileImage = findViewById(R.id.profile_image);
        btnSave = findViewById(R.id.btn_save);
        btnChangePassword = findViewById(R.id.btn_change_password);
        ImageView btnCamera = findViewById(R.id.btn_camera);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL_KEY, null);
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, home.class);
                startActivity(intent);
            }
        });

        if (email != null) {
            fetchAlumnoInfo(email);
        }

        btnSave.setOnClickListener(v -> {
            if (email != null) {
                if (imageUri != null) {
                    uploadImageAndSaveProfile(email);
                } else {
                    updateAlumnoInfo(email);
                }
            }
        });


        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog(this);
        });

        btnCamera.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileImage);
        }
    }

    private void uploadImageAndSaveProfile(String email) {
        if (imageUri != null) {
            String url = "https://simulascore.com/apis/moduloAlumno/uploadImage.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d(TAG, "Server Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            if (status.equals("success")) {
                                String imageUrl = jsonResponse.getString("url");
                                Log.d(TAG, "Image URL: " + imageUrl);
                                saveImageUrlToDatabase(imageUrl);
                            } else {
                                String message = jsonResponse.getString("message");
                                Log.e(TAG, "Error: " + message);
                                Toast.makeText(EditProfileActivity.this, "Error al subir la imagen: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e(TAG, errorMsg);
                            Toast.makeText(EditProfileActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, error.getMessage());
                            Toast.makeText(EditProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("correoAlumno", email);
                    params.put("imagenPerfil", imageToString(imageUri));
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private String imageToString(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        String url = "https://simulascore.com/apis/moduloAlumno/updateImageUrl.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        if (status.equals("success")) {
                            Log.d(TAG, "Image URL updated successfully");
                            updateAlumnoInfo(email);
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Error: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e(TAG, errorMsg);
                    } else {
                        Log.e(TAG, error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("imageUrl", imageUrl);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                            String imageUrl = "https://simulascore.com" + data.getString("urlImagen").replace("\\", "/");

                            etName.setText(nombre);
                            etSurname.setText(apellido);

                            Log.d(TAG, "Image URL: " + imageUrl);

                            Glide.with(EditProfileActivity.this)
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

    private void updateAlumnoInfo(String email) {
        String url = "https://simulascore.com/apis/moduloAlumno/updateAlumnoInfo.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            Intent intent = new Intent(EditProfileActivity.this, home.class);
                            startActivity(intent);
                            Log.d(TAG, "Profile updated successfully");
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("nombre", etName.getText().toString());
                params.put("apellido", etSurname.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void showChangePasswordDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_change_password);

        EditText etNewPassword = dialog.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = dialog.findViewById(R.id.et_confirm_password);
        ImageView ivToggleNewPassword = dialog.findViewById(R.id.iv_toggle_new_password);
        ImageView ivToggleConfirmPassword = dialog.findViewById(R.id.iv_toggle_confirm_password);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        ivToggleNewPassword.setOnClickListener(v -> {
            if (etNewPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (etConfirmPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnConfirm.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (newPassword.isEmpty()) {
                etNewPassword.setError("Por favor, ingrese una contraseña");
                return;
            } else if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Por favor, confirme la contraseña");
                return;
            } else if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Las contraseñas no coinciden");
                return;
            } else {
                changePassword(newPassword);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void changePassword(String newPassword) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);

        if (email == null) {
            Log.e(TAG, "Email not found in shared preferences");
            return;
        }

        String url = "https://simulascore.com/apis/moduloAlumno/changePassword.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            Log.d(TAG, "Password updated successfully");
                            Toast.makeText(EditProfileActivity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Error: " + message);
                            Toast.makeText(EditProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            if (error.networkResponse != null) {
                String errorMsg = new String(error.networkResponse.data);
                Log.e(TAG, errorMsg);
                Toast.makeText(EditProfileActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, error.getMessage());
                Toast.makeText(EditProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("newPassword", newPassword);
                Log.d(TAG, "Params: " + params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
