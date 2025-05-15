package com.example.projetjava.model;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;





public class Probleme {

    private int id;
    @SerializedName("mot-cle")
    private List<String> motCle;

    private List<QuestionReponse> problemes;

    public Probleme() {
        this.motCle = new ArrayList<>();
        this.problemes = new ArrayList<>();
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