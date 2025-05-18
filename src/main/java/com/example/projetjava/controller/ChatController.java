package com.example.projetjava.controller;

import com.example.projetjava.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ChatController {
    private GestionConversation gestionConversation;
    private GestionProbleme gestionProbleme;
    private Conversation conversationActuelle;
    private ObservableList<Conversation> historiqueConversations;
    private StringProperty messageActuel = new SimpleStringProperty("");
    private LlmService llmService;

    public ChatController() {
        this.gestionConversation = new GestionConversation();
        this.gestionProbleme = new GestionProbleme();
        this.llmService = new LlmService();
        this.historiqueConversations = FXCollections.observableArrayList(gestionConversation.getListeConversation());
        this.gestionProbleme.ouvrirFichier();

        nouvelleConversation();
    }

    public void nouvelleConversation() {
        this.conversationActuelle = gestionConversation.creerConversation();
        ajouterMessageSystem("Bonjour ! Je suis votre assistant technique. Comment puis-je vous aider aujourd'hui ?");
        refreshHistorique();
    }

    public void envoyerMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        int convId = Integer.parseInt(conversationActuelle.getId());
        gestionConversation.ajouterMsg(convId, message, true);

        // Ici on appelle ta méthode 'rechercheSolution' qui fait tout le travail
        String reponse = llmService.rechercheSolution(message);

        gestionConversation.ajouterMsg(convId, reponse, false);
        refreshHistorique();
    }

    public void marquerCommeResolu(boolean resolu) {
        int convId = Integer.parseInt(conversationActuelle.getId());
        gestionConversation.estResolue(convId, resolu);

        if (resolu) {
            ajouterMessageSystem("Parfait ! Je suis heureux d'avoir pu vous aider. N'hésitez pas à me solliciter pour d'autres questions.");
        } else {
            ajouterMessageSystem("Je suis désolé de ne pas avoir pu résoudre votre problème. Souhaitez-vous essayer avec une autre formulation ou un autre problème ?");
        }
    }

    public void ajouterFeedback(int note) {
        int convId = Integer.parseInt(conversationActuelle.getId());
        gestionConversation.ajouterFeedback(convId, note);

        switch (note) {
            case 1:
            case 2:
                ajouterMessageSystem("Je suis désolé que mon assistance n'ait pas été satisfaisante. Je travaille constamment à m'améliorer.");
                break;
            case 3:
                ajouterMessageSystem("Merci pour votre retour. J'espère pouvoir vous aider davantage la prochaine fois.");
                break;
            case 4:
            case 5:
                ajouterMessageSystem("Merci beaucoup pour votre excellent retour ! Je suis ravi d'avoir pu vous aider.");
                break;
        }
    }

    private void ajouterMessageSystem(String message) {
        int convId = Integer.parseInt(conversationActuelle.getId());
        gestionConversation.ajouterMsg(convId, message, false);
    }

    public void changerConversation(Conversation conversation) {
        this.conversationActuelle = conversation;
    }

    private void refreshHistorique() {
        Platform.runLater(() -> {
            historiqueConversations.setAll(gestionConversation.getListeConversation());
        });
    }

    // Getters pour l'affichage
    public ObservableList<Conversation> getHistoriqueConversations() {
        return historiqueConversations;
    }

    public Conversation getConversationActuelle() {
        return conversationActuelle;
    }

    public StringProperty messageActuelProperty() {
        return messageActuel;
    }

    public void setMessageActuel(String message) {
        this.messageActuel.set(message);
    }

    public List<Conversation> getConversationsNonResolues() {
        return gestionConversation.getListeConversationNonResolu();
    }
}
