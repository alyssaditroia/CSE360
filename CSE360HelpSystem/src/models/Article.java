package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@code Article} class handles articles in program memory without interacting with the database.
 */
public class Article {
    /** 
     * The unique identifier for the article. 
     */
    private int id;
    
    /**
     * The title of the article stored as a character array for security.
     */
    private char[] title;
    
    /**
     * The authors of the article stored as a character array for security.
     */
    private char[] authors;
    
    /**
     * The abstract/short description of the article stored as a character array for security.
     */
    private char[] abstractText;  
    
    /**
     * Keywords associated with the article stored as a character array for security.
     */
    private char[] keywords;
    
    /**
     * The main content of the article stored as a character array for security.
     */
    private char[] body;
    
    /**
     * References cited in the article stored as a character array for security.
     */
    private char[] references;
    
    /**
     * The difficulty level of the article (beginner, intermediate, advanced, expert).
     */
    private String level;  
    
    /**
     * List of tags or categories associated with the article.
     */
    private List<String> groupingIdentifiers;  
    
    /**
     * Access permissions for the article.
     */
    private String permissions; 
    
    /**
     * The date when the article was created or added to the system.
     */
    private Date dateAdded;
    
    /**
     * Version number of the article (e.g., "1.0", "2.1").
     */
    private String version;
    
    // PHASE 3 ADDITIONS
    /**
     * Flag variable controlling if the article is generic or part of a special group
     * 
     * Articles by default are created as generic, and a function later will be called to set the flag variable for special group articles
     */
    private Boolean isSpecialGroupArticle;

    /**
     * Constructor for creating a complete article with all fields including ID.
     *
     * @param id The unique identifier for the article
     * @param title The article's title as a character array
     * @param authors The article's authors as a character array
     * @param abstractText A short description of the article as a character array
     * @param keywords Keywords associated with the article as a character array
     * @param body The main content of the article as a character array
     * @param references References cited in the article as a character array
     * @param level The difficulty level of the article (beginner, intermediate, advanced, expert)
     * @param groupingIdentifiers List of tags or categories for the article
     * @param permissions Access permissions for the article
     * @param dateAdded Date when the article was created
     * @param version Version number of the article
     */
    public Article(int id, char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body, char[] references,
                   String level, List<String> groupingIdentifiers, String permissions, Date dateAdded, String version) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.level = level;
        this.groupingIdentifiers = new ArrayList<>(groupingIdentifiers);
        this.permissions = permissions;
        this.dateAdded = dateAdded;
        this.version = version;
        this.isSpecialGroupArticle = false;
    }

    /**
     * Constructor for creating an article without an ID (typically used for new articles).
     *
     * @param title The article's title as a character array
     * @param authors The article's authors as a character array
     * @param abstractText A short description of the article as a character array
     * @param keywords Keywords associated with the article as a character array
     * @param body The main content of the article as a character array
     * @param references References cited in the article as a character array
     * @param level The difficulty level of the article (beginner, intermediate, advanced, expert)
     * @param groupingIdentifiers List of tags or categories for the article
     * @param permissions Access permissions for the article
     * @param dateAdded Date when the article was created
     * @param version Version number of the article
     */
    public Article(char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body, char[] references,
                   String level, List<String> groupingIdentifiers, String permissions, Date dateAdded, String version) {
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.level = level;
        this.groupingIdentifiers = new ArrayList<>(groupingIdentifiers);
        this.permissions = permissions;
        this.dateAdded = dateAdded;
        this.version = version;
    }

    /**
     * Searches the article's content for a specific keyword.
     *
     * @param keyword The term to search for
     * @return true if the keyword is found in the title, keywords, or body; false otherwise
     */
    public boolean searchArticle(String keyword) {
        return new String(this.keywords).contains(keyword) ||
               new String(this.title).contains(keyword) ||
               new String(this.body).contains(keyword);
    }
    
    /**
     * Gets the difficulty level of the article.
     *
     * @return The article's level (beginner, intermediate, advanced, expert)
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the difficulty level of the article.
     *
     * @param level The new level to set
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Gets the list of grouping identifiers (tags/categories) for the article.
     *
     * @return List of grouping identifiers
     */
    public List<String> getGroupingIdentifiers() {
        System.out.println("Getting grouping identifiers: " + groupingIdentifiers);
        return groupingIdentifiers;
    }

    /**
     * Sets the list of grouping identifiers (tags/categories) for the article.
     *
     * @param groupingIdentifiers New list of grouping identifiers
     */
    public void setGroupingIdentifiers(List<String> groupingIdentifiers) {
        this.groupingIdentifiers = new ArrayList<>(groupingIdentifiers);
    }

    /**
     * Gets the permissions settings for the article.
     *
     * @return The article's permissions
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * Sets the permissions settings for the article.
     *
     * @param permissions New permissions to set
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    /**
     * Gets the date when the article was added.
     *
     * @return The date the article was added
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * Sets the date when the article was added.
     *
     * @param dateAdded New date to set
     */
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * Gets the version number of the article.
     *
     * @return The article's version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version number of the article.
     *
     * @param version New version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the unique identifier of the article.
     *
     * @return The article's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the article.
     *
     * @param id New ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the title of the article.
     *
     * @return The article's title as a String, or null if title is not set
     */	
    public String getTitle() {
        return title != null ? new String(title) : null;  // Handle null title
    }

    /**
     * Sets the title of the article.
     *
     * @param title New title to set as a character array
     */
    public void setTitle(char[] title) {
        this.title = title;
    }

    /**
     * Gets the authors of the article.
     *
     * @return The article's authors as a String, or null if authors are not set
     */
    public String getAuthors() {
        return authors != null ? new String(authors) : null;  // Handle null authors
    }

    /**
     * Sets the authors of the article.
     *
     * @param authors New authors to set as a character array
     */
    public void setAuthors(char[] authors) {
        this.authors = authors;
    }

	/**
	 * Gets the abstract/short description of the article.
	 *
	 * @return The article's abstract as a String, or null if abstract is not set
	 */
    public String getAbstractText() {
        return abstractText != null ? new String(abstractText) : null;  // Handle null abstractText
    }

    /**
     * Sets the abstract/short description of the article.
     *
     * @param abstractText New abstract to set as a character array
     */
    public void setAbstractText(char[] abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * Gets the keywords associated with the article.
     *
     * @return The article's keywords as a String, or null if keywords are not set
     */
    public String getKeywords() {
        return keywords != null ? new String(keywords) : null;  // Handle null keywords
    }

	/**
	 * Sets the keywords associated with the article.
	 *
	 * @param keywords New keywords to set as a character array
	 */
    public void setKeywords(char[] keywords) {
        this.keywords = keywords;
    }

    /**
     * Gets the main content/body of the article.
     *
     * @return The article's body as a String, or null if body is not set
     */
    public String getBody() {
        return body != null ? new String(body) : null;  // Handle null body
    }

	/**
	 * Sets the main content/body of the article.
	 *
	 * @param body New body content to set as a character array
	 */
    public void setBody(char[] body) {
        this.body = body;
    }

    /**
     * Gets the references cited in the article.
     *
     * @return The article's references as a String, or null if references are not set
     */
    public String getReferences() {
        return references != null ? new String(references) : null;  // Handle null references
    }

    /**
     * Sets the references cited in the article.
     *
     * @param references New references to set as a character array
     */
    public void setReferences(char[] references) {
        this.references = references;
    }
    
    // PHASE 3 ADDITIONS
    /**
     * Sets the article to be created as part of a special group
     * 
     * Called when creating articles as part of a special group
     */
    public void setAsPartOfSpecialGroup() {
    	this.isSpecialGroupArticle = true;
    }
    
    /**
     * Checks if articles are generic or not
     * 
     * @return
     */
    public Boolean checkSpecialGroupArticle() {
    	return this.isSpecialGroupArticle;
    }
}




