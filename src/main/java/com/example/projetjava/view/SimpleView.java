package com.example.projetjava.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimpleView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/simpleview.fxml"));
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Chatbot Simple");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
