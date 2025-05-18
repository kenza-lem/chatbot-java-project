package com.example.projetjava.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GestionConversation {
    private List<Conversation> listeConversation;
    private static final String fichierConversation = "src/main/resources/conversation.json";
    private int nextIdConversation = 1;

    public GestionConversation() {
        this.listeConversation = new ArrayList<>();
        chargerConversation();
    }

    private void chargerConversation() {
        try {
            FileReader file = new FileReader(fichierConversation);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Conversation>>() {}.getType();
            listeConversation = gson.fromJson(file, listType);
            file.close();

            if (listeConversation != null && !listeConversation.isEmpty()) {
                for (Conversation conv : listeConversation) {
                    try {
                        int convId = Integer.parseInt(conv.getId());
                        if (convId >= nextIdConversation) {
                            nextIdConversation = convId + 1;
                        }
                    } catch (NumberFormatException e) {
                        // Gérer les IDs non numériques si nécessaire
                    }
                }
            } else {
                listeConversation = new ArrayList<>();
            }
        } catch (IOException e) {
            listeConversation = new ArrayList<>();

        }
    }
//cest une methode pour savegarder la cnv dans json
public void sauvegarderCnv(){
        try {
            FileWriter fwriter = new FileWriter(fichierConversation);
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(listeConversation,fwriter);
            fwriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
}
//cest une methode pour creer une nouvelle conversation
    public Conversation creerConversation() {
        Conversation conversation = new Conversation();
        conversation.setId(String.valueOf(nextIdConversation));
        nextIdConversation++;
        listeConversation.add(conversation);
        sauvegarderCnv();
        return conversation;
    }
    //cest une methode pour ajputer une cnv a une cnv existante
    public void ajouterMsg(int idConversation, String msg,boolean isUser) {
        Conversation cnv=trouverCnv(idConversation);
        if (cnv !=null){
            cnv.addMessage(msg,isUser);
            sauvegarderCnv();
        }
    }
    //methode pour marquer que la cnv est resolu
    public void estResolue(int conId,boolean resolu){
        Conversation cnv=trouverCnv(conId);
        if(cnv != null){
            cnv.setResolved(resolu);
            sauvegarderCnv();
        }
    }
    //on doit ajouter un feedback a une conversation
    public void ajouterFeedback(int conversationId, int note) {
        Conversation cnv= trouverCnv(conversationId);
        if (cnv != null) {
            cnv.addFeedback(note);
            sauvegarderCnv();
        }
    }
    // methode pour trouver une cnv par id
    public  Conversation trouverCnv(int idConversation){
        for (Conversation cnv : listeConversation) {
            if (cnv.getId().equals(String.valueOf(idConversation))) {
                return cnv;
            }
        }
        return null;
    }
    //obtenir toutes les cnv
    public List<Conversation> getListeConversation() {
        return listeConversation;
    }
    //obtenir les cnv non resolu
    public List<Conversation> getListeConversationNonResolu() {
        List<Conversation> listeConversationNonResolu = new ArrayList<>();
        for (Conversation cnv : listeConversation) {
            if (!cnv.isResolved()) {
                listeConversationNonResolu.add(cnv);
            }
        }
        return listeConversationNonResolu;
    }
}
