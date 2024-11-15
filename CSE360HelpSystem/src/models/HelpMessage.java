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
	 * Generic 	- used to request help about how the tool works and how to use it
	 * Specific	- used to express the help information (articles) they are searching for can not be found
	 * 			- search list from user used in this case
	 */
	private Boolean isSpecificMessage;
	
	/**
	 * The searches made by the user, used if the user is sending a specific message
	 */
	private ArrayList<String> searchRequests;
	
	/**
     * The unique identifier of the user who sent this help message
     * References the id field in the cse360users table
     */
    private int userId;
	
	 // Constructor
    public HelpMessage(String messageBody, Boolean isSpecificMessage, int userId) {
        this.messageBody = messageBody;
        this.isSpecificMessage = isSpecificMessage;
        this.searchRequests = new ArrayList<>();
        this.userId = userId;
    }

    // Getters and Setters
    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Boolean getIsSpecificMessage() {
        return isSpecificMessage;
    }

    public void setIsSpecificMessage(Boolean isSpecificMessage) {
        this.isSpecificMessage = isSpecificMessage;
    }

    public ArrayList<String> getSearchRequests() {
        return searchRequests;
    }

    public void setSearchRequests(ArrayList<String> searchRequests) {
        this.searchRequests = searchRequests;
    }
    
    /**
     * Gets the ID of the user who sent this help message
     * @return the user's ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who sent this help message
     * @param userId the user's ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    // Add messageId getter/setter
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
