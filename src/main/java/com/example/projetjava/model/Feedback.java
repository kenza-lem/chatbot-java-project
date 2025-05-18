package com.example.projetjava.model;

public class Feedback {



        private int id;
        private int conversationId;
        private int rating; // 1-5 étoiles
        private String commentaire;
        private boolean solutionFonctionnelle;

        public Feedback(int conversationId, int rating, String commentaire, boolean solutionFonctionnelle) {
            this.conversationId = conversationId;
            this.rating = rating;
            this.commentaire = commentaire;
            this.solutionFonctionnelle = solutionFonctionnelle;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getConversationId() {
            return conversationId;
        }

        public void setConversationId(int conversationId) {
            this.conversationId = conversationId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            if (rating >= 1 && rating <= 5) {
                this.rating = rating;
            }
        }

        public String getCommentaire() {
            return commentaire;
        }

        public void setCommentaire(String commentaire) {
            this.commentaire = commentaire;
        }

        public boolean isSolutionFonctionnelle() {
            return solutionFonctionnelle;
        }

        public void setSolutionFonctionnelle(boolean solutionFonctionnelle) {
            this.solutionFonctionnelle = solutionFonctionnelle;
        }
    }

