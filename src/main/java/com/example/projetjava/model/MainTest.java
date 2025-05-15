package com.example.projetjava.model;
import com.example.projetjava.model.GestionProbleme;
//classe juste pour tester rapidement les classes dans model
public class MainTest {
    public  static void main(String[] args){
        GestionProbleme gp = new GestionProbleme();
        gp.ouvrirFichier();
        gp.afficherTout();
        gp.RechercherProbleme("Wi-Fi");
        gp.RechercherProbleme("mi");
    }
}
