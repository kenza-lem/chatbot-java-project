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

            // Crée une instance de Gson avec l'option d'exclusion des champs sans @Expose
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            // Définir le type pour la désérialisation de la liste de problèmes
            Type problemeListType = new TypeToken<List<Probleme>>(){}.getType();

            // Désérialise le fichier JSON en une liste de problèmes
            listeProbleme = gson.fromJson(reader, problemeListType);

            // Ferme le fichier après traitement
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//
//    public List<Probleme> getListeProbleme() {
//        return listeProbleme;
//    }

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
}
