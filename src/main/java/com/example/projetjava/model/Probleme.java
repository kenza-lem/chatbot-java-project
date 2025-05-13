package com.example.projetjava.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Probleme {

    public int id;

    public List<String> motCle;

    public List<QuestionReponse> problemes;

    public Probleme(int id, List<String> motCle, List<QuestionReponse> problemes) {
        this.id = id;
        this.motCle = motCle;
        this.problemes = problemes;
    }

    public int getId() {
        return id;

    }

    public List<String> getMotCle() {
        return motCle;
    }

    public List<QuestionReponse> getProblemes() {
        return problemes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMotCle(List<String> motCle) {
        this.motCle = motCle;

    }

    public void setProblemes(List<QuestionReponse> problemes) {
        this.problemes = problemes;
    }

}