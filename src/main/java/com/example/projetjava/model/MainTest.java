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

        LlmService llmService = new LlmService();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenue dans le chatbot d'assistance technique !");
        System.out.println("Posez votre question ou tapez 'exit' pour quitter.");

        while (true) {
            System.out.print("Vous : ");
            String question = scanner.nextLine();

            if ("exit".equalsIgnoreCase(question)) {
                System.out.println("Au revoir !");
                break;
            }

            String reponse = llmService.rechercheSolution(question);

            if (reponse.isEmpty()) {
                System.out.println("Chatbot : Désolé, je n'ai pas trouvé de solution pour votre question.");
            } else {
                System.out.println("Chatbot : " + reponse);
            }
        }
        scanner.close();
    }
}
