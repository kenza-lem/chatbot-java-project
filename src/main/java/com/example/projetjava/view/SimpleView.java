package com.example.projetjava.view;

import com.example.projetjava.controller.ChatController;
import com.example.projetjava.model.Conversation;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Rating;

import java.util.Objects;

public class SimpleView extends Application {
    private ChatController chatController;
    private VBox messagesContainer;
    private ScrollPane scrollPane;
    private TextField messageInput;
    private ListView<Conversation> historiqueListView;
    private VBox centerPanel;
    private VBox messagesPanel;

    @Override
    public void start(Stage primaryStage) {
        chatController = new ChatController();
        primaryStage.setTitle("Chat Helper");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Sidebar
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setSpacing(10);
        sidebar.setStyle("-fx-background-color: #222;");

        // Titre en haut (nom du chatbot)
        Label title = new Label("Relatus.AI");
        title.getStyleClass().add("sidebar-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-text-fill: #00FF99; -fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 30 0 20 0;");

        // Historique des conversations
        historiqueListView = new ListView<>();
        historiqueListView.getStyleClass().add("sidebar-history");
        historiqueListView.setItems(FXCollections.observableArrayList(chatController.getHistoriqueConversations()));
        historiqueListView.setCellFactory(list -> new ConversationListCell());
        VBox.setVgrow(historiqueListView, Priority.ALWAYS);
        historiqueListView.setStyle("-fx-background-color: transparent; -fx-text-fill: #fff; -fx-border-color: #333; -fx-border-width: 0 0 1 0;");

        sidebar.getChildren().addAll(title, historiqueListView);
        root.setLeft(sidebar);

        // Zone centrale (messages + input/feedback)
        centerPanel = new VBox();
        centerPanel.setSpacing(0);
        centerPanel.setPadding(new Insets(0));
        centerPanel.setAlignment(Pos.TOP_CENTER);

        messagesPanel = createRightPanel();
        centerPanel.getChildren().add(messagesPanel);
        updateCenterPanel();
        root.setCenter(centerPanel);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/View/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox();
        rightPanel.setSpacing(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: transparent;");

        // Zone de messages
        messagesContainer = new VBox();
        messagesContainer.setSpacing(10);
        messagesContainer.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rightPanel.getChildren().addAll(scrollPane);
        updateMessagesView();
        return rightPanel;
    }

    private HBox createInputBox() {
        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(15));
        inputBox.setSpacing(10);
        inputBox.setStyle("-fx-background-color: #fff; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, -3); -fx-background-radius: 20px;");

        // Zone de saisie du message
        messageInput = new TextField();
        messageInput.setPromptText("Écrivez votre question ici...");
        messageInput.setPrefHeight(40);
        messageInput.setFont(Font.font("Segoe UI", 14));
        messageInput.getStyleClass().add("message-input");
        messageInput.setStyle("-fx-background-color: #222; -fx-text-fill: #fff; -fx-border-color: #00FF99; -fx-border-radius: 20px; -fx-background-radius: 20px;");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        // Binding avec le controller
        chatController.messageActuelProperty().bindBidirectional(messageInput.textProperty());

        // Bouton d'envoi
        Button sendButton = new Button("Envoyer");
        sendButton.setPrefHeight(40);
        sendButton.getStyleClass().add("send-button");
        sendButton.setStyle("-fx-background-color: #00FF99; -fx-text-fill: #181818; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand;");
        sendButton.setOnAction(e -> sendMessage());

        // Envoyer le message avec la touche Entrée
        messageInput.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(messageInput, sendButton);
        return inputBox;
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            chatController.envoyerMessage(message);
            messageInput.clear();
            updateMessagesView();
            updateCenterPanel();
        }
    }

    private void updateMessagesView() {
        messagesContainer.getChildren().clear();

        if (chatController.getConversationActuelle() != null) {
            for (Conversation.Message message : chatController.getConversationActuelle().getMessages()) {
                HBox messageBox = new HBox(10);
                messageBox.setPadding(new Insets(5));

                if (message.isFromUser()) {
                    messageBox.setAlignment(Pos.CENTER_RIGHT);

                    VBox textBox = new VBox();
                    textBox.getStyleClass().add("message-bubble-user");
                    textBox.setStyle("-fx-background-color: #00FF99; -fx-background-radius: 15 0 15 15; -fx-padding: 10; -fx-text-fill: #181818;");

                    Text text = new Text(message.getContent());
                    text.setFill(Color.web("#181818"));
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.LIGHTGRAY);
                    timestamp.setStyle("-fx-font-size: 10;");

                    textBox.getChildren().addAll(text, timestamp);
                    messageBox.getChildren().add(textBox);
                } else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);

                    VBox textBox = new VBox();
                    textBox.getStyleClass().add("message-bubble-bot");
                    textBox.setStyle("-fx-background-color: #222; -fx-background-radius: 0 15 15 15; -fx-padding: 10; -fx-text-fill: #fff; -fx-border-color: #00FF99; -fx-border-width: 1;");

                    Text text = new Text(message.getContent());
                    text.setFill(Color.web("#fff"));
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.GRAY);
                    timestamp.setStyle("-fx-font-size: 10;");

                    textBox.getChildren().addAll(text, timestamp);
                    messageBox.getChildren().addAll(textBox);
                }

                // Animation du message
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageBox);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();

                messagesContainer.getChildren().add(messageBox);
            }

            // Faire défiler vers le bas
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        }
    }

    private void playStartupAnimation(BorderPane root) {
        // Animation de fondu au démarrage
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animation de rebond pour l'interface
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), root);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        // Jouer les animations séquentiellement
        SequentialTransition sequence = new SequentialTransition(fadeIn, scaleIn);
        sequence.play();
    }

    // Cellule personnalisée pour l'affichage des conversations dans la liste
    private class ConversationListCell extends ListCell<Conversation> {
        @Override
        protected void updateItem(Conversation conversation, boolean empty) {
            super.updateItem(conversation, empty);

            if (empty || conversation == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Circle statusCircle = new Circle(5);
                statusCircle.setFill(conversation.isResolved() ? Color.GREEN : Color.ORANGE);

                VBox vbox = new VBox(3);
                Label idLabel = new Label("Conversation #" + conversation.getId());
                idLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

                Label dateLabel = new Label(conversation.getFormattedDateTime());
                dateLabel.setFont(Font.font("System", 12));
                dateLabel.setTextFill(Color.GRAY);

                String lastMessagePreview = "";
                if (!conversation.getMessages().isEmpty()) {
                    Conversation.Message lastMsg = conversation.getMessages().get(conversation.getMessages().size() - 1);
                    String content = lastMsg.getContent();
                    lastMessagePreview = (content.length() > 25) ? content.substring(0, 25) + "..." : content;
                }

                Label previewLabel = new Label(lastMessagePreview);
                previewLabel.setFont(Font.font("System", 12));
                previewLabel.setTextFill(Color.DARKGRAY);

                vbox.getChildren().addAll(idLabel, dateLabel, previewLabel);

                // Bouton de suppression
                Button deleteBtn = new Button("🗑");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px;");
                deleteBtn.setOnAction(e -> {
                    chatController.supprimerConversation(conversation);
                    updateHistoriqueListView();
                });

                hbox.getChildren().addAll(statusCircle, vbox, deleteBtn);

                setGraphic(hbox);
            }
        }
    }

    private void updateHistoriqueListView() {
        historiqueListView.setItems(FXCollections.observableArrayList(chatController.getHistoriqueConversations()));
    }

    private void updateCenterPanel() {
        // Supprime tout sauf le panel de messages
        centerPanel.getChildren().setAll(messagesPanel);
        Conversation conv = chatController.getConversationActuelle();
        if (conv != null && conv.isResolved()) {
            centerPanel.getChildren().add(createFeedbackBox(conv));
        } else {
            centerPanel.getChildren().add(createInputBox());
        }
    }

    private HBox createFeedbackBox(Conversation conversation) {
        HBox feedbackBox = new HBox(30);
        feedbackBox.setAlignment(Pos.CENTER);
        feedbackBox.setPadding(new Insets(30, 0, 30, 0));
        feedbackBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 20px;");

        Label goodEmoji = new Label("👍");
        goodEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-radius: 50%; -fx-padding: 10;");
        goodEmoji.setOnMouseEntered(e -> goodEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-color: #00FF99; -fx-background-radius: 50%; -fx-padding: 10;"));
        goodEmoji.setOnMouseExited(e -> goodEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-radius: 50%; -fx-padding: 10;"));
        goodEmoji.setOnMouseClicked((MouseEvent e) -> {
            chatController.ajouterFeedback(1);
            updateMessagesView();
            updateCenterPanel();
        });

        Label badEmoji = new Label("👎");
        badEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-radius: 50%; -fx-padding: 10;");
        badEmoji.setOnMouseEntered(e -> badEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-color: #00FF99; -fx-background-radius: 50%; -fx-padding: 10;"));
        badEmoji.setOnMouseExited(e -> badEmoji.setStyle("-fx-font-size: 40px; -fx-cursor: hand; -fx-background-radius: 50%; -fx-padding: 10;"));
        badEmoji.setOnMouseClicked((MouseEvent e) -> {
            chatController.ajouterFeedback(0);
            updateMessagesView();
            updateCenterPanel();
        });

        feedbackBox.getChildren().addAll(goodEmoji, badEmoji);
        return feedbackBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}