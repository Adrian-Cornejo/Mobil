package com.example.simulascore;

public class QuestionWithFeedback extends Question {
    private String feedback; // Campo para la retroalimentación
    private String correctAnswer; // Respuesta correcta
    private String userAnswer; // Respuesta del usuario

    public QuestionWithFeedback(String id, String questionText, String option1, String option2, String option3, String option4, String imageUrl, String additionalText, String correctAnswer, String userAnswer, String feedback) {
        super(id, questionText, option1, option2, option3, option4, imageUrl, additionalText);
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
        this.feedback = feedback;
    }

    // Getters y Setters para la retroalimentación, respuesta correcta y respuesta del usuario
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    // Getters y Setters de la clase padre (Question)
    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public String getQuestionText() {
        return super.getQuestionText();
    }

    @Override
    public String getOption1() {
        return super.getOption1();
    }

    @Override
    public String getOption2() {
        return super.getOption2();
    }

    @Override
    public String getOption3() {
        return super.getOption3();
    }

    @Override
    public String getOption4() {
        return super.getOption4();
    }

    @Override
    public String getImageUrl() {
        return super.getImageUrl();
    }

    @Override
    public String getAdditionalText() {
        return super.getAdditionalText();
    }

    @Override
    public void setAdditionalText(String additionalText) {
        super.setAdditionalText(additionalText);
    }
}
