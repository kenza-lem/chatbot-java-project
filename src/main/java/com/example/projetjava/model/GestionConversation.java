package com.example.projetjava.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestionConversation {
    private List<Conversation> listeConversation;
    private static final String fichierConversation = "src/main/resources/conversation.json";
    private int nextIdConversation = 1;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setPrettyPrinting()
            .create();

    public GestionConversation() {
        this.listeConversation = new ArrayList<>();
        chargerConversation();
    }

    private void chargerConversation() {
        try {
            FileReader file = new FileReader(fichierConversation);
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

                    }
                }
            } else {
                listeConversation = new ArrayList<>();
            }
        } catch (IOException e) {
            listeConversation = new ArrayList<>();
        }
    }

    public void sauvegarderCnv() {
        try {
            FileWriter fwriter = new FileWriter(fichierConversation);
            gson.toJson(listeConversation, fwriter);
            fwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Conversation creerConversation() {
        Conversation conversation = new Conversation();
        conversation.setId(String.valueOf(nextIdConversation));
        nextIdConversation++;
        listeConversation.add(conversation);
        sauvegarderCnv();
        return conversation;
    }

    public void ajouterMsg(int idConversation, String msg, boolean isUser) {
        Conversation cnv = trouverCnv(idConversation);
        if (cnv != null) {
            cnv.addMessage(msg, isUser);
            sauvegarderCnv();
        }
    }

    public void estResolue(int conId, boolean resolu) {
        Conversation cnv = trouverCnv(conId);
        if (cnv != null) {
            cnv.setResolved(resolu);
            sauvegarderCnv();
        }
    }

    public void ajouterFeedback(int conversationId, int note) {
        Conversation cnv = trouverCnv(conversationId);
        if (cnv != null) {
            cnv.addFeedback(note);
            sauvegarderCnv();
        }
    }

    public Conversation trouverCnv(int idConversation) {
        for (Conversation cnv : listeConversation) {
            if (cnv.getId().equals(String.valueOf(idConversation))) {
                return cnv;
            }
        }
        return null;
    }

    public List<Conversation> getListeConversation() {
        return listeConversation;
    }

    public List<Conversation> getListeConversationNonResolu() {
        List<Conversation> listeConversationNonResolu = new ArrayList<>();
        for (Conversation cnv : listeConversation) {
            if (!cnv.isResolved()) {
                listeConversationNonResolu.add(cnv);
            }
        }
        return listeConversationNonResolu;
    }

    public void supprimerConversation(Conversation conversation) {
        listeConversation.remove(conversation);
        sauvegarderCnv();
    }
}
