package com.example.simulascore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RetroalimentacionMejorEduExamen extends AppCompatActivity {

    private TextView tvQuestion, profileName, tvAdditionalText, tvFeedback, score;
    private ImageView ivQuestionImage;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext, btnPrevious, menu;
    private List<QuestionWithFeedback> questionList; // Changed to List<QuestionWithFeedback>
    private int currentQuestionIndex = 0;

    private static final String TAG = "RetroalimentacionMejorEduExamen";
    private String email;

    private  Integer examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retroalimentacion_mejoredu_examen);

        tvQuestion = findViewById(R.id.tv_question);
        profileName = findViewById(R.id.tv_student_info);
        score = findViewById(R.id.calificacion);
        tvAdditionalText = findViewById(R.id.tv_textoAdicional);
        tvFeedback = findViewById(R.id.tv_feedback);
        ivQuestionImage = findViewById(R.id.iv_question_image);
        rgOptions = findViewById(R.id.rg_options);
        rbOption1 = findViewById(R.id.rb_option1);
        rbOption2 = findViewById(R.id.rb_option2);
        rbOption3 = findViewById(R.id.rb_option3);
        rbOption4 = findViewById(R.id.rb_option4);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        menu = findViewById(R.id.btn_send_exam);

        questionList = new ArrayList<>();



        // Obtener el email y examId de SharedPreferences o Intent
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);
        examId = getIntent().getIntExtra("EXAM_ID", -1);


        Log.d("MyApp", "Email: " + email + ", Exam ID: " + examId);



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                } else {
                    Toast.makeText(RetroalimentacionMejorEduExamen.this, "Has completado todas las preguntas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    displayQuestion();
                } else {
                    Toast.makeText(RetroalimentacionMejorEduExamen.this, "Estás en la primera pregunta", Toast.LENGTH_SHORT).show();
                }
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RetroalimentacionMejorEduExamen.this, home.class);
                startActivity(intent);
            }
        });

        fetchQuestionsAndAnswers();
    }

    private void fetchQuestionsAndAnswers() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando preguntas...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/obtenerPreguntasYRespuestasMejorEdu.php?idExamen=" + examId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                JSONObject data = jsonResponse.getJSONObject("data");
                                JSONObject preguntasAlumnoObj = data.getJSONObject("preguntasAlumno");
                                JSONArray preguntasAlumno = preguntasAlumnoObj.getJSONArray("Español");
                                JSONArray respuestasAlumno = data.getJSONArray("respuestasAlumno");
                                JSONArray retroalimentacionYCorrectas = data.getJSONArray("retroalimentacionYCorrectas");

                                for (int i = 0; i < preguntasAlumno.length(); i++) {
                                    JSONObject questionObject = preguntasAlumno.getJSONObject(i);
                                    String questionId = questionObject.getString("id_pregunta");
                                    String questionText = questionObject.getString("pregunta");
                                    String option1 = questionObject.getString("respuesta1");
                                    String option2 = questionObject.getString("respuesta2");
                                    String option3 = questionObject.getString("respuesta3");
                                    String option4 = questionObject.getString("respuesta4");
                                    String imageUrl = questionObject.optString("url_imagen", "");
                                    String additionalText = questionObject.optString("texto_adicional", "");

                                    String userAnswer = "";
                                    for (int j = 0; j < respuestasAlumno.length(); j++) {
                                        JSONObject answerObject = respuestasAlumno.getJSONObject(j);
                                        if (answerObject.getString("questionId").equals(questionId)) {
                                            userAnswer = answerObject.getString("answerText");
                                            break;
                                        }
                                    }

                                    String correctAnswer = "";
                                    String feedback = "";
                                    for (int j = 0; j < retroalimentacionYCorrectas.length(); j++) {
                                        JSONObject feedbackObject = retroalimentacionYCorrectas.getJSONObject(j);
                                        if (feedbackObject.getString("id_pregunta").equals(questionId)) {
                                            correctAnswer = feedbackObject.getString("respuesta_correcta");
                                            feedback = feedbackObject.getString("retroa");
                                            break;
                                        }
                                    }

                                    questionList.add(new QuestionWithFeedback(
                                            questionId, questionText, option1, option2, option3, option4,
                                            imageUrl, additionalText, userAnswer, correctAnswer, feedback));
                                }

                                displayQuestion();
                                // Llama al método para obtener los datos del alumno y la calificación
                                fetchStudentInfoAndGrade();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(RetroalimentacionMejorEduExamen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String errorMessage = e.getMessage(); // Obtener el mensaje de la excepción
                            Log.e("RetroalimentacionOlimpiadaExamen", "Error en la respuesta del servidor: " + errorMessage);
                            Toast.makeText(RetroalimentacionMejorEduExamen.this, "Error en la respuesta del servidor: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(RetroalimentacionMejorEduExamen.this, "Error al cargar preguntas", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void fetchStudentInfoAndGrade() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando datos del alumno...");
        progressDialog.show();

        String url = "https://simulascore.com/apis/moduloExamenes/obtenerDatosAlumnoYCalificacionMejorEdu.php?email=" + email + "&idExamen=" + examId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                JSONObject data = jsonResponse.getJSONObject("data");
                                JSONObject alumnoObj = data.getJSONObject("alumno");
                                String studentName = alumnoObj.getString("nombre");
                                String lastName = alumnoObj.getString("apellido");

                                JSONArray resultados = data.getJSONArray("resultadosExamen");
                                if (resultados.length() > 0) {
                                    JSONObject resultado = resultados.getJSONObject(0); // Tomar el primer resultado
                                    String studentGrade = resultado.getString("puntaje_general");

                                    // Muestra la información en los TextViews correspondientes
                                    profileName.setText(studentName + " " + lastName);
                                    score.setText("Calificación: " + studentGrade);
                                } else {
                                    Toast.makeText(RetroalimentacionMejorEduExamen.this, "No se encontraron calificaciones para este examen.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(RetroalimentacionMejorEduExamen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String errorMessage = e.getMessage(); // Obtener el mensaje de la excepción
                            Log.e("RetroalimentacionOlimpiadaExamen", "Error en la respuesta del servidor: " + errorMessage);
                            Toast.makeText(RetroalimentacionMejorEduExamen.this, "Error en la respuesta del servidor: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(RetroalimentacionMejorEduExamen.this, "Error al cargar datos del alumno", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    private void displayQuestion() {
        QuestionWithFeedback currentQuestion = questionList.get(currentQuestionIndex);
        tvQuestion.setText(currentQuestion.getQuestionText());
        rbOption1.setText(currentQuestion.getOption1());
        rbOption2.setText(currentQuestion.getOption2());
        rbOption3.setText(currentQuestion.getOption3());
        rbOption4.setText(currentQuestion.getOption4());
        rgOptions.clearCheck();

        String imageUrl = currentQuestion.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load("https://simulascore.com" + imageUrl.replace("\\", "/"))
                    .into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        if (!currentQuestion.getAdditionalText().isEmpty()) {
            tvAdditionalText.setVisibility(View.VISIBLE);
            tvAdditionalText.setText(currentQuestion.getAdditionalText());
        } else {
            tvAdditionalText.setVisibility(View.GONE);
        }

        // Set user answer and correct answer in RadioGroup
        String userAnswer = currentQuestion.getUserAnswer();
        String correctAnswer = currentQuestion.getCorrectAnswer();

        resetOptionColors();

        if (userAnswer.equals(rbOption1.getText().toString())) {
            rbOption1.setChecked(true);
            setOptionColor(rbOption1, userAnswer.equals(correctAnswer));
        } else if (userAnswer.equals(rbOption2.getText().toString())) {
            rbOption2.setChecked(true);
            setOptionColor(rbOption2, userAnswer.equals(correctAnswer));
        } else if (userAnswer.equals(rbOption3.getText().toString())) {
            rbOption3.setChecked(true);
            setOptionColor(rbOption3, userAnswer.equals(correctAnswer));
        } else if (userAnswer.equals(rbOption4.getText().toString())) {
            rbOption4.setChecked(true);
            setOptionColor(rbOption4, userAnswer.equals(correctAnswer));
        }

        // Highlight the correct answer if user answer is wrong
        if (!userAnswer.equals(correctAnswer)) {
            if (correctAnswer.equals(rbOption1.getText().toString())) {
                rbOption1.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (correctAnswer.equals(rbOption2.getText().toString())) {
                rbOption2.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (correctAnswer.equals(rbOption3.getText().toString())) {
                rbOption3.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (correctAnswer.equals(rbOption4.getText().toString())) {
                rbOption4.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }

        // Display feedback
        tvFeedback.setText(currentQuestion.getFeedback());
    }

    private void resetOptionColors() {
        rbOption1.setTextColor(getResources().getColor(android.R.color.black));
        rbOption2.setTextColor(getResources().getColor(android.R.color.black));
        rbOption3.setTextColor(getResources().getColor(android.R.color.black));
        rbOption4.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void setOptionColor(RadioButton radioButton, boolean isCorrect) {
        if (isCorrect) {
            radioButton.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            radioButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
