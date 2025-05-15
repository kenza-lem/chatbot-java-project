package com.example.projetjava.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GestionProbleme {
    private List<Probleme> listeProbleme;

    public void ouvrirFichier() {
        try {
            // Ouvre le fichier JSON
            FileReader reader = new FileReader("src/main/resources/probleme.json");

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Probleme>>() {}.getType();
            List<Probleme> problemes = gson.fromJson(new FileReader("src/main/resources/probleme.json"), listType);
            listeProbleme = gson.fromJson(reader, listType);
             reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public List<Probleme> getListeProbleme() {
        return listeProbleme;
   }

    public void RechercherProbleme(String motCle) {
        boolean trouve = false;
        for (Probleme p : listeProbleme) {
            if (p.getMotCle() != null && p.getMotCle().contains(motCle)) {
                trouve = true;
                System.out.println("Solutions trouvées pour : " + motCle);
                for (QuestionReponse qr : p.getProblemes()) {
                    System.out.println("- Question : " + qr.getQuestion());
                    System.out.println("  Réponse : " + qr.getSolution());
                    System.out.println();
                }
            }
        }
        if (!trouve) {
            System.out.println("Aucun problème trouvé pour : " + motCle);
        }


    }
    public void afficherTout() {
        for (Probleme p : listeProbleme) {
            System.out.println("Problème ID: " + p.getId());
            System.out.println("Mots-clés: " + p.getMotCle());
            for (QuestionReponse qr : p.getProblemes()) {
                System.out.println("  Question: " + qr.getQuestion());
                System.out.println("  Réponse : " + qr.getSolution());
            }
            System.out.println("-----");
        }
    }

}
