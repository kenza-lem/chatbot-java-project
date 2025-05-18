package com.example.projetjava.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class LlmService {
    private final String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey = "AIzaSyAlOQJIe_ldlcpfEK77ZllcLcbanO25z-s";
    private final HttpClient client;
    private final Gson gson;
    private final GestionProbleme gestionProbleme;

    public LlmService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.gestionProbleme = new GestionProbleme();
        this.gestionProbleme.ouvrirFichier();
    }

    public String analyserQuestion(String question) {
        try {
            Map<String, Object> prompt = new HashMap<>();
            prompt.put("role", "user");
            prompt.put("parts", List.of(Map.of("text",
                    "Voici une question : \"" + question + "\". " +
                            "Identifie les mots-clés pertinents, séparés par des virgules, sans texte supplémentaire."
            )));
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", List.of(prompt));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            if (jsonResponse.has("problemes") && jsonResponse.getAsJsonArray("problemes").size() > 0) {
                JsonArray candidates = jsonResponse.getAsJsonArray("problemes");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = candidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
                    if (parts.size() > 0) {
                        return parts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
            return ""; // Si rien n’est trouvé dans la réponse

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String rechercheSolution(String question) {
        // Vérification directe de la correspondance de la question
        for (Probleme probleme : gestionProbleme.getListeProbleme()) {
            for (QuestionReponse qr : probleme.getProblemes()) {
                if (qr.getQuestion().equalsIgnoreCase(question.trim())) {
                    return "Solution trouvée dans la base de connaissances :\n" + qr.getSolution();
                }
            }
        }

        // Si aucune correspondance directe n'est trouvée, procéder à l'analyse des mots-clés
        String motsCles = analyserQuestion(question);
        if (motsCles.isEmpty()) {
            return genererReponseDirecte(question);
        }

        List<QuestionReponse> solutionsTrouvees = new ArrayList<>();
        String[] listMotsCles = motsCles.split(",");

        for (String motCle : listMotsCles) {
            for (Probleme probleme : gestionProbleme.getListeProbleme()) {
                if (probleme.getMotCle() != null) {
                    for (String mc : probleme.getMotCle()) {
                        if (mc.trim().equalsIgnoreCase(motCle.trim())) {
                            solutionsTrouvees.addAll(probleme.getProblemes());
                            break;
                        }
                    }
                }
            }
        }

        if (!solutionsTrouvees.isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append("J'ai trouvé ces solutions qui pourraient vous aider:\n\n");

            for (int i = 0; i < Math.min(3, solutionsTrouvees.size()); i++) {
                QuestionReponse qr = solutionsTrouvees.get(i);
                result.append("• Pour \"").append(qr.getQuestion()).append("\":\n")
                        .append("  ").append(qr.getSolution()).append("\n\n");
            }

            return result.toString();
        } else {
            return genererReponseDirecte(question);
        }
    }


    private String genererReponseDirecte(String question) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();

            contents.put("role", "user");
            contents.put("parts", List.of(Map.of("text",
                    "Je suis un chatbot d'assistance technique. Un utilisateur me pose la question suivante: \"" +
                            question + "\". Donne-moi une réponse concise et pratique pour l'aider à résoudre ce problème " +
                            "technique. Limite ta réponse à 3-4 phrases maximum et propose des étapes claires si possible.")));

            requestBody.put("contents", List.of(contents));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            if (jsonResponse.has("candidates")) {
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = candidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
                    if (parts.size() > 0) {
                        return parts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
            return "Désolé, je n'ai pas pu trouver de solution à votre problème. Essayez de reformuler votre question ou contactez le support technique.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Une erreur s'est produite lors de la recherche d'une solution. Veuillez réessayer plus tard.";
        }
    }
}
