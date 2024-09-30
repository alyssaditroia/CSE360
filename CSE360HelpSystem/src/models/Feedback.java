package models;

import java.time.LocalDateTime;

public class Feedback {
    private String feedbackID;
    private User user;
    private HelpArticle article;
    private String message;
    private LocalDateTime createdDate;

    public Feedback(String feedbackID, User user, HelpArticle article, String message) {
        this.setFeedbackID(feedbackID);
        this.setUser(user);
        this.setArticle(article);
        this.setMessage(message);
        this.setCreatedDate(LocalDateTime.now());
    }

	/**
	 * @return the feedbackID
	 */
	public String getFeedbackID() {
		return feedbackID;
	}

	/**
	 * @param feedbackID the feedbackID to set
	 */
	public void setFeedbackID(String feedbackID) {
		this.feedbackID = feedbackID;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the article
	 */
	public HelpArticle getArticle() {
		return article;
	}

	/**
	 * @param article the article to set
	 */
	public void setArticle(HelpArticle article) {
		this.article = article;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the createdDate
	 */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

}