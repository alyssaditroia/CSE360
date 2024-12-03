
package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Conversation;
import models.HelpMessage;
/**
 * The {@code HelpMessageDatabase} stores student help messages for sending and receiving messages between students and instructors.
 * Extends the original database class
 */

//$EXCLUDE$

public class HelpMessageDatabase extends Database {

    /**
     * Database instance for interacting with core database functionality
     */
    private Database db;
    
    /**
     * Database connection instance
     */
    private Connection connection;

    /**
     * Constructor for HelpMessageDatabase
     * Initializes database connection and creates necessary tables
     * 
     * @throws Exception if database connection or table creation fails
     */
    public HelpMessageDatabase() throws Exception {
        System.out.println("[HelpMessageDB] Help Message Database Initializing");
        // Initialize the Database instance and connection
        db = Database.getInstance(); 
        connection = db.getConnection(); // Get the shared connection
        if (connection != null) {
            System.out.println("[HelpMessageDB] Connection established successfully in HelpMessageDatabase.");
        } else {
            System.out.println("[HelpMessageDB] Error connection is null in HelpMessageDatabase.");
        }
        createHelpTables();
    }

    /**
     * Creates the necessary tables for the help system if they don't exist
     * 
     * @throws SQLException if table creation fails
     */
    private void createHelpTables() throws SQLException {
        // Create help_messages table with all fields from HelpMessage class
        String messageTable = "CREATE TABLE IF NOT EXISTS help_messages ("
            + "message_id INT AUTO_INCREMENT PRIMARY KEY, "
            + "message_body TEXT NOT NULL, "
            + "is_specific BOOLEAN DEFAULT FALSE, "
            + "user_id INT NOT NULL, "
            + "search_requests TEXT, "  // Store as comma-separated string
            + "FOREIGN KEY (user_id) REFERENCES cse360users(id))";

        // Create conversation_messages junction table
        String conversationMessagesTable = "CREATE TABLE IF NOT EXISTS conversation_messages ("
            + "conversation_id INT NOT NULL, "
            + "message_id INT NOT NULL, "
            + "creator_id INT, "
            + "PRIMARY KEY (conversation_id, message_id), "
            + "FOREIGN KEY (message_id) REFERENCES help_messages(message_id))";

        try (Statement statement = connection.createStatement()) {
            statement.execute(messageTable);
            statement.execute(conversationMessagesTable);
            System.out.println("[HelpMessageDB] Help system tables created successfully");
        } catch (SQLException e) {
            System.out.println("[HelpMessageDB] Error creating help system tables: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves a new help message to the database
     * 
     * @param message The HelpMessage to save
     * @return the generated message ID, or -1 if save fails
     * @throws SQLException if database operation fails
     */
    public int saveHelpMessage(HelpMessage message) throws SQLException {
        String sql = "INSERT INTO help_messages (message_body, is_specific, user_id, search_requests) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, message.getMessageBody());
            pstmt.setBoolean(2, message.getIsSpecificMessage());
            pstmt.setInt(3, message.getUserId());
            String searchRequests = message.getSearchRequests().isEmpty() ? null : 
                                  String.join(",", message.getSearchRequests());
            pstmt.setString(4, searchRequests);
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Links a message to a conversation
     * 
     * @param conversationId ID of the conversation
     * @param messageId ID of the message to add
     * @throws SQLException if database operation fails
     */
    public void addMessageToConversation(int conversationId, int messageId) throws SQLException {
        String sql = "INSERT INTO conversation_messages (conversation_id, message_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, conversationId);
            pstmt.setInt(2, messageId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Adds multiple messages to a conversation
     * 
     * @param conversationId ID of the conversation
     * @param messageIds List of message IDs to add
     * @throws SQLException if database operation fails
     */
    public void addMessagesToConversation(int conversationId, List<Integer> messageIds) throws SQLException {
        String sql = "INSERT INTO conversation_messages (conversation_id, message_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int messageId : messageIds) {
                pstmt.setInt(1, conversationId);
                pstmt.setInt(2, messageId);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves all message IDs for a conversation
     * 
     * @param conversationId ID of the conversation
     * @return List of message IDs
     * @throws SQLException if database operation fails
     */
    public List<Integer> getConversationMessageIds(int conversationId) throws SQLException {
        List<Integer> messageIds = new ArrayList<>();
        String sql = "SELECT message_id FROM conversation_messages WHERE conversation_id = ? ORDER BY message_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                messageIds.add(rs.getInt("message_id"));
            }
        }
        return messageIds;
    }

    /**
     * Retrieves a help message by its ID
     * 
     * @param messageId ID of the message to retrieve
     * @return HelpMessage object, or null if not found
     * @throws SQLException if database operation fails
     */
    public HelpMessage getHelpMessage(int messageId) throws SQLException {
        String sql = "SELECT * FROM help_messages WHERE message_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, messageId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                HelpMessage message = new HelpMessage(
                    rs.getString("message_body"),
                    rs.getBoolean("is_specific"),
                    rs.getInt("user_id")
                );
                message.setMessageId(messageId);
                
                String searchRequests = rs.getString("search_requests");
                if (searchRequests != null && !searchRequests.isEmpty()) {
                    for (String search : searchRequests.split(",")) {
                        message.getSearchRequests().add(search);
                    }
                }
                return message;
            }
        }
        return null;
    }

    /**
     * Retrieves all messages for a conversation
     * 
     * @param conversationId ID of the conversation
     * @return List of HelpMessage objects
     * @throws SQLException if database operation fails
     */
    public List<HelpMessage> getConversationMessages(int conversationId) throws SQLException {
        List<HelpMessage> messages = new ArrayList<>();
        String sql = "SELECT m.* FROM help_messages m " +
                    "JOIN conversation_messages cm ON m.message_id = cm.message_id " +
                    "WHERE cm.conversation_id = ? " +
                    "ORDER BY m.message_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HelpMessage message = new HelpMessage(
                    rs.getString("message_body"),
                    rs.getBoolean("is_specific"),
                    rs.getInt("user_id")
                );
                message.setMessageId(rs.getInt("message_id"));
                
                String searchRequests = rs.getString("search_requests");
                if (searchRequests != null && !searchRequests.isEmpty()) {
                    for (String search : searchRequests.split(",")) {
                        message.getSearchRequests().add(search);
                    }
                }
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Creates a Conversation object with all its messages from the database
     * 
     * @param conversationId ID of the conversation to load
     * @return Conversation object with all associated messages
     * @throws SQLException if database operation fails
     */
    public Conversation loadConversation(int conversationId) throws SQLException {
        Conversation conversation = new Conversation(conversationId);
        List<HelpMessage> messages = getConversationMessages(conversationId);
        for (HelpMessage message : messages) {
            conversation.addMessage(message);
        }
        return conversation;
    }

    /**
     * Deletes a conversation and all its associated messages
     * 
     * @param conversationId ID of the conversation to delete
     * @throws SQLException if database operation fails
     */
    public void deleteConversation(int conversationId) throws SQLException {
        // First get all message IDs
        List<Integer> messageIds = getConversationMessageIds(conversationId);
        
        // Delete from conversation_messages
        String deleteConvMessages = "DELETE FROM conversation_messages WHERE conversation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteConvMessages)) {
            pstmt.setInt(1, conversationId);
            pstmt.executeUpdate();
        }
        
        // Delete the messages
        if (!messageIds.isEmpty()) {
            String deleteMessages = "DELETE FROM help_messages WHERE message_id IN ("
                + String.join(",", messageIds.stream().map(String::valueOf).toArray(String[]::new))
                + ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(deleteMessages);
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Get a new connection from the parent database
                connection = db.getConnection();
            }
        } catch (SQLException e) {
            System.out.println("[HelpMessageDB] Error checking database connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}