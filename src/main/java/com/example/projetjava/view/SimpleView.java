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
import javafx.scene.shape.Polygon;
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
    private BorderPane centerBorderPane; // New BorderPane for center layout
    private VBox messagesPanel;
    private BorderPane root;
    private VBox sidebar;
    private Button toggleButton;
    private boolean sidebarVisible = true;
    private double sidebarWidth = 220;

    @Override
    public void start(Stage primaryStage) {
        chatController = new ChatController();
        primaryStage.setTitle("Chat Helper");

        root = new BorderPane();
        root.getStyleClass().add("root");

        HBox toggleButtonContainer = createToggleButtonContainer();

        sidebar = createSidebar();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        
        root.setLeft(mainLayout);

        centerBorderPane = new BorderPane();
        centerBorderPane.setStyle("-fx-background-color: #111111;"); // Dark background

        centerBorderPane.setTop(toggleButtonContainer);

        messagesPanel = createMessagesPanel();
        centerBorderPane.setCenter(messagesPanel);

        HBox inputBox = createInputBox();
        centerBorderPane.setBottom(inputBox);
        
        root.setCenter(centerBorderPane);
        
        updateMessagesView();

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/View/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        playStartupAnimation(root);
    }

    private HBox createToggleButtonContainer() {
        HBox container = new HBox();
        container.setPadding(new Insets(10, 10, 0, 10));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-background-color: transparent;");

        toggleButton = createToggleButton();
        toggleButton.setOnAction(e -> toggleSidebar());
        
        container.getChildren().add(toggleButton);
        return container;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(sidebarWidth);
        sidebar.setSpacing(10);
        sidebar.setStyle("-fx-background-color: #000000;");

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(25, 10, 15, 10));

        Label title = new Label("Relatus.AI");
        title.getStyleClass().add("sidebar-title");
        title.setStyle("-fx-text-fill: #00A651; -fx-font-size: 22px; -fx-font-weight: bold;");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        headerBox.getChildren().add(title);

        // Nouvelle conversation button
        Button newConvButton = new Button("Nouvelle Conversation");
        newConvButton.setMaxWidth(Double.MAX_VALUE);
        newConvButton.setStyle("-fx-background-color: #008741; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10; -fx-background-radius: 5;");
        newConvButton.setOnAction(e -> {
            chatController.nouvelleConversation();
            updateMessagesView();
            updateCenterPanel();
        });

        // Historique des conversations
        historiqueListView = new ListView<>();
        historiqueListView.getStyleClass().add("sidebar-history");
        historiqueListView.setItems(FXCollections.observableArrayList(chatController.getHistoriqueConversations()));
        historiqueListView.setCellFactory(list -> new ConversationListCell());
        VBox.setVgrow(historiqueListView, Priority.ALWAYS);
        historiqueListView.setStyle("-fx-background-color: transparent; -fx-text-fill: #fff; -fx-border-color: #222222; -fx-border-width: 0 0 1 0;");
        
        // Quand une cnv est selectionne
        historiqueListView.setOnMouseClicked(event -> {
            Conversation selectedConversation = historiqueListView.getSelectionModel().getSelectedItem();
            if (selectedConversation != null) {
                chatController.changerConversation(selectedConversation);
                updateMessagesView();
                updateCenterPanel();
            }
        });

        //effacer lhistorique des conversations
        Button clearHistoryButton = new Button("Effacer l'historique");
        clearHistoryButton.getStyleClass().add("clear-history-button");
        clearHistoryButton.setMaxWidth(Double.MAX_VALUE);
        clearHistoryButton.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-padding: 10; -fx-background-radius: 5;");
        clearHistoryButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Effacer tout l'historique");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer toutes les conversations ? Cette action est irréversible.");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {

                    chatController.supprimerToutesConversations();
                    

                    historiqueListView.setItems(FXCollections.observableArrayList());
                    historiqueListView.setItems(FXCollections.observableArrayList(chatController.getHistoriqueConversations()));
                    updateMessagesView();
                    updateCenterPanel();
                }
            });
        });

        sidebar.getChildren().addAll(headerBox, newConvButton, historiqueListView, clearHistoryButton);
        return sidebar;
    }

    private Button createToggleButton() {
        Button button = new Button();
        button.getStyleClass().add("toggle-button");
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        

        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
            0.0, 0.0,
            10.0, 5.0,
            0.0, 10.0
        );
        arrow.setFill(Color.web("#00A651"));
        
        button.setGraphic(arrow);

        Tooltip tooltip = new Tooltip("Afficher/Masquer l'historique");
        tooltip.setStyle("-fx-font-size: 12px;");
        button.setTooltip(tooltip);
        
        return button;
    }
    
    private void toggleSidebar() {
        if (sidebarVisible) {

            TranslateTransition translate = new TranslateTransition(Duration.millis(250), sidebar);
            translate.setToX(-sidebarWidth);
            translate.setOnFinished(e -> {
                root.setLeft(null);

                Polygon arrow = (Polygon) toggleButton.getGraphic();
                arrow.setRotate(180);
            });
            translate.play();
        } else {

            sidebar.setTranslateX(-sidebarWidth);
            root.setLeft(sidebar);
            
            TranslateTransition translate = new TranslateTransition(Duration.millis(250), sidebar);
            translate.setToX(0);
            translate.play();
            

            Polygon arrow = (Polygon) toggleButton.getGraphic();
            arrow.setRotate(0);
        }
        
        sidebarVisible = !sidebarVisible;
    }

    private VBox createMessagesPanel() {
        VBox messagesPanel = new VBox();
        messagesPanel.setSpacing(10);
        messagesPanel.setPadding(new Insets(10));
        messagesPanel.setAlignment(Pos.TOP_CENTER);
        messagesPanel.setStyle("-fx-background-color: transparent;");


        messagesContainer = new VBox();
        messagesContainer.setSpacing(10);
        messagesContainer.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        messagesPanel.getChildren().addAll(scrollPane);
        return messagesPanel;
    }

    private HBox createInputBox() {
        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(15));
        inputBox.setSpacing(10);
        inputBox.setStyle("-fx-background-color: #111111; -fx-effect: dropshadow(three-pass-box, rgba(0,255,0,0.1), 10, 0, 0, -3); -fx-background-radius: 20px 20px 0 0;");

        // Zone de saisie du message
        messageInput = new TextField();
        messageInput.setPromptText("Écrivez votre question ici...");
        messageInput.setPrefHeight(40);
        messageInput.setFont(Font.font("Segoe UI", 14));
        messageInput.getStyleClass().add("message-input");
        messageInput.setStyle("-fx-background-color: #222222; -fx-text-fill: #FFFFFF; -fx-border-color: #00A651; -fx-border-radius: 20px; -fx-background-radius: 20px;");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        // Binding avec le controller
        chatController.messageActuelProperty().bindBidirectional(messageInput.textProperty());

        // Bouton résolution avec symbole checkmark (✔)
        Button resolveButton = new Button("✔");
        resolveButton.setPrefHeight(40);
        resolveButton.setPrefWidth(40);
        resolveButton.getStyleClass().add("resolve-button");
        resolveButton.setStyle("-fx-background-color: #008741; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 18px;");
        resolveButton.setOnAction(e -> {
            chatController.marquerCommeResolu(true);
            updateMessagesView();
            updateCenterPanel();
        });
        
        // Add tooltip to resolve button
        Tooltip resolveTooltip = new Tooltip("Marquer comme résolu");
        resolveTooltip.setStyle("-fx-font-size: 12px;");
        resolveButton.setTooltip(resolveTooltip);

        // Bouton d'envoi avec symbole flèche droite (→)
        Button sendButton = new Button("→");
        sendButton.setPrefHeight(40);
        sendButton.setPrefWidth(40);
        sendButton.getStyleClass().add("send-button");
        sendButton.setStyle("-fx-background-color: #00A651; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 18px;");
        sendButton.setOnAction(e -> sendMessage());
        
        // Add tooltip to send button
        Tooltip sendTooltip = new Tooltip("Envoyer le message");
        sendTooltip.setStyle("-fx-font-size: 12px;");
        sendButton.setTooltip(sendTooltip);

        // Envoyer le message avec la touche Entrée
        messageInput.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(messageInput, resolveButton, sendButton);
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
                    textBox.getStyleClass().add("message-bubble-user");
                    textBox.setStyle("-fx-background-color: #00A651; -fx-background-radius: 15 0 15 15; -fx-padding: 10; -fx-text-fill: #000000;");

                    Text text = new Text(message.getContent());
                    text.setFill(Color.web("#000000"));
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.web("#444444"));
                    timestamp.setStyle("-fx-font-size: 10;");

                    textBox.getChildren().addAll(text, timestamp);
                    messageBox.getChildren().add(textBox);
                } else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);
 
                    VBox textBox = new VBox();
                    textBox.getStyleClass().add("message-bubble-bot");
                    textBox.setStyle("-fx-background-color: #111111; -fx-background-radius: 0 15 15 15; -fx-padding: 10; -fx-text-fill: #FFFFFF; -fx-border-color: #00A651; -fx-border-width: 1;");

                    Text text = new Text(message.getContent());
                    text.setFill(Color.web("#FFFFFF"));
                    text.setWrappingWidth(400);

                    Label timestamp = new Label(message.getFormattedTimestamp());
                    timestamp.setTextFill(Color.web("#888888"));
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

            // Show feedback section if conversation is resolved
            Conversation conv = chatController.getConversationActuelle();
            if (conv != null && conv.isResolved()) {
                VBox feedbackSection = createFeedbackSection();
                messagesContainer.getChildren().add(feedbackSection);
                
                // Replace the input box with a "New Conversation" button at the bottom
                Button newConversationBtn = new Button("Nouvelle Conversation");
                newConversationBtn.setPrefHeight(40);
                newConversationBtn.setMaxWidth(Double.MAX_VALUE);
                newConversationBtn.setStyle("-fx-background-color: #00A651; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand;");
                newConversationBtn.setOnAction(e -> {
                    chatController.nouvelleConversation();
                    updateMessagesView();
                    updateCenterPanel();
                });
                
                HBox bottomBox = new HBox(newConversationBtn);
                bottomBox.setPadding(new Insets(15));
                bottomBox.setAlignment(Pos.CENTER);
                bottomBox.setStyle("-fx-background-color: #111111;");
                HBox.setHgrow(newConversationBtn, Priority.ALWAYS);
                
                centerBorderPane.setBottom(bottomBox);
            } else {
                // Make sure the input box is shown for unresolved conversations
                centerBorderPane.setBottom(createInputBox());
            }

            // Faire défiler vers le bas
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        }
    }
    
    private VBox createFeedbackSection() {
        VBox feedbackSection = new VBox(15);
        feedbackSection.setAlignment(Pos.CENTER);
        feedbackSection.setPadding(new Insets(20));
        feedbackSection.setStyle("-fx-background-color: #111111; -fx-background-radius: 15; -fx-border-color: #00A651; -fx-border-width: 1; -fx-border-radius: 15;");
        
        Label feedbackTitle = new Label("Comment évaluez-vous la qualité de l'assistance?");
        feedbackTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        feedbackTitle.setTextFill(Color.web("#FFFFFF"));
        
        // Using ControlsFX Rating component
        Rating ratingControl = new Rating(5);
        ratingControl.setPartialRating(false);
        ratingControl.setUpdateOnHover(true);
        ratingControl.setStyle("-fx-background-color: transparent;");
        
        HBox emojiContainer = new HBox(30);
        emojiContainer.setAlignment(Pos.CENTER);
        
        Label badEmoji = new Label("😞");
        badEmoji.setStyle("-fx-font-size: 24px;");
        
        Label neutralEmoji = new Label("😐");
        neutralEmoji.setStyle("-fx-font-size: 24px;");
        
        Label goodEmoji = new Label("😊");
        goodEmoji.setStyle("-fx-font-size: 24px;");
        
        emojiContainer.getChildren().addAll(badEmoji, neutralEmoji, goodEmoji);
        
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Commentaires ou suggestions additionnels (optionnel)");
        commentInput.setPrefRowCount(3);
        commentInput.setWrapText(true);
        commentInput.setStyle("-fx-background-color: #222222; -fx-text-fill: #000000; -fx-background-radius: 10;");
        
        Button submitFeedback = new Button("★");
        submitFeedback.setPrefHeight(40);
        submitFeedback.setPrefWidth(40);
        submitFeedback.setStyle("-fx-background-color: #00A651; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 18px;");
        submitFeedback.setOnAction(e -> {
            int rating = (int) ratingControl.getRating();
            chatController.ajouterFeedback(rating);
            
            // Show confirmation
            feedbackSection.getChildren().clear();
            Label thankYou = new Label("Merci pour votre évaluation!");
            thankYou.setFont(Font.font("System", FontWeight.BOLD, 18));
            thankYou.setTextFill(Color.web("#FFFFFF"));
            feedbackSection.getChildren().add(thankYou);
            
            // Animation
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), thankYou);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });
        
        // Add tooltip for feedback button
        Tooltip submitTooltip = new Tooltip("Soumettre l'évaluation");
        submitTooltip.setStyle("-fx-font-size: 12px;");
        submitFeedback.setTooltip(submitTooltip);
        
        feedbackSection.getChildren().addAll(feedbackTitle, ratingControl, emojiContainer, commentInput, submitFeedback);
        
        // Add an entrance animation
        feedbackSection.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), feedbackSection);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        return feedbackSection;
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
                statusCircle.setFill(conversation.isResolved() ? Color.web("#00A651") : Color.ORANGE);

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
        // Clear the list before setting new items to ensure complete refresh
        historiqueListView.getItems().clear();
        historiqueListView.setItems(FXCollections.observableArrayList(chatController.getHistoriqueConversations()));
    }

    private void updateCenterPanel() {
        // Make sure the BorderPane is properly updated with the current conversation state
        Conversation conv = chatController.getConversationActuelle();
        updateHistoriqueListView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
