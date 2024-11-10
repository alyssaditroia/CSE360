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
        Button resolveBtn = new Button("Mark as Resolved");
        resolveBtn.setOnAction(e -> resolveCurrentConversation());
        resolveBtn.getStyleClass().add("action-button");
        VBox.setMargin(resolveBtn, new javafx.geometry.Insets(10));
        chatArea.getChildren().add(0, resolveBtn);
    }
    
    private void setupAdminView() {
        messageTypeComboBox.setVisible(false);
        Button deleteBtn = new Button("Delete Conversation");
        deleteBtn.setOnAction(e -> deleteCurrentConversation());
        deleteBtn.getStyleClass().add("action-button");
        VBox.setMargin(deleteBtn, new javafx.geometry.Insets(10));
        chatArea.getChildren().add(0, deleteBtn);
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
        
        try {
            List<HelpMessage> messages = messageDB.getConversationMessages(conversation.getConversationId());
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
        
        // Add ALL style classes explicitly
        bubble.getStyleClass().addAll("message-bubble");
        
        boolean isOwnMessage = message.getUserId() == userSession.getCurrentUser().getId();
        System.out.println("Creating bubble for message from user: " + message.getUserId());
        System.out.println("Current user: " + userSession.getCurrentUser().getId());
        System.out.println("Is own message: " + isOwnMessage);
        
        // Set alignment and add style class based on ownership
        if (isOwnMessage) {
            bubble.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            bubble.getStyleClass().add("own-message");
            System.out.println("Added own-message style class");
        } else {
            bubble.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            // Determine sender role and add appropriate style
            try {
                if (db != null) {
                    String userId = String.valueOf(message.getUserId());
                    if (db.isUserInstructor(userId)) {
                        bubble.getStyleClass().add("instructor-message");
                        System.out.println("Added instructor-message style class");
                    } else if (db.isUserStudent(userId)) {
                        bubble.getStyleClass().add("student-message");
                        System.out.println("Added student-message style class");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Add sender label
        String senderRole;
        try {
            if (db != null) {
                String userId = String.valueOf(message.getUserId());
                if (db.isUserInstructor(userId)) {
                    senderRole = "Instructor";
                } else if (db.isUserStudent(userId)) {
                    senderRole = "Student";
                } else {
                    senderRole = "Unknown Role";
                }
            } else {
                senderRole = "Unknown Role";
            }
        } catch (SQLException e) {
            senderRole = "Error Getting Role";
            e.printStackTrace();
        }
        
        Label senderLabel = new Label(senderRole);
        senderLabel.getStyleClass().add("sender-label");
        bubble.getChildren().add(senderLabel);
        
        // Add message content
        Label contentLabel = new Label(message.getMessageBody());
        contentLabel.getStyleClass().add("content-label");
        contentLabel.setWrapText(true);
        bubble.getChildren().add(contentLabel);
        
        // Add specific help request information if applicable
        if (message.getIsSpecificMessage()) {
            Label typeLabel = new Label("Specific Help Request");
            typeLabel.getStyleClass().add("message-type");
            bubble.getChildren().add(typeLabel);
            
            if (!message.getSearchRequests().isEmpty()) {
                Label searchLabel = new Label("Search History: " + 
                    String.join(", ", message.getSearchRequests()));
                searchLabel.getStyleClass().add("search-history");
                bubble.getChildren().add(searchLabel);
            }
        }

        // Print all style classes for debugging
        System.out.println("Final style classes for bubble: " + bubble.getStyleClass());
        
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