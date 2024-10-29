package models;

import java.time.LocalDateTime;
/**
 * The {@code Feedback} class handles user feedback in program memory without interacting with the database
 */
public class Feedback {
	/**
     * The unique identifier for the feedback entry.
     */
    private String feedbackID;
    
    /**
     * The user who provided the feedback.
     */
    private User user;
    
    /**
     * The article that the feedback is associated with.
     */
    private Article article;
    
    /**
     * The feedback message content provided by the user.
     */
    private String message;
    
    /**
     * The date and time when the feedback was created.
     */
    private LocalDateTime createdDate;

    /**
     * Constructs a new Feedback object with the specified parameters.
     * Sets the creation date to the current time.
     *
     * @param feedbackID The unique identifier for this feedback
     * @param user The user providing the feedback
     * @param article The article receiving the feedback
     * @param message The feedback message content
     */
    public Feedback(String feedbackID, User user, Article article, String message) {
        this.setFeedbackID(feedbackID);
        this.setUser(user);
        this.setArticle(article);
        this.setMessage(message);
        this.setCreatedDate(LocalDateTime.now());
    }

    /**
     * Gets the unique identifier of this feedback.
     *
     * @return The feedback's unique identifier
     */
	public String getFeedbackID() {
		return feedbackID;
	}

	/**
     * Sets the unique identifier for this feedback.
     *
     * @param feedbackID The unique identifier to set
     */
	public void setFeedbackID(String feedbackID) {
		this.feedbackID = feedbackID;
	}

	/**
     * Gets the user who provided this feedback.
     *
     * @return The user who created the feedback
     */
	public User getUser() {
		return user;
	}

	 /**
     * Sets the user associated with this feedback.
     *
     * @param user The user to associate with this feedback
     */
	public void setUser(User user) {
		this.user = user;
	}

	/**
     * Gets the article associated with this feedback.
     *
     * @return The article this feedback is about
     */
	public Article getArticle() {
		return article;
	}

	/**
     * Sets the article associated with this feedback.
     *
     * @param article The article to associate with this feedback
     */
	public void setArticle(Article article) {
		this.article = article;
	}

	/**
     * Gets the feedback message content.
     *
     * @return The feedback message
     */
	public String getMessage() {
		return message;
	}

	/**
     * Sets the feedback message content.
     *
     * @param message The feedback message to set
     */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
     * Gets the date and time when this feedback was created.
     *
     * @return The creation date and time
     */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	/**
     * Sets the creation date and time for this feedback.
     *
     * @param createdDate The creation date and time to set
     */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

}
