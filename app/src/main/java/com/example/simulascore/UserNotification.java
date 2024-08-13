package com.example.simulascore;

public class UserNotification {
    private String contenido;
    private String profesor;
    private String fechaHora;

    // Constructor
    public UserNotification(String contenido, String profesor, String fechaHora) {
        this.contenido = contenido;
        this.profesor = profesor;
        this.fechaHora = fechaHora;
    }

    // Métodos Getter
    public String getContenido() {
        return contenido;
    }

    public String getProfesor() {
        return profesor;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}
