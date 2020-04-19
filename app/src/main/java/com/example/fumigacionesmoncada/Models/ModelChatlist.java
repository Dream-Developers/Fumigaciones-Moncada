package com.example.fumigacionesmoncada.Models;

public class ModelChatlist {
    String id; //lo necesitar[e para obtener el id de chatList


    public ModelChatlist() {
    }

    public ModelChatlist(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}