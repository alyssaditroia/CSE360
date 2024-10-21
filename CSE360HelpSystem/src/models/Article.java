package models;
/**
 *  the {@code Article} class handles articles in program memory without interacting with the database
 */
public class Article {
    private int id;
    private char [] title;
    private char [] authors;
    private char [] abstractText;
    private char [] keywords;
    private char [] body;
    private char [] references;

    /**
     * Constructor for full article creation
     * @param id
     * @param title
     * @param authors
     * @param abstractText
     * @param keywords
     * @param body
     * @param references
     */
    public Article(int id, char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body, char[] references) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
    }

    /**
     * Constructor for partial article (without ID, for creation)
     * @param title2
     * @param authors2
     * @param abstractText2
     * @param keywords2
     * @param body2
     * @param references2
     */
    public Article(char[] title2, char[] authors2, char[] abstractText2, char[] keywords2, char[] body2, char[] references2) {
        this.title = title2;
        this.authors = authors2;
        this.abstractText = abstractText2;
        this.keywords = keywords2;
        this.body = body2;
        this.references = references2;
    }

    // Getters and Setters
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
}



