package com.example.projetjava.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private String id;
    private LocalDateTime creationDate;
    private int feedbackRating;
    private List<Message> messages;
    private boolean resolved;

    // Constructeur avec paramètres
    public Conversation(String id, LocalDateTime creationDate, int feedbackRating) {
        this.id = id;
        this.creationDate = creationDate;
        this.feedbackRating = feedbackRating;
        this.messages = new ArrayList<>();
        this.resolved = false;
    }

    // Constructeur par défaut
    public Conversation() {
        this.creationDate = LocalDateTime.now();
        this.feedbackRating = 0;
        this.messages = new ArrayList<>();
        this.resolved = false;
    }

    // Ajouter un message à la conversation
    public void addMessage(String content, boolean isFromUser) {
        messages.add(new Message(content, isFromUser, LocalDateTime.now()));
    }

    // Marquer la conversation comme résolue
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    // Ajouter un feedback (note de 1 à 5)
    public void addFeedback(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.feedbackRating = rating;
        }
    }

    // Getters et setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return creationDate.format(formatter);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public boolean isResolved() {
        return resolved;
    }

    public int getFeedbackRating() {
        return feedbackRating;
    }

    // Classe interne représentant un message
    public static class Message {
        private String content;
        private boolean fromUser;
        private LocalDateTime timestamp;

        public Message(String content, boolean fromUser, LocalDateTime timestamp) {
            this.content = content;
            this.fromUser = fromUser;
            this.timestamp = timestamp;
        }

        public String getContent() {
            return content;
        }

        public boolean isFromUser() {
            return fromUser;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getFormattedTimestamp() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return timestamp.format(formatter);
        }
    }
}
