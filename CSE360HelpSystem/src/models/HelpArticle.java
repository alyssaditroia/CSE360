package models;

import java.time.LocalDateTime;
import java.util.List;

import enums.SkillRating;

public class HelpArticle {
    private String articleID;
    private String title;
    private String content;
    private List<String> tags;
    private SkillRating targetUserLevel; // Assume UserLevel is another enum or class
    private User createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public HelpArticle(String articleID, String title, String content, List<String> tags,
                       SkillRating targetUserLevel, User createdBy) {
        this.setArticleID(articleID);
        this.setTitle(title);
        this.setContent(content);
        this.setTags(tags);
        this.setTargetUserLevel(targetUserLevel);
        this.setCreatedBy(createdBy);
        this.setCreatedDate(LocalDateTime.now());
        this.setLastUpdated(LocalDateTime.now());
    }

	/**
	 * @return the articleID
	 */
	public String getArticleID() {
		return articleID;
	}

	/**
	 * @param articleID the articleID to set
	 */
	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the targetUserLevel
	 */
	public SkillRating getTargetUserLevel() {
		return targetUserLevel;
	}

	/**
	 * @param targetUserLevel the targetUserLevel to set
	 */
	public void setTargetUserLevel(SkillRating targetUserLevel) {
		this.targetUserLevel = targetUserLevel;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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

	/**
	 * @return the lastUpdated
	 */
	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

    // Getters and Setters
    // (Include getters and setters for each field)
}
