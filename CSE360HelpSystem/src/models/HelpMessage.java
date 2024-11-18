package models;

import java.util.ArrayList;

/**
 * The {@Code HelpMessage} class is the Model for student Help Messages
 */
public class HelpMessage {
    /**
     * Unique identifier for the help message
     */
    private int messageId;

    /**
     * Contains the actual message requesting help that the user sends to the system
     */
    private String messageBody;

    /**
     * Flag variable controls if the help message is a generic or specific message
     * 
     * Generic - used to request help about how the tool works and how to use it
     * Specific - used to express the help information (articles) they are searching
     * for can not be found
     * - search list from user used in this case
     */
    private Boolean isSpecificMessage;

    /**
     * The searches made by the user, used if the user is sending a specific message
     */
    private ArrayList<String> searchRequests;

    /**
     * The unique identifier of the user who sent this help message References the id
     * field in the cse360users table
     */
    private int userId;

    // Constructor
    public HelpMessage(String messageBody, Boolean isSpecificMessage, int userId) {
        this.messageBody = messageBody;
        this.isSpecificMessage = isSpecificMessage;
        this.searchRequests = new ArrayList<>();
        this.userId = userId;
        System.out.println("[HelpMessage] Constructor called");
    }

    // Getters and Setters
    public String getMessageBody() {
        System.out.println("[HelpMessage] Getting messageBody: " + messageBody);
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        System.out.println("[HelpMessage] Setting messageBody: " + messageBody);
        this.messageBody = messageBody;
    }

    public Boolean getIsSpecificMessage() {
        System.out.println("[HelpMessage] Getting isSpecificMessage: " + isSpecificMessage);
        return isSpecificMessage;
    }

    public void setIsSpecificMessage(Boolean isSpecificMessage) {
        System.out.println("[HelpMessage] Setting isSpecificMessage: " + isSpecificMessage);
        this.isSpecificMessage = isSpecificMessage;
    }

    public ArrayList<String> getSearchRequests() {
        System.out.println("[HelpMessage] Getting searchRequests: " + searchRequests);
        return searchRequests;
    }

    public void setSearchRequests(ArrayList<String> searchRequests) {
        System.out.println("[HelpMessage] Setting searchRequests: " + searchRequests);
        this.searchRequests = searchRequests;
    }

    public int getUserId() {
        System.out.println("[HelpMessage] Getting userId: " + userId);
        return userId;
    }

    public void setUserId(int userId) {
        System.out.println("[HelpMessage] Setting userId: " + userId);
        this.userId = userId;
    }

    public int getMessageId() {
        System.out.println("[HelpMessage] Getting messageId: " + messageId);
        return messageId;
    }

    public void setMessageId(int messageId) {
        System.out.println("[HelpMessage] Setting messageId: " + messageId);
        this.messageId = messageId;
    }
}

