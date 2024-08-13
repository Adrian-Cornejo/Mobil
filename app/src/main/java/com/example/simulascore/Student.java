package com.example.simulascore;

public class Student {
    private String code;
    private String name;
    private String surname;
    private String email;
    private boolean selected;

    public Student(String code, String name, String surname, String email) {
        this.code = code;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.selected = false;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
