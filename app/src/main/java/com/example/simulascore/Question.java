package com.example.simulascore;
public class Question {
    private String id;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String imageUrl;
    private String additionalText; // Nuevo campo para el texto adicional

    public Question(String id, String questionText, String option1, String option2, String option3, String option4, String imageUrl, String additionalText) {
        this.id = id;
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.imageUrl = imageUrl;
        this.additionalText = additionalText;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }
}

