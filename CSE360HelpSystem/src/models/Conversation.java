package models;

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
        System.out.println("[Conversation] New conversation created with ID: " + id);
    }

    /**
     * Gets the conversation's unique identifier
     * @return the conversation ID
     */
    public int getConversationId() {
        System.out.println("[Conversation] Getting conversationId: " + conversationId);
        return conversationId;
    }

    /**
     * Sets the conversation's unique identifier
     * @param conversationId the ID to set
     */
    public void setConversationId(int conversationId) {
        System.out.println("[Conversation] Setting conversationId to: " + conversationId);
        this.conversationId = conversationId;
    }

    /**
     * Checks if the conversation is resolved
     * @return true if resolved, false if still active
     */
    public Boolean getIsResolved() {
        System.out.println("[Conversation] Getting isResolved: " + isResolved);
        return isResolved;
    }

    /**
     * Sets the resolution status of the conversation
     * @param isResolved the resolution status to set
     */
    public void setIsResolved(Boolean isResolved) {
        System.out.println("[Conversation] Setting isResolved to: " + isResolved);
        this.isResolved = isResolved;
    }

    /**
     * Gets all messages in the conversation
     * @return List of HelpMessage objects
     */
    public List<HelpMessage> getMessages() {
        System.out.println("[Conversation] Getting messages. Current size: " + messages.size());
        return messages;
    }

    /**
     * Sets the list of messages for the conversation
     * @param messages List of HelpMessage objects
     */
    public void setMessages(List<HelpMessage> messages) {
        System.out.println("[Conversation] Setting messages. New size: " + messages.size());
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
        System.out.println("[Conversation] Added message: " + message.getMessageBody() +
                ". Total messages: " + messages.size());
    }

    /**
     * Removes a specific message from the conversation
     * @param message The HelpMessage to remove
     * @return true if message was removed, false if not found
     */
    public boolean removeMessage(HelpMessage message) {
        boolean removed = this.messages.remove(message);
        System.out.println("[Conversation] Attempted to remove message: " + message.getMessageBody() +
                ". Removed: " + removed + ". Total messages: " + messages.size());
        return removed;
    }

    /**
     * Gets the most recent message in the conversation
     * @return The last HelpMessage in the list, or null if no messages exist
     */
    public HelpMessage getLatestMessage() {
        if (messages.isEmpty()) {
            System.out.println("[Conversation] No messages to retrieve as latest.");
            return null;
        }
        HelpMessage latestMessage = messages.get(messages.size() - 1);
        System.out.println("[Conversation] Getting latest message: " + latestMessage.getMessageBody());
        return latestMessage;
    }

    /**
     * Gets the number of messages in the conversation
     * @return The total number of messages
     */
    public int getMessageCount() {
        System.out.println("[Conversation] Getting message count: " + messages.size());
        return messages.size();
    }

    /**
     * Resolves the conversation
     */
    public void resolveConversation() {
        this.isResolved = true;
        System.out.println("[Conversation] Conversation marked as resolved.");
    }

    /**
     * Reopens a resolved conversation
     */
    public void reopenConversation() {
        this.isResolved = false;
        System.out.println("[Conversation] Conversation reopened.");
    }

    /**
     * Clears all messages from the conversation
     */
    public void clearMessages() {
        this.messages.clear();
        System.out.println("[Conversation] All messages cleared. Total messages: " + messages.size());
    }
}
