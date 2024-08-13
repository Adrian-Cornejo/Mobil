package com.example.simulascore;

public class MessagesA {
    private String idEmisor;
    private String idReceptor;
    private String messageContent;
    private String fechaHora;
    private boolean isSent;

    public MessagesA(String idEmisor, String idReceptor, String messageContent, String fechaHora, boolean isSent) {
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.messageContent = messageContent;
        this.fechaHora = fechaHora;
        this.isSent = isSent;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public boolean isSent() {
        return isSent;
    }
}
