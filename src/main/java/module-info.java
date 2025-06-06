module com.example.projetjava {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Bibliothèques
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires opennlp.tools.models;
    requires java.net.http;
    requires java.desktop;

    // Ouverture à la réflexion (ex : Gson)
    opens com.example.projetjava to javafx.graphics, javafx.fxml;
    opens com.example.projetjava.model to com.google.gson;

    // Exposition à JavaFX (Application, FXML, etc.)
    exports com.example.projetjava.view;
    //exports com.example.projetjava.controller;
    exports com.example.projetjava.model;
}
