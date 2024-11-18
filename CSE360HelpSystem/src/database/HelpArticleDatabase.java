package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.util.Arrays;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import models.Article;
/**
 * The {@code HelpArticleDatabase} handles interactions with the database for the HelpArticle table and Grouping Identifiers table
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 */
public class HelpArticleDatabase extends Database{

	/**
	 * Database instance for interacting with core database functionality
	 */
    private Database db; // NOTE: Uses the existing Database class
    
    /**
     * Database connection instance
     */
    private Connection connection;
    
    /**
     * Encryption helper instance for encrypting/decrypting article data
     */
    private EncryptionHelper encryptionHelper;

    /**
     * Constructor for HelpArticleDatabase
     * Initializes database connection and creates necessary tables
     * 
     * @throws Exception if database connection or table creation fails
     */
    public HelpArticleDatabase() throws Exception {
    	System.out.println("[HelpArticleDB] Help Article Table Initializing");
        // Initialize the Database instance and connection
        db = Database.getInstance(); 
        connection = db.getConnection(); // Get the shared connection
        if (connection != null) {
            System.out.println("[HelpArticleDB] Connection established successfully in HelpArticleDatabase.");
        } else {
            System.out.println("[HelpArticleDB] Connection is null in HelpArticleDatabase.");
        }
        encryptionHelper = new EncryptionHelper();
        createArticleTables(); // Create tables for articles if they don't exist
        createGroupingIdentifiersTable();
    }

    /**
     * Creates the articles table if it doesn't exist.
     *
     * @throws SQLException if a database access error occurs
     */
    public void createArticleTables() throws SQLException {
    	String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
    	        + "id INT AUTO_INCREMENT PRIMARY KEY, "
    	        + "iv VARCHAR(255), "  // Store Base64 encoded IV
    	        + "title CLOB, "        // Store encrypted title
    	        + "authors CLOB, "      // Store encrypted authors
    	        + "abstract CLOB, "     // Store encrypted abstract
    	        + "keywords CLOB, "     // Store encrypted keywords
    	        + "body CLOB, "         // Store encrypted body
    	        + "references CLOB, "   // Store encrypted references
    	        + "level VARCHAR(50), "  // New field for article level
    	        + "grouping_identifiers CLOB, " // New field for grouping/tags
    	        + "permissions VARCHAR(50), "   // New field for permissions
    	        + "date_added DATE, "   // New field for date added
    	        + "version VARCHAR(20))"; // New field for article version
        try (Statement statement = connection.createStatement()) {
            statement.execute(articleTable);
            System.out.println("[HelpArticleDB] Article table created or already exists");
        }catch (SQLException e) {
            System.out.println("[HelpArticleDB] Error creating article table: " + e.getMessage());
            throw e;
        }
    }
    
    
    /**
     * Creates the grouping identifiers table if it doesn't exist.
     *
     * @throws SQLException if a database access error occurs
     */
    public void createGroupingIdentifiersTable() throws SQLException {
        String groupingTable = "CREATE TABLE IF NOT EXISTS GroupingIdentifiers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "identifier VARCHAR(255) UNIQUE NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(groupingTable);
            System.out.println("[HelpArticleDB] Grouping Identifiers table created or already exists.");
        } catch (SQLException e) {
            System.out.println("[HelpArticleDB] Error creating Grouping Identifiers table: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Inserts a new grouping identifier into the database.
     * 
     * @param identifier The identifier to be added
     * @throws SQLException if a database access error occurs
     */
    public void insertGroupingIdentifier(String identifier) throws SQLException {
        String sql = "INSERT INTO GroupingIdentifiers (identifier) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, identifier);
            pstmt.executeUpdate();
            System.out.println("[HelpArticleDB] Inserted grouping identifier into database: " + identifier);
        } catch (SQLException e) {
            System.out.println("[HelpArticleDB] Error inserting grouping identifier: " + e.getMessage());
            throw e;
        }
    }
	/**
	 * Function added by Alyssa DiTroia 
	 * areArticlesEmpty() Method to check if the article table is empty
	 * 
	 * @return boolean True or False
	 * @throws SQLException
	 */
	public boolean areArticlesEmpty() throws SQLException {
		statement = connection.createStatement();
		String query = "SELECT COUNT(*) AS count FROM articles";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			boolean dbStatus = resultSet.getInt("count") == 0;
			System.out.println("[HelpArticleDB] Database is empty " + dbStatus);
			return resultSet.getInt("count") == 0;
		}
		return false;
	}
	
	/**
	 * Creates a new Article either from individual parameters or an Article object
	 * 
	 * @param title article title
	 * @param authors article authors
	 * @param abstractText article abstract
	 * @param keywords article keywords
	 * @param body article body content
	 * @param references article references
	 * @param level article difficulty level
	 * @param groupingIdentifiers list of groups/tags for the article
	 * @param permissions access permissions for the article
	 * @param dateAdded date the article was created
	 * @param version article version number
	 * 
	 * @throws Exception if encryption or database operation fails
	 */
	public void createArticle(char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body,
			char[] references, String level, List<String> groupingIdentifiers, String permissions, Date dateAdded,
			String version) throws Exception {
		System.out.println("[HelpArticleDB] Creating article with groups: " + groupingIdentifiers);
	    String groupingIdentifiersString = String.join(",", groupingIdentifiers);
	    System.out.println("[HelpArticleDB] Storing groups as: '" + groupingIdentifiersString + "'");
		
		byte[] iv = EncryptionUtils.getInitializationVector(title);

		byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(title), iv);
		byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(authors), iv);
		byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(abstractText), iv);
		byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(keywords), iv);
		byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(body), iv);
		byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(references), iv);

		String sql = "INSERT INTO articles (iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, Base64.getEncoder().encodeToString(iv));
			pstmt.setString(2, Base64.getEncoder().encodeToString(encryptedTitle));
			pstmt.setString(3, Base64.getEncoder().encodeToString(encryptedAuthors));
			pstmt.setString(4, Base64.getEncoder().encodeToString(encryptedAbstract));
			pstmt.setString(5, Base64.getEncoder().encodeToString(encryptedKeywords));
			pstmt.setString(6, Base64.getEncoder().encodeToString(encryptedBody));
			pstmt.setString(7, Base64.getEncoder().encodeToString(encryptedReferences));
			pstmt.setString(8, level); // Store the level
			pstmt.setString(9, groupingIdentifiersString); // Join grouping identifiers as a comma-separated
																		// string
			pstmt.setString(10, permissions); // Store the permissions
			pstmt.setDate(11, new java.sql.Date(dateAdded.getTime())); // Store the date added
			pstmt.setString(12, version); // Store the article version
			pstmt.executeUpdate();
		}
	}
	
	 /**
     * Creates a new article from an Article object.
     * Encrypts all sensitive data before storing in the database.
     * 
     * @param article Article object containing all article information
     * @throws Exception if encryption or database operation fails
     */
	public void createArticle(Article article) throws Exception {
	    // Generate IV based on article title
	    byte[] iv = EncryptionUtils.getInitializationVector(article.getTitle().toCharArray());

	    // Encrypt article fields using the IV
	    byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getTitle().toCharArray()), iv);
	    byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAuthors().toCharArray()), iv);
	    byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAbstractText().toCharArray()), iv);
	    byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getKeywords().toCharArray()), iv);
	    byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getBody().toCharArray()), iv);
	    byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getReferences().toCharArray()), iv);

	    // SQL query to insert the article into the database
	    String sql = "INSERT INTO articles (iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    // Prepare the SQL statement
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        // Set the encrypted values and other fields in the statement
	        pstmt.setString(1, Base64.getEncoder().encodeToString(iv));  // IV
	        pstmt.setString(2, Base64.getEncoder().encodeToString(encryptedTitle));  // Title
	        pstmt.setString(3, Base64.getEncoder().encodeToString(encryptedAuthors));  // Authors
	        pstmt.setString(4, Base64.getEncoder().encodeToString(encryptedAbstract));  // Abstract
	        pstmt.setString(5, Base64.getEncoder().encodeToString(encryptedKeywords));  // Keywords
	        pstmt.setString(6, Base64.getEncoder().encodeToString(encryptedBody));  // Body
	        pstmt.setString(7, Base64.getEncoder().encodeToString(encryptedReferences));  // References
	        pstmt.setString(8, article.getLevel());  // Level
	        pstmt.setString(9, String.join(",", article.getGroupingIdentifiers()));  // Grouping Identifiers
	        pstmt.setString(10, article.getPermissions());  // Permissions
	        pstmt.setDate(11, new java.sql.Date(article.getDateAdded().getTime()));  // Date added
	        pstmt.setString(12, article.getVersion());  // Version

	        // Execute the update
	        pstmt.executeUpdate();
	    }
	}


	/**
	 * Gets an article from the database and decrypts it
	 * 
	 * @param id the unique identifier of the article to retrieve
	 * @return String[] array containing decrypted article fields in order: title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version
	 * @throws SQLException if database retrieval fails
	 * @throws Exception if decryption fails or article not found
	 */
	public String[] getDecryptedArticle(int id) throws Exception {
	    String sql = "SELECT * FROM articles WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String ivBase64 = rs.getString("iv");
	                byte[] iv = Base64.getDecoder().decode(ivBase64);

	                byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
	                byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));
	                byte[] encryptedAbstract = Base64.getDecoder().decode(rs.getString("abstract"));
	                byte[] encryptedKeywords = Base64.getDecoder().decode(rs.getString("keywords"));
	                byte[] encryptedBody = Base64.getDecoder().decode(rs.getString("body"));
	                byte[] encryptedReferences = Base64.getDecoder().decode(rs.getString("references"));

	                String level = rs.getString("level");
	                String groupingIdentifiers = rs.getString("grouping_identifiers");
	                String permissions = rs.getString("permissions");
	                Date dateAdded = rs.getDate("date_added");
	                String version = rs.getString("version");

	                // Decrypt the data
	                char[] title = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedTitle, iv));
	                char[] authors = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAuthors, iv));
	                char[] abstractText = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAbstract, iv));
	                char[] keywords = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedKeywords, iv));
	                char[] body = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedBody, iv));
	                char[] references = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedReferences, iv));

	                return new String[]{
	                    new String(title),
	                    new String(authors),
	                    new String(abstractText),
	                    new String(keywords),
	                    new String(body),
	                    new String(references),
	                    level,
	                    groupingIdentifiers,
	                    permissions,
	                    dateAdded.toString(),
	                    version
	                };
	            } else {
	                throw new SQLException("Article with ID " + id + " not found.");
	            }
	        }
	    }
	}
	
	
    /**
     * Fetches all articles with decrypted titles, authors, and other details.
     * 
     * @return A list of Article objects
     * @throws Exception if an error occurs during the database operation
     */
	public List<Article> getAllDecryptedArticles() throws Exception {
	    List<Article> articles = new ArrayList<>();
	    String sql = "SELECT id, iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version FROM articles";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String ivBase64 = rs.getString("iv");
	            byte[] iv = Base64.getDecoder().decode(ivBase64);

	            byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
	            byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));
	            byte[] encryptedAbstract = Base64.getDecoder().decode(rs.getString("abstract"));
	            byte[] encryptedKeywords = Base64.getDecoder().decode(rs.getString("keywords"));
	            byte[] encryptedBody = Base64.getDecoder().decode(rs.getString("body"));
	            byte[] encryptedReferences = Base64.getDecoder().decode(rs.getString("references"));

	            String level = rs.getString("level");
	            String groupingIdentifiers = rs.getString("grouping_identifiers");
	            String permissions = rs.getString("permissions");
	            Date dateAdded = rs.getDate("date_added");
	            String version = rs.getString("version");

	            // Decrypt data
	            String title = new String(encryptionHelper.decrypt(encryptedTitle, iv));
	            String authors = new String(encryptionHelper.decrypt(encryptedAuthors, iv));
	            String abstractText = new String(encryptionHelper.decrypt(encryptedAbstract, iv));
	            String keywords = new String(encryptionHelper.decrypt(encryptedKeywords, iv));
	            String body = new String(encryptionHelper.decrypt(encryptedBody, iv));
	            String references = new String(encryptionHelper.decrypt(encryptedReferences, iv));

	            String groupingIdentifiersStr = rs.getString("grouping_identifiers");
	            System.out.println("Raw grouping identifiers from DB: '" + groupingIdentifiersStr + "'");
	            
	            List<String> groupList;
	            if (groupingIdentifiersStr != null && !groupingIdentifiersStr.trim().isEmpty()) {
	                groupList = new ArrayList<>(List.of(groupingIdentifiersStr.split(",")));
	                System.out.println("Parsed groups: " + groupList);
	            } else {
	                groupList = new ArrayList<>();
	                System.out.println("No groups found, using empty list");
	            }

	            // Create article with the groupList
	            Article article = new Article(id, title.toCharArray(), authors.toCharArray(), 
	                abstractText.toCharArray(), keywords.toCharArray(), body.toCharArray(), 
	                references.toCharArray(), level, groupList, permissions, dateAdded, version);
	            
	            articles.add(article);
	        }
	    }
	    System.out.println("[HelpArticleDB]  All articles retrieved from database");
	    return articles;
	}


	/**
	 * Clears sensitive data from character arrays
	 * @param array the character array to clear
	 */
	private void clearCharArray(char[] array) {
	    Arrays.fill(array, ' ');
	}
		
	/**
	 * Lists all articles in the database with basic information
	 * 
	 * @throws SQLException if database access fails
	 * @throws Exception if decryption fails or other errors occur
	 */
	public void listArticles() throws Exception {
		System.out.println("[HelpArticleDB] Listing Articles: ");
        String sql = "SELECT id, iv, title, authors FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int sequenceNumber = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String ivBase64 = rs.getString("iv");
                byte[] iv = Base64.getDecoder().decode(ivBase64);

                byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
                byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));

                // Decrypt title and authors
                String title = new String(encryptionHelper.decrypt(encryptedTitle, iv));
                String authors = new String(encryptionHelper.decrypt(encryptedAuthors, iv));

                // Display the article's ID, title, and authors
                System.out.println("ID: " + id + " | Article " + sequenceNumber++ + ": " + title + " by " + authors);
            }
        }
    }
		

	/**
	 * Filters articles based on their difficulty level
	 * 
	 * @param level the difficulty level to filter by
	 * @return List<Article> list of articles matching the specified level
	 * @throws SQLException if database access fails
	 * @throws Exception if decryption fails or other errors occur
	 */
	public List<Article> filterArticlesByLevel(String level) throws Exception {
	    List<Article> articles = new ArrayList<>();
	    String sql = "SELECT * FROM articles WHERE level = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, level);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                // Same decryption process as in getAllDecryptedArticles
	                // Fetch, decrypt, and create the Article object here
	                // Add the article to the list
	            }
	        }
	    }
	    return articles;
	}
		
	/**
     * Searches for articles based on a search query.
     * Searches through decrypted titles, authors, and keywords.
     * 
     * @param searchQuery the text to search for in articles
     * @return List<Article> list of articles matching the search query
     * @throws Exception if database access or decryption fails
     */
	public List<Article> searchArticles(String searchQuery) throws Exception {
	    List<Article> articles = new ArrayList<>();
	    String sql = "SELECT * FROM articles";  // Fetch all articles

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        // Iterate through all articles
	        while (rs.next()) {
	            // Decrypt each article
	            Article article = decryptArticleFromResultSet(rs);

	            // Check if the decrypted fields contain the search query
	            if (matchesSearchQuery(article, searchQuery.toLowerCase())) {
	                articles.add(article);  // Add matching article to the list
	            }
	        }
	    } catch (SQLException e) {
	        throw new SQLException("Error searching for articles: " + e.getMessage());
	    }
	    
	    return articles;  // Return the list of matching decrypted articles
	}
		
		
	/**
	 * Checks if an article matches the given search query in its title, authors, or keywords
	 * 
	 * @param article the Article to check
	 * @param searchQuery the search term to look for
	 * @return true if the article matches the search query, false otherwise
	 */
	private boolean matchesSearchQuery(Article article, String searchQuery) {
	    // Check if title, authors, or keywords contain the search query
	    return new String(article.getTitle()).toLowerCase().contains(searchQuery) ||
	           new String(article.getAuthors()).toLowerCase().contains(searchQuery) ||
	           new String(article.getKeywords()).toLowerCase().contains(searchQuery);
	}

		

	/**
	 * Retrieves a single article from the database and decrypts it
	 * 
	 * @param rs ResultSet containing the encrypted article data
	 * @return decrypted Article object
	 * @throws Exception if decryption or database operation fails
	 */
	public Article decryptArticleFromResultSet(ResultSet rs) throws Exception {
	    // Get the ID first
	    int id = rs.getInt("id");

	    // Retrieve the IV (Initialization Vector)
	    String ivBase64 = rs.getString("iv");
	    byte[] iv = Base64.getDecoder().decode(ivBase64);

	    // Retrieve and decrypt fields
	    byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
	    byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));
	    byte[] encryptedAbstract = Base64.getDecoder().decode(rs.getString("abstract"));
	    byte[] encryptedKeywords = Base64.getDecoder().decode(rs.getString("keywords"));
	    byte[] encryptedBody = Base64.getDecoder().decode(rs.getString("body"));
	    byte[] encryptedReferences = Base64.getDecoder().decode(rs.getString("references"));

	    // Decrypt fields using the IV and the encryption helper
	    char[] title = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedTitle, iv));
	    char[] authors = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAuthors, iv));
	    char[] abstractText = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAbstract, iv));
	    char[] keywords = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedKeywords, iv));
	    char[] body = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedBody, iv));
	    char[] references = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedReferences, iv));

	    // Get non-encrypted fields
	    String level = rs.getString("level");
	    String groupingIdentifiers = rs.getString("grouping_identifiers");
	    String permissions = rs.getString("permissions");
	    java.sql.Date dateAdded = rs.getDate("date_added");
	    String version = rs.getString("version");
	    
	    // Use the constructor that includes ID
	    return new Article(
	        id, title, authors, abstractText, keywords, body, references, level,
	        List.of(groupingIdentifiers.split(",")), permissions, dateAdded, version
	    );
	}


	/**
	 * Adds specified groups to the backup operation
	 * 
	 * @param filename path to backup file
	 * @param groups list of groups to include in backup
	 * @throws Exception if backup operation fails
	 */
	public void backupArticles(String filename) throws Exception {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
	        // Get all general articles using the existing method
	        List<Article> articles = getAllGeneralArticles();
	        
	        for (Article article : articles) {
	            // Generate IV for encryption
	            byte[] iv = EncryptionUtils.getInitializationVector(article.getTitle().toCharArray());
	            
	            // Encrypt the article fields
	            byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getTitle().toCharArray()), iv);
	            byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAuthors().toCharArray()), iv);
	            byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAbstractText().toCharArray()), iv);
	            byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getKeywords().toCharArray()), iv);
	            byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getBody().toCharArray()), iv);
	            byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getReferences().toCharArray()), iv);
	            
	            // Write each field to the backup file
	            writer.write(article.getId() + "\n");
	            writer.write(Base64.getEncoder().encodeToString(iv) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedTitle) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedAuthors) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedAbstract) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedKeywords) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedBody) + "\n");
	            writer.write(Base64.getEncoder().encodeToString(encryptedReferences) + "\n");
	            writer.write(article.getLevel() + "\n");
	            writer.write(String.join(",", article.getGroupingIdentifiers()) + "\n");
	            writer.write(article.getPermissions() + "\n");
	            writer.write(article.getDateAdded().toString() + "\n");
	            writer.write(article.getVersion() + "\n");
	            writer.write("END_OF_ARTICLE\n");
	        }
	    }
	    System.out.println("[HelpArticleDB] General articles backed up to " + filename);
	}

	/**
	 * Restores articles from a backup file
	 * 
	 * @param filename path to the backup file to restore from
	 * @throws SQLException if database operations fail
	 * @throws IOException if file reading fails
	 */
	public void restoreArticles(String filename) throws SQLException, IOException {
		// Delete all general articles from the database to replace them with backup
		String deleteSql = "DELETE FROM articles WHERE id NOT IN (SELECT article_id FROM special_group_articles)";
	    try (Statement stmt = connection.createStatement()) {
	        stmt.executeUpdate(deleteSql);
	    }

	    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            int id = Integer.parseInt(line);
	            String iv = reader.readLine();
	            String title = reader.readLine();
	            String authors = reader.readLine();
	            String abstractText = reader.readLine();
	            String keywords = reader.readLine();
	            String body = reader.readLine();
	            String references = reader.readLine();
	            String level = reader.readLine();
	            String groupingIdentifiers = reader.readLine();
	            String permissions = reader.readLine();
	            Date dateAdded = Date.valueOf(reader.readLine());  // Parsing the date from string
	            String version = reader.readLine();
	            reader.readLine(); // Skip "END_OF_ARTICLE"

	            String insertSql = "INSERT INTO articles (id, iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version) "
	                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
	                pstmt.setInt(1, id);
	                pstmt.setString(2, iv);
	                pstmt.setString(3, title);
	                pstmt.setString(4, authors);
	                pstmt.setString(5, abstractText);
	                pstmt.setString(6, keywords);
	                pstmt.setString(7, body);
	                pstmt.setString(8, references);
	                pstmt.setString(9, level);
	                pstmt.setString(10, groupingIdentifiers);
	                pstmt.setString(11, permissions);
	                pstmt.setDate(12, new java.sql.Date(dateAdded.getTime()));
	                pstmt.setString(13, version);
	                pstmt.executeUpdate();
	            }
	        }
	    }
	    System.out.println("[HelpArticleDB] Articles restored from " + filename);
	}
		

    /**
     * Updates an existing article in the database.
     * Encrypts all sensitive data before updating the database record.
     * 
     * @param article Article object containing updated information
     * @throws Exception if encryption or database update operation fails
     */
	public void updateArticle(Article article) throws Exception {
		char[] title = article.getTitle().toCharArray();
	    // Generate the IV (Initialization Vector) from the article title, just like in createArticle()
	    byte[] iv = EncryptionUtils.getInitializationVector(title);

	    // Encrypt all fields before updating
	    byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(title), iv);
	    byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAuthors().toCharArray()), iv);
	    byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAbstractText().toCharArray()), iv);
	    byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getKeywords().toCharArray()), iv);
	    byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getBody().toCharArray()), iv);
	    byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getReferences().toCharArray()), iv);

	    String sql = "UPDATE articles SET iv = ?, title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, " +
	                 "references = ?, level = ?, grouping_identifiers = ?, permissions = ?, date_added = ?, version = ? " +
	                 "WHERE id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        // Set the encrypted values in the update statement
	        pstmt.setString(1, Base64.getEncoder().encodeToString(iv)); // IV
	        pstmt.setString(2, Base64.getEncoder().encodeToString(encryptedTitle)); // Title
	        pstmt.setString(3, Base64.getEncoder().encodeToString(encryptedAuthors)); // Authors
	        pstmt.setString(4, Base64.getEncoder().encodeToString(encryptedAbstract)); // Abstract
	        pstmt.setString(5, Base64.getEncoder().encodeToString(encryptedKeywords)); // Keywords
	        pstmt.setString(6, Base64.getEncoder().encodeToString(encryptedBody)); // Body
	        pstmt.setString(7, Base64.getEncoder().encodeToString(encryptedReferences)); // References
	        pstmt.setString(8, article.getLevel()); // Level
	        pstmt.setString(9, String.join(",", article.getGroupingIdentifiers())); // Grouping Identifiers as a comma-separated string
	        pstmt.setString(10, article.getPermissions()); // Permissions
	        pstmt.setDate(11, new java.sql.Date(article.getDateAdded().getTime())); // Convert java.util.Date to java.sql.Date
	        pstmt.setString(12, article.getVersion()); // Version
	        pstmt.setInt(13, article.getId()); // Article ID in the WHERE clause to identify which article to update

	        pstmt.executeUpdate(); // Execute the update
	    } catch (SQLException e) {
	        throw new SQLException("Error updating article: " + e.getMessage());
	    }
	}


	/**
     * Deletes an article from the database.
     * 
     * @param id unique identifier of the article to delete
     * @throws SQLException if database deletion operation fails
     */
    public void deleteArticle(int id) throws SQLException {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[HelpArticleDB] Article with ID " + id + " deleted successfully.");
            } else {
                System.out.println("[HelpArticleDB] No article found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("[HelpArticleDB] Error deleting article: " + e.getMessage());
            throw e;  // Re-throw the exception to handle it further up the call chain
        }
    }
	    
	    
    /**
     * Fetches all grouping identifiers from the database.
     * 
     * @return a list of all grouping identifiers
     * @throws SQLException if a database access error occurs
     */
    public List<String> fetchGroupingIdentifiers() throws SQLException {
        List<String> identifiers = new ArrayList<>();
        String sql = "SELECT identifier FROM GroupingIdentifiers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                identifiers.add(rs.getString("identifier"));
            }
        } catch (SQLException e) {
            System.out.println("[HelpArticleDB] Error fetching grouping identifiers: " + e.getMessage());
            throw e;
        }
        return identifiers;
    }
	
    /**
     * Backs up articles from specific groups to a file
     * 
     * @param filename path where backup file should be created
     * @param groups list of group identifiers to include in backup
     * @throws SQLException if database access fails
     * @throws IOException if file writing fails
     * @throws Exception if encryption fails or other errors occur
     */
    public void backupGroupArticles(String filename, List<String> groups) throws Exception {
        List<Article> articlesToBackup = getAllGeneralArticles().stream()
            .filter(article -> article.getGroupingIdentifiers().stream()
                .anyMatch(groups::contains))
            .collect(Collectors.toList());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Article article : articlesToBackup) {
                // Generate IV for encryption
                byte[] iv = EncryptionUtils.getInitializationVector(article.getTitle().toCharArray());
                
                // Encrypt the article fields
                byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getTitle().toCharArray()), iv);
                byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAuthors().toCharArray()), iv);
                byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getAbstractText().toCharArray()), iv);
                byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getKeywords().toCharArray()), iv);
                byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getBody().toCharArray()), iv);
                byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(article.getReferences().toCharArray()), iv);
                
                // Write each field to the backup file
                writer.write(article.getId() + "\n");
                writer.write(Base64.getEncoder().encodeToString(iv) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedTitle) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedAuthors) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedAbstract) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedKeywords) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedBody) + "\n");
                writer.write(Base64.getEncoder().encodeToString(encryptedReferences) + "\n");
                writer.write(article.getLevel() + "\n");
                writer.write(String.join(",", article.getGroupingIdentifiers()) + "\n");
                writer.write(article.getPermissions() + "\n");
                writer.write(article.getDateAdded().toString() + "\n");
                writer.write(article.getVersion() + "\n");
                writer.write("END_OF_ARTICLE\n");
            }
        }
        System.out.println("[HelpArticleDB] Group-specific articles backed up to " + filename);
    }

    /**
     * Restores articles with option to merge with existing content
     * 
     * @param filename path to the backup file to restore from
     * @param merge if true, preserves existing articles and only adds new ones; if false, replaces all content
     * @throws SQLException if database operations fail
     * @throws IOException if file reading fails
     */
    public void restoreArticlesWithMerge(String filename, boolean merge) throws SQLException, IOException {
        if (!merge) {
            // If not merging, use existing restore method which replaces all content
            restoreArticles(filename);
            return;
        }

        // Read existing article IDs
        Set<Integer> existingIds = new HashSet<>();
        String selectSql = "SELECT id FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id"));
            }
        }

        // Read and process backup file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int id = Integer.parseInt(line);
                
                // Read article data
                String iv = reader.readLine();
                String title = reader.readLine();
                String authors = reader.readLine();
                String abstractText = reader.readLine();
                String keywords = reader.readLine();
                String body = reader.readLine();
                String references = reader.readLine();
                String level = reader.readLine();
                String groupingIdentifiers = reader.readLine();
                String permissions = reader.readLine();
                Date dateAdded = Date.valueOf(reader.readLine());
                String version = reader.readLine();
                reader.readLine(); // Skip "END_OF_ARTICLE"

                // If ID exists and we're merging, skip this article
                if (existingIds.contains(id)) {
                    continue;
                }

                // Insert new article
                String insertSql = "INSERT INTO articles (id, iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, iv);
                    pstmt.setString(3, title);
                    pstmt.setString(4, authors);
                    pstmt.setString(5, abstractText);
                    pstmt.setString(6, keywords);
                    pstmt.setString(7, body);
                    pstmt.setString(8, references);
                    pstmt.setString(9, level);
                    pstmt.setString(10, groupingIdentifiers);
                    pstmt.setString(11, permissions);
                    pstmt.setDate(12, new java.sql.Date(dateAdded.getTime()));
                    pstmt.setString(13, version);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    
    /**
     * Gets all articles that are not part of any special group
     * 
     * @return List<Article> list of general articles not tied to any special group
     * @throws Exception if database access or decryption fails
     */
    public List<Article> getAllGeneralArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        
        // First check if special_group_articles table exists
        boolean specialTableExists = false;
        try (ResultSet rs = connection.getMetaData().getTables(null, null, "SPECIAL_GROUP_ARTICLES", null)) {
            specialTableExists = rs.next();
        }
        
        // Build appropriate SQL query based on table existence
        String sql;
        if (specialTableExists) {
            sql = "SELECT id, iv, title, authors, abstract, keywords, body, references, level, " +
                  "grouping_identifiers, permissions, date_added, version FROM articles " +
                  "WHERE id NOT IN (SELECT article_id FROM special_group_articles)";
        } else {
            // If table doesn't exist, just get all articles
            sql = "SELECT id, iv, title, authors, abstract, keywords, body, references, level, " +
                  "grouping_identifiers, permissions, date_added, version FROM articles";
        }
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Use existing decryption logic
                Article article = decryptArticleFromResultSet(rs);
                articles.add(article);
            }
        }
        System.out.println("[HelpArticleDB] All general articles retrieved from database");
        return articles;
    }
    
    
    public void backupSpecialGroupArticles(String filename, int groupId) throws SQLException, IOException {
        // Debug print
        System.out.println("[HelpArticleDB] Backing up articles for group ID: " + groupId);
        
        String sql = "SELECT a.* FROM articles a " +
                     "INNER JOIN special_group_articles sga ON a.id = sga.article_id " +
                     "WHERE sga.group_id = ?";
                     
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            
            // Debug print
            System.out.println("[HelpArticleDB] Found articles in group: " + rs.getFetchSize());
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    // Debug print
                    System.out.println("[HelpArticleDB] Writing article ID: " + id);
                    
                    String ivBase64 = rs.getString("iv");
                    String title = rs.getString("title");
                    String authors = rs.getString("authors");
                    String abstractText = rs.getString("abstract");
                    String keywords = rs.getString("keywords");
                    String body = rs.getString("body");
                    String references = rs.getString("references");
                    String level = rs.getString("level");
                    String groupingIdentifiers = rs.getString("grouping_identifiers");
                    String permissions = rs.getString("permissions");
                    String dateAdded = rs.getString("date_added");
                    String version = rs.getString("version");

                    // Write each field to the backup file
                    writer.write(id + "\n");
                    writer.write(ivBase64 + "\n");
                    writer.write(title + "\n");
                    writer.write(authors + "\n");
                    writer.write(abstractText + "\n");
                    writer.write(keywords + "\n");
                    writer.write(body + "\n");
                    writer.write(references + "\n");
                    writer.write(level + "\n");
                    writer.write(groupingIdentifiers + "\n");
                    writer.write(permissions + "\n");
                    writer.write(dateAdded + "\n");
                    writer.write(version + "\n");
                    writer.write("END_OF_ARTICLE\n");
                }
            }
        }
    }
    
    
    public void restoreSpecialGroupArticles(String filename, int groupId) throws SQLException, IOException {
        // First clear existing articles for this group
        String deleteSql = "DELETE FROM special_group_articles WHERE group_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, groupId);
            deleteStmt.executeUpdate();
        }

        // Now restore from backup and associate with group
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int id = Integer.parseInt(line);
                String iv = reader.readLine();
                String title = reader.readLine();
                String authors = reader.readLine();
                String abstractText = reader.readLine();
                String keywords = reader.readLine();
                String body = reader.readLine();
                String references = reader.readLine();
                String level = reader.readLine();
                String groupingIdentifiers = reader.readLine();
                String permissions = reader.readLine();
                Date dateAdded = Date.valueOf(reader.readLine());
                String version = reader.readLine();
                reader.readLine(); // Skip "END_OF_ARTICLE"

                // Insert/Update the article
                String insertSql = "MERGE INTO articles (id, iv, title, authors, abstract, keywords, body, references, level, grouping_identifiers, permissions, date_added, version) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, iv);
                    pstmt.setString(3, title);
                    pstmt.setString(4, authors);
                    pstmt.setString(5, abstractText);
                    pstmt.setString(6, keywords);
                    pstmt.setString(7, body);
                    pstmt.setString(8, references);
                    pstmt.setString(9, level);
                    pstmt.setString(10, groupingIdentifiers);
                    pstmt.setString(11, permissions);
                    pstmt.setDate(12, dateAdded);
                    pstmt.setString(13, version);
                    pstmt.executeUpdate();
                    
                    // Associate with special group
                    String groupSql = "INSERT INTO special_group_articles (group_id, article_id) VALUES (?, ?)";
                    try (PreparedStatement groupStmt = connection.prepareStatement(groupSql)) {
                        groupStmt.setInt(1, groupId);
                        groupStmt.setInt(2, id);
                        groupStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
	