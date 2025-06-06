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

    /**
     * Ajoute un feedback (note) à la conversation actuelle et affiche un message
     * de réponse approprié en fonction de la note donnée.
     * 
     * @param note La note donnée par l'utilisateur (de 1 à 5)
     */
    public void ajouterFeedback(int note) {
        try {
            // Conversion sécurisée de l'ID de la conversation de String en int
            int convId = Integer.parseInt(conversationActuelle.getId());
            
            // Vérification et correction de la note si nécessaire
            if (note < 1) {
                note = 1;
            } else if (note > 5) {
                note = 5;
            }
            
            // Enregistrement du feedback dans la conversation
            gestionConversation.ajouterFeedback(convId, note);
            
            // Affichage d'un message approprié selon la note
            switch (note) {
                case 1:
                case 2:
                    // Message pour une note faible
                    ajouterMessageSystem("Je suis désolé que mon assistance n'ait pas été satisfaisante. Je travaille constamment à m'améliorer.");
                    break;
                case 3:
                    // Message pour une note moyenne
                    ajouterMessageSystem("Merci pour votre retour. J'espère pouvoir vous aider davantage la prochaine fois.");
                    break;
                case 4:
                case 5:
                    // Message pour une note élevée
                    ajouterMessageSystem("Merci beaucoup pour votre excellent retour ! Je suis ravi d'avoir pu vous aider.");
                    break;
            }
            
            // Rafraîchissement de l'interface utilisateur
            refreshHistorique();
        } catch (NumberFormatException e) {
            System.err.println("Erreur lors de la conversion de l'ID de conversation: " + e.getMessage());
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

    public void supprimerConversation(Conversation conversation) {
        gestionConversation.supprimerConversation(conversation);
        refreshHistorique();
        if (conversation == conversationActuelle) {
            nouvelleConversation();
        }
    }
    
    /**
     * Supprime toutes les conversations de l'historique et crée une nouvelle conversation.
     */
    public void supprimerToutesConversations() {
        // Obtenir une copie de la liste pour éviter les ConcurrentModificationException
        List<Conversation> conversations = new ArrayList<>(gestionConversation.getListeConversation());
        
        // Supprimer chaque conversation
        for (Conversation conversation : conversations) {
            gestionConversation.supprimerConversation(conversation);
        }
        
        // Vider l'observable list pour mise à jour immédiate de l'UI
        historiqueConversations.clear();
        
        // Créer une nouvelle conversation
        nouvelleConversation();
    }
}
