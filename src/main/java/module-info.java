module com.example.projetjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires opennlp.tools.models;
    requires java.net.http;
    requires java.desktop;

    opens com.example.projetjava to javafx.graphics, javafx.fxml;
    opens com.example.projetjava.model to com.google.gson;

    exports com.example.projetjava.view;
    exports com.example.projetjava;
    exports com.example.projetjava.controller;
    exports com.example.projetjava.model;
}
