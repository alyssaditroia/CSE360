package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@code Article} class handles articles in program memory without interacting with the database.
 */
public class Article {
    private int id;
    private char[] title;
    private char[] authors;
    private char[] abstractText;  // Use this as "Short Description"
    private char[] keywords;
    private char[] body;
    private char[] references;
    private String level;  // beginner, intermediate, advanced, expert
    private List<String> groupingIdentifiers;  // Tags, categories, etc.
    private String permissions;  // Article permissions as a String (can be Enum)
    private Date dateAdded;
    private String version;  // e.g., "1.0", "2.1", etc.

    /**
     * Constructor for full article creation.
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
    }

    /**
     * Constructor for partial article creation (without ID).
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

    // Getters and Setters for new fields
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getGroupingIdentifiers() {
        System.out.println("Getting grouping identifiers: " + groupingIdentifiers);
        return groupingIdentifiers;
    }

    public void setGroupingIdentifiers(List<String> groupingIdentifiers) {
        this.groupingIdentifiers = new ArrayList<>(groupingIdentifiers);
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // Existing Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return new String(title);
    }

    public void setTitle(char[] title) {
        this.title = title;
    }

    public String getAuthors() {
        return new String(authors);
    }

    public void setAuthors(char[] authors) {
        this.authors = authors;
    }

    public String getAbstractText() {
        return new String(abstractText);
    }

    public void setAbstractText(char[] abstractText) {
        this.abstractText = abstractText;
    }

    public String getKeywords() {
        return new String(keywords);
    }

    public void setKeywords(char[] keywords) {
        this.keywords = keywords;
    }

    public String getBody() {
        return new String(body);
    }

    public void setBody(char[] body) {
        this.body = body;
    }

    public String getReferences() {
        return new String(references);
    }

    public void setReferences(char[] references) {
        this.references = references;
    }

    public boolean searchArticle(String keyword) {
        return new String(this.keywords).contains(keyword) ||
               new String(this.title).contains(keyword) ||
               new String(this.body).contains(keyword);
    }
}




