module com.example.projetjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.projetjava to javafx.fxml;
    exports com.example.projetjava;
}