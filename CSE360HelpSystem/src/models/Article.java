package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@code Article} class handles articles in program memory without interacting with the database.
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 */
public class Article {
    private int id;
    private char[] title;
    private char[] authors;
    private char[] abstractText;
    private char[] keywords;
    private char[] body;
    private char[] references;
    private String level;
    private List<String> groupingIdentifiers;
    private String permissions;
    private Date dateAdded;
    private String version;
    private Boolean isSpecialGroupArticle;

    // Constructor for creating a complete article with all fields including ID
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
        System.out.println("[Article] Article created with ID: " + id);
    }

    // Constructor for creating an article without an ID (typically used for new articles)
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
        System.out.println("[Article] New article created without ID.");
    }

    // Method to search for a keyword in the article
    public boolean searchArticle(String keyword) {
        boolean found = new String(this.keywords).contains(keyword) ||
                        new String(this.title).contains(keyword) ||
                        new String(this.body).contains(keyword);
        System.out.println("[Article] Search for keyword \"" + keyword + "\": " + found);
        return found;
    }

    // Getters and Setters with print statements
    public String getLevel() {
        System.out.println("[Article] Getting level: " + level);
        return level;
    }

    public void setLevel(String level) {
        System.out.println("[Article] Setting level to: " + level);
        this.level = level;
    }

    public List<String> getGroupingIdentifiers() {
        System.out.println("[Article] Getting grouping identifiers: " + groupingIdentifiers);
        return groupingIdentifiers;
    }

    public void setGroupingIdentifiers(List<String> groupingIdentifiers) {
        System.out.println("[Article] Setting grouping identifiers. New size: " + groupingIdentifiers.size());
        this.groupingIdentifiers = new ArrayList<>(groupingIdentifiers);
    }

    public String getPermissions() {
        System.out.println("[Article] Getting permissions: " + permissions);
        return permissions;
    }

    public void setPermissions(String permissions) {
        System.out.println("[Article] Setting permissions to: " + permissions);
        this.permissions = permissions;
    }

    public Date getDateAdded() {
        System.out.println("[Article] Getting date added: " + dateAdded);
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        System.out.println("[Article] Setting date added to: " + dateAdded);
        this.dateAdded = dateAdded;
    }

    public String getVersion() {
        System.out.println("[Article] Getting version: " + version);
        return version;
    }

    public void setVersion(String version) {
        System.out.println("[Article] Setting version to: " + version);
        this.version = version;
    }

    public int getId() {
        System.out.println("[Article] Getting ID: " + id);
        return id;
    }

    public void setId(int id) {
        System.out.println("[Article] Setting ID to: " + id);
        this.id = id;
    }

    public String getTitle() {
        System.out.println("[Article] Getting title: " + new String(title));
        return title != null ? new String(title) : null;
    }

    public void setTitle(char[] title) {
        System.out.println("[Article] Setting title to: " + new String(title));
        this.title = title;
    }

    public String getAuthors() {
        System.out.println("[Article] Getting authors: " + new String(authors));
        return authors != null ? new String(authors) : null;
    }

    public void setAuthors(char[] authors) {
        System.out.println("[Article] Setting authors to: " + new String(authors));
        this.authors = authors;
    }

    public String getAbstractText() {
        System.out.println("[Article] Getting abstract: " + new String(abstractText));
        return abstractText != null ? new String(abstractText) : null;
    }

    public void setAbstractText(char[] abstractText) {
        System.out.println("[Article] Setting abstract to: " + new String(abstractText));
        this.abstractText = abstractText;
    }

    public String getKeywords() {
        System.out.println("[Article] Getting keywords: " + new String(keywords));
        return keywords != null ? new String(keywords) : null;
    }

    public void setKeywords(char[] keywords) {
        System.out.println("[Article] Setting keywords to: " + new String(keywords));
        this.keywords = keywords;
    }

    public String getBody() {
        System.out.println("[Article] Getting body.");
        return body != null ? new String(body) : null;
    }

    public void setBody(char[] body) {
        System.out.println("[Article] Setting body.");
        this.body = body;
    }

    public String getReferences() {
        System.out.println("[Article] Getting references: " + new String(references));
        return references != null ? new String(references) : null;
    }

    public void setReferences(char[] references) {
        System.out.println("[Article] Setting references to: " + new String(references));
        this.references = references;
    }

    public void setAsPartOfSpecialGroup() {
        System.out.println("[Article] Setting article as part of a special group.");
        this.isSpecialGroupArticle = true;
    }

    public Boolean checkSpecialGroupArticle() {
        System.out.println("[Article] Checking if article is part of a special group: " + isSpecialGroupArticle);
        return this.isSpecialGroupArticle;
    }
}





