package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * The {@code Conversation} class is the model for the conversation structure
 * This is an object that defines a conversation
 */
public class Conversation {
    private int conversationId;
    private Boolean isResolved;
    private List<HelpMessage> messages;

    // Constructor for new conversations
    public Conversation(int id) {
    	this.conversationId = id;
    	this.isResolved = false;
        this.messages = new ArrayList<>();
    }
    
    /**
     * Gets the conversation's unique identifier
     * @return the conversation ID
     */
    public int getConversationId() {
        return conversationId;
    }

    /**
     * Sets the conversation's unique identifier
     * @param conversationId the ID to set
     */
    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Checks if the conversation is resolved
     * @return true if resolved, false if still active
     */
    public Boolean getIsResolved() {
        return isResolved;
    }

    /**
     * Sets the resolution status of the conversation
     * @param isResolved the resolution status to set
     */
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    /**
     * Gets all messages in the conversation
     * @return List of HelpMessage objects
     */
    public List<HelpMessage> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages for the conversation
     * @param messages List of HelpMessage objects
     */
    public void setMessages(List<HelpMessage> messages) {
        this.messages = messages;
    }

    /**
     * Adds a single message to the conversation
     * @param message The HelpMessage to add
     */
    public void addMessage(HelpMessage message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
    }

    /**
     * Removes a specific message from the conversation
     * @param message The HelpMessage to remove
     * @return true if message was removed, false if not found
     */
    public boolean removeMessage(HelpMessage message) {
        return this.messages.remove(message);
    }

    /**
     * Gets the most recent message in the conversation
     * @return The last HelpMessage in the list, or null if no messages exist
     */
    public HelpMessage getLatestMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    /**
     * Gets the number of messages in the conversation
     * @return The total number of messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Resolves the conversation
     */
    public void resolveConversation() {
        this.isResolved = true;
    }

    /**
     * Reopens a resolved conversation
     */
    public void reopenConversation() {
        this.isResolved = false;
    }

    /**
     * Clears all messages from the conversation
     */
    public void clearMessages() {
        this.messages.clear();
    }
}