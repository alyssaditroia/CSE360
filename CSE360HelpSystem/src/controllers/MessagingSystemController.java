package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;
import database.HelpMessageDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessagingSystemController extends PageController {
    @FXML private ListView<Conversation> conversationListView;
    @FXML private VBox chatArea;
    @FXML private TextArea messageInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane messageScroll;
    @FXML private VBox messageContainer;
    @FXML private Label currentUserLabel;
    @FXML private ComboBox<String> messageTypeComboBox;
    @FXML private ListView<String> searchHistoryList;
    
    private HelpMessageDatabase messageDB;
    private UserSession userSession;
    private ObservableList<Conversation> conversations;

    @Override
    public void initialize(javafx.stage.Stage stage, database.Database db) {
        super.initialize(stage, db);
        try {
            userSession = UserSession.getInstance();
            conversations = FXCollections.observableArrayList();
            messageDB = new HelpMessageDatabase();
            
            setupUI();
            // Only load conversations if we have a user session
            if (userSession.getCurrentUser() != null) {
                loadConversations();
            }
            setupEventHandlers();
            configureForUserRole();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error initializing messaging system");
        }
    }
    
    private void setupUI() {
        conversationListView.setItems(conversations);
        conversationListView.setCellFactory(lv -> new ConversationListCell());
        
        if (userSession.getCurrentRole().equals("student")) {
            messageTypeComboBox.setItems(FXCollections.observableArrayList(
                "Generic - Help using the tool",
                "Specific - Can't find information"
            ));
            messageTypeComboBox.getSelectionModel().selectFirst();
        } else {
            messageTypeComboBox.setVisible(false);
        }
        
        currentUserLabel.setText("Logged in as: " + userSession.getUsername() + 
                               " (" + userSession.getCurrentRole() + ")");
    }
    
    private void loadConversations() {
        if (messageDB == null) return;
        
        try {
            Connection conn = messageDB.getConnection();
            if (conn == null) return;
                
            String sql;
            List<Conversation> userConversations = new ArrayList<>();

            if (userSession.getCurrentRole().equals("student")) {
                sql = "SELECT DISTINCT conversation_id FROM conversation_messages " +
                      "WHERE creator_id = ? " +
                      "ORDER BY conversation_id";
                            
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userSession.getCurrentUser().getId());
                    ResultSet rs = pstmt.executeQuery();
                    
                    while (rs.next()) {
                        int conversationId = rs.getInt("conversation_id");
                        Conversation conversation = messageDB.loadConversation(conversationId);
                        if (conversation != null) {
                            userConversations.add(conversation);
                        }
                    }
                }
            } else {
                // Instructors and admins see all conversations
                sql = "SELECT DISTINCT conversation_id FROM conversation_messages ORDER BY conversation_id";
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    while (rs.next()) {
                        int conversationId = rs.getInt("conversation_id");
                        Conversation conversation = messageDB.loadConversation(conversationId);
                        if (conversation != null) {
                            userConversations.add(conversation);
                        }
                    }
                }
            }
            
            conversations.setAll(userConversations);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading conversations");
        }
    }
    
    private void setupEventHandlers() {
        conversationListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    displayConversation(newVal);
                }
            }
        );
        
        sendButton.setOnAction(e -> sendMessage());
        
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER") && !event.isShiftDown()) {
                sendMessage();
                event.consume();
            }
        });
    }
    
    private void configureForUserRole() {
        String role = userSession.getCurrentRole();
        switch (role) {
            case "student":
                setupStudentView();
                break;
            case "instructor":
                setupInstructorView();
                break;
            case "admin":
                setupAdminView();
                break;
        }
    }
    
    private void setupStudentView() {
        Button newConversationBtn = new Button("New Help Request");
        newConversationBtn.setOnAction(e -> startNewConversation());
        newConversationBtn.getStyleClass().add("action-button");
        VBox.setMargin(newConversationBtn, new javafx.geometry.Insets(10));
        chatArea.getChildren().add(0, newConversationBtn);
        messageTypeComboBox.setVisible(true);
    }
    
    private void setupInstructorView() {
        messageTypeComboBox.setVisible(false);
    }
    
    private void setupAdminView() {
        messageTypeComboBox.setVisible(false);
        /*
        Button deleteBtn = new Button("Delete Conversation");
        deleteBtn.setOnAction(e -> deleteCurrentConversation());
        deleteBtn.getStyleClass().add("action-button");
        VBox.setMargin(deleteBtn, new javafx.geometry.Insets(10));
        chatArea.getChildren().add(0, deleteBtn);
        */
    }
    
 // In MessagingSystemController.java, modify the sendMessage() method:
    private void sendMessage() {
        if (messageInput.getText().trim().isEmpty()) return;
        
        Conversation selectedConversation = conversationListView.getSelectionModel().getSelectedItem();
        
        try {
            // For new conversations
            if (selectedConversation == null && userSession.getCurrentRole().equals("student")) {
                startNewConversation();
                selectedConversation = conversationListView.getSelectionModel().getSelectedItem();
            }
            
            if (selectedConversation != null) {
                // Create and save message
                HelpMessage message = new HelpMessage(
                    messageInput.getText().trim(),
                    messageTypeComboBox.isVisible() && 
                    messageTypeComboBox.getSelectionModel().getSelectedIndex() == 1,
                    userSession.getCurrentUser().getId()
                );
                
                if (message.getIsSpecificMessage() && userSession.getUserSearches() != null) {
                    message.setSearchRequests(userSession.getUserSearches());
                }
                
                // Save message and get its ID
                int messageId = messageDB.saveHelpMessage(message);
                
                // Check if this is the first message in the conversation
                String checkSql = "SELECT COUNT(*) FROM conversation_messages WHERE conversation_id = ?";
                boolean isFirstMessage = true;
                try (PreparedStatement checkStmt = messageDB.getConnection().prepareStatement(checkSql)) {
                    checkStmt.setInt(1, selectedConversation.getConversationId());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        isFirstMessage = rs.getInt(1) == 0;
                    }
                }

                // Add creator_id if it's the first message and from a student
                if (isFirstMessage && userSession.getCurrentRole().equals("student")) {
                    String sql = "INSERT INTO conversation_messages (conversation_id, message_id, creator_id) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = messageDB.getConnection().prepareStatement(sql)) {
                        pstmt.setInt(1, selectedConversation.getConversationId());
                        pstmt.setInt(2, messageId);
                        pstmt.setInt(3, userSession.getCurrentUser().getId());
                        pstmt.executeUpdate();
                    }
                } else {
                    messageDB.addMessageToConversation(selectedConversation.getConversationId(), messageId);
                }
                
                // Update UI
                displayConversation(selectedConversation);
                messageInput.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error sending message");
        }
    }
    
    private void startNewConversation() {
        try {
            // Get max conversation ID from conversation_messages or start at 1
            String getMaxId = "SELECT MAX(conversation_id) as max_id FROM conversation_messages";
            int nextId = 1;
            
            // Use messageDB's connection directly instead of creating a new one
            try (Statement stmt = messageDB.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(getMaxId)) {
                if (rs.next() && rs.getObject("max_id") != null) {
                    nextId = rs.getInt("max_id") + 1;
                }
            }
            
            // Create conversation object with new ID
            Conversation newConversation = new Conversation(nextId);
            conversations.add(newConversation);
            conversationListView.getSelectionModel().select(newConversation);
            messageInput.requestFocus();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error creating new conversation");
        }
    }

    
    private void displayConversation(Conversation conversation) {
        messageContainer.getChildren().clear();
        searchHistoryList.getItems().clear();
        
        try {
            List<HelpMessage> messages = messageDB.getConversationMessages(conversation.getConversationId());
            
            // Add conversation type label at top if there are messages
            if (!messages.isEmpty()) {
                HelpMessage firstMessage = messages.get(0);
                Label typeLabel = new Label(firstMessage.getIsSpecificMessage() ? 
                    "Specific Help Request Conversation" : "General Help Request Conversation");
                typeLabel.getStyleClass().add("user-label");
                messageContainer.getChildren().add(typeLabel);
                
                // Add searches only from first message if specific
                if (firstMessage.getIsSpecificMessage() && !firstMessage.getSearchRequests().isEmpty()) {
                    searchHistoryList.getItems().addAll(firstMessage.getSearchRequests());
                }
            }

            // Add all messages
            for (HelpMessage message : messages) {
                VBox messageBubble = createMessageBubble(message);
                messageContainer.getChildren().add(messageBubble);
            }
            
            messageScroll.setVvalue(1.0);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading messages");
        }
    }
    
    private VBox createMessageBubble(HelpMessage message) {
        VBox bubble = new VBox(5);
        bubble.getStyleClass().addAll("message-bubble");
        
        boolean isOwnMessage = message.getUserId() == userSession.getCurrentUser().getId();
        
        // Set alignment based on message ownership
        if (isOwnMessage) {
            bubble.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            bubble.getStyleClass().add("own-message");
        } else {
            bubble.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            if (db != null) {
                try {
                    String userId = String.valueOf(message.getUserId());
                    if (db.isUserInstructor(userId)) {
                        bubble.getStyleClass().add("instructor-message");
                    } else if (db.isUserStudent(userId)) {
                        bubble.getStyleClass().add("student-message");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Create username label
        try {
            // Query the database to get the username for this message's user ID
            String query = "SELECT username FROM cse360users WHERE id = ?";
            String username = "Unknown User";
            
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, message.getUserId());
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
            
            Label senderLabel = new Label(username);
            senderLabel.getStyleClass().add("sender-label");
            bubble.getChildren().add(senderLabel);
            
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error retrieving user info");
            errorLabel.getStyleClass().add("sender-label");
            bubble.getChildren().add(errorLabel);
        }
        
        // Add message content
        Label contentLabel = new Label(message.getMessageBody());
        contentLabel.getStyleClass().add("content-label");
        contentLabel.setWrapText(true);
        bubble.getChildren().add(contentLabel);
        
        return bubble;
    }
    
    private void resolveCurrentConversation() {
        Conversation selected = conversationListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.resolveConversation();
            loadConversations();
        }
    }
    
    private void deleteCurrentConversation() {
        Conversation selected = conversationListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                messageDB.deleteConversation(selected.getConversationId());
                loadConversations();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting conversation");
            }
        }
    }
}