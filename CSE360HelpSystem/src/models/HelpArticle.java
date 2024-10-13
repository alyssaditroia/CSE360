package models;

import java.time.LocalDateTime;
import java.util.List;

 
/**
 * 
 */
public class HelpArticle {
	// Unique ID of the article
    private String articleID;
    // Article title
    private String title;
    // Article short description
    private String shortDescription;
    // The body of the help article
    private String content;
    // Logs user who created the article
    private User createdBy;
    // Records the date the article was initially created
    private LocalDateTime createdDate;
    // Most recent date that the article was updated
    private LocalDateTime lastUpdated;
    // Level of the article
    private ArticleLevels level;
    // Grouping identifiers for the article
    private List<String> groupingIdentifiers;
    // Keywords for the article
    private List<String> keywords;
    // Reference materials for the article
    private List<String> referenceMaterials;
    // Related articles for the article
    private List<String> relatedArticles;
    // Controls if instructors users can access the article
    private Boolean instructorAccess;
    // Controls if student users can access the article
    private Boolean studentAccess;
    
    
    // Enum to store the four potential article levels
    public enum ArticleLevels {
    	BEGINNER,
    	INTERMEDIATE,
    	ADVANCED,
    	EXPERT
    }
    
    /**
     * 
     */
    public HelpArticle() {
    }
    
    // Getters and Setters
    // (Include getters and setters for each field)
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
	 * 
	 * @return
	 */
	public String getDescription() {
		return shortDescription;
	}
	
	/**
	 * 
	 * @param shortDescription
	 */
	public void setDescription(String shortDescription) {
		this.shortDescription = shortDescription;
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
	
	/**
	 * 
	 * @return
	 */
	public ArticleLevels getLevel() {
        return level;
    }
	
	/**
	 * 
	 * @param level
	 */
    public void setLevel(ArticleLevels level) {
        this.level = level;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getGroupingIdentifiers() {
        return groupingIdentifiers;
    }
    
    /**
     * 
     * @param groupingIdentifiers
     */
    public void setGroupingIdentifiers(List<String> groupingIdentifiers) {
        this.groupingIdentifiers = groupingIdentifiers;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getKeywords() {
        return keywords;
    }
    
    /**
     * 
     * @param keywords
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getReferenceMaterials() {
        return referenceMaterials;
    }
    
    /**
     * 
     * @param referenceMaterials
     */
    public void setReferenceMaterials(List<String> referenceMaterials) {
        this.referenceMaterials = referenceMaterials;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getRelatedArticles() {
        return relatedArticles;
    }
    
    /**
     * 
     * @param relatedArticles
     */
    public void setRelatedArticles(List<String> relatedArticles) {
        this.relatedArticles = relatedArticles;
    }
    
    /**
     * 
     * @return
     */
    public Boolean getInstructorAccess() {
        return instructorAccess;
    }
    
    /**
     * 
     * @param instructorAccess
     */
    public void setInstructorAccess(Boolean instructorAccess) {
        this.instructorAccess = instructorAccess;
    }
    
    /**
     * 
     * @return
     */
    public Boolean getStudentAccess() {
        return studentAccess;
    }
    
    /**
     * 
     * @param studentAccess
     */
    public void setStudentAccess(Boolean studentAccess) {
        this.studentAccess = studentAccess;
    }
    
}
