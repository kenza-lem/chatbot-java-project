package com.example.projetjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import com.example.projetjava.view.SimpleView;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Lancer l'interface du chatbot
        new SimpleView().start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}