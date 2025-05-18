//package com.example.projetjava.model;
//import com.example.projetjava.model.GestionProbleme;
////classe juste pour tester rapidement les classes dans model
//public class    MainTest {
//    public  static void main(String[] args){
//        GestionProbleme gp = new GestionProbleme();
//        gp.ouvrirFichier();
//        gp.afficherTout();
//        gp.RechercherProbleme("Wi-Fi");
//        gp.RechercherProbleme("mi");
//    }
//}
package com.example.projetjava.model;

import com.example.projetjava.model.LlmService;

//public class MainTest {
//    public static void main(String[] args) {
//        LlmService llmService = new LlmService();
//        String question = "reseau public";
//        String reponse = llmService.rechercheSolution(question);
//        System.out.println("Réponse : " + reponse);
//    }
//}

import com.example.projetjava.model.LlmService;

import java.util.Scanner;

public class MainTest {
    public static void main(String[] args) {
        // Créez une instance de LlmService
        LlmService llmService = new LlmService();

        // Créez un scanner pour lire les entrées de l'utilisateur
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bienvenue dans le chatbot d'assistance technique !");
        System.out.println("Posez votre question ou tapez 'exit' pour quitter.");

        while (true) {
            // Demander à l'utilisateur de saisir une question
            System.out.print("Vous : ");
            String question = scanner.nextLine();

            // Si l'utilisateur tape 'exit', quitter la boucle
            if ("exit".equalsIgnoreCase(question)) {
                System.out.println("Au revoir !");
                break;
            }

            // Utiliser LlmService pour obtenir une solution
            String reponse = llmService.rechercheSolution(question);

            // Afficher la réponse du chatbot
            if (reponse.isEmpty()) {
                System.out.println("Chatbot : Désolé, je n'ai pas trouvé de solution pour votre question.");
            } else {
                System.out.println("Chatbot : " + reponse);
            }
        }

        // Fermer le scanner
        scanner.close();
    }
}
