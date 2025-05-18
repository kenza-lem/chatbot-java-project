package com.example.projetjava.view;

import com.example.projetjava.controller.ChatController;
import com.example.projetjava.model.Conversation;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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

    @Override
    public void start(Stage primaryStage) {
        chatController = new ChatController();

        // Configuration de la fenêtre principale
        primaryStage.setTitle("HelpDesk Assistant");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/projetjava/icon/support-icon.png"))));

        // Création du layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f2f5;");

        // En-tête du chatbot
        HBox header = createHeader();
        root.setTop(header);

        // Zone centrale avec historique des conversations
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        // Panneau de gauche (historique)
        VBox leftPanel = createLeftPanel();

        // Panneau de droite (messages)
        VBox rightPanel = createRightPanel();

        splitPane.getItems().addAll(leftPanel, rightPanel);
        root.setCenter(splitPane);

        // Zone de saisie de message
        HBox inputBox = createInputBox();
        root.setBottom(inputBox);

        // Configuration de la scène
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/projetjava/css/styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Animation de démarrage
        playStartupAnimation(root);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));
        header.setSpacing(10);
        header.setStyle("-fx-background-color: #4285f4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");

        // Logo animé
        Circle logoCircle = new Circle(25);
        logoCircle.setFill(Color.WHITE);

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/projetjava/icon/robot-logo.png"))));
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        logoView.setClip(logoCircle);

        // Animation du logo
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), logoView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.play();

        // Titre
        Label titleLabel = new Label("HelpDesk Assistant");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setStyle("-fx-background-color: white;");

        Label subtitleLabel = new Label("Support Technique");
        subtitleLabel.setFont(Font.font("Segoe UI", 16));
        subtitleLabel.setTextFill(Color.WHITE);

        // Bouton nouvelle conversation
        Button newChatButton = new Button("Nouvelle conversation");
        newChatButton.getStyleClass().add("new-chat-button");
        HBox.setMargin(newChatButton, new Insets(0, 0, 0, 20));
        newChatButton.setOnAction(e -> {
            chatController.nouvelleConversation();
            updateMessagesView();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoView, titleLabel, separator, subtitleLabel, spacer, newChatButton);
        return header;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setMinWidth(250);
        leftPanel.setStyle("-fx-background-color: #ffffff;");

        Label historyLabel = new Label("Historique des conversations");
        historyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Liste de l'historique des conversations
        historiqueListView = new ListView<>(chatController.getHistoriqueConversations());
        historiqueListView.setCellFactory(lv -> new ConversationListCell());
        historiqueListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chatController.changerConversation(newVal);
                updateMessagesView();
            }
        });
        VBox.setVgrow(historiqueListView, Priority.ALWAYS);

        // Section non résolus
        Label nonResolusLabel = new Label("Problèmes non résolus");
        nonResolusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        VBox nonResolusBox = new VBox(5);
        chatController.getConversationsNonResolues().forEach(conv -> {
            Button convButton = new Button("Conversation #" + conv.getId());
            convButton.getStyleClass().add("conversation-button");
            convButton.setMaxWidth(Double.MAX_VALUE);
            convButton.setOnAction(e -> {
                chatController.changerConversation(conv);
                updateMessagesView();
                historiqueListView.getSelectionModel().select(conv);
            });
            nonResolusBox.getChildren().add(convButton);
        });

        leftPanel.getChildren().addAll(historyLabel, historiqueListView, nonResolusLabel, nonResolusBox);
        return leftPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox();
        rightPanel.setSpacing(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #eceff1;");

        // Zone de messages
        messagesContainer = new VBox();
        messagesContainer.setSpacing(10);
        messagesContainer.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Zone d'évaluation
        HBox feedbackBox = new HBox(10);
        feedbackBox.setAlignment(Pos.CENTER);
        feedbackBox.setPadding(new Insets(10));
        feedbackBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label feedbackLabel = new Label("Ce problème est-il résolu ? ");

        // Boutons résolu/non résolu
        Button resoluButton = new Button("Résolu");
        resoluButton.getStyleClass().add("resolved-button");
        resoluButton.setOnAction(e -> chatController.marquerCommeResolu(true));

        Button nonResoluButton = new Button("Non résolu");
        nonResoluButton.getStyleClass().add("unresolved-button");
        nonResoluButton.setOnAction(e -> chatController.marquerCommeResolu(false));

        // Rating
        Label ratingLabel = new Label("Noter l'assistance : ");
        Rating rating = new Rating();
        rating.setMax(5);
        rating.setPartialRating(false);
        rating.ratingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chatController.ajouterFeedback(newVal.intValue());
            }
        });

        feedbackBox.getChildren().addAll(feedbackLabel, resoluButton, nonResoluButton, new Separator(javafx.geometry.Orientation.VERTICAL), ratingLabel, rating);

        rightPanel.getChildren().addAll(scrollPane, feedbackBox);

        // Initialiser avec les messages
        updateMessagesView();

        return rightPanel;
    }

    private HBox createInputBox() {
        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(15));
        inputBox.setSpacing(10);
        inputBox.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, -3);");

        // Zone de saisie du message
        messageInput = new TextField();
        messageInput.setPromptText("Écrivez votre question ici...");
        messageInput.setPrefHeight(40);
        messageInput.setFont(Font.font("Segoe UI", 14));
        messageInput.getStyleClass().add("message-input");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        // Binding avec le controller
        chatController.messageActuelProperty().bindBidirectional(messageInput.textProperty());

        // Bouton d'envoi
        Button sendButton = new Button("Envoyer");
        sendButton.setPrefHeight(40);
        sendButton.getStyleClass().add("send-button");
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
                    textBox.setStyle("-fx-background-color: #4285f4; -fx-background-radius: 15 0 15 15; -fx-padding: 10;");

                    Text text = new Text(message.getContent());
                    text.setFill(Color.WHITE);
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.LIGHTGRAY);
                    timestamp.setStyle("-fx-font-size: 10;");

                    textBox.getChildren().addAll(text, timestamp);
                    messageBox.getChildren().add(textBox);
                } else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);

                    ImageView botIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/projetjava/icon/bot-icon.png"))));
                    botIcon.setFitHeight(30);
                    botIcon.setFitWidth(30);
                    botIcon.setEffect(new DropShadow(5, Color.LIGHTGRAY));

                    VBox textBox = new VBox();
                    textBox.setStyle("-fx-background-color: white; -fx-background-radius: 0 15 15 15; -fx-padding: 10;");

                    Text text = new Text(message.getContent());
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.GRAY);
                    timestamp.setStyle("-fx-font-size: 10;");

                    textBox.getChildren().addAll(text, timestamp);
                    messageBox.getChildren().addAll(botIcon, textBox);
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
                hbox.getChildren().addAll(statusCircle, vbox);

                setGraphic(hbox);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}