package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.util.Arrays;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import models.Article;

public class HelpArticleDatabase extends Database{

    private Database db; // Use the existing Database class
    private Connection connection;
    private EncryptionHelper encryptionHelper;

    public HelpArticleDatabase() throws Exception {
    	System.out.println("[INFO] Help Article Table Initializing");
        // Initialize the Database instance and connection
        db = Database.getInstance(); 
        connection = db.getConnection(); // Get the shared connection
        if (connection != null) {
            System.out.println("[INFO] Connection established successfully in HelpArticleDatabase.");
        } else {
            System.out.println("[ERROR] Connection is null in HelpArticleDatabase.");
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
    	System.out.println("[INFO] Creating help article table if it does not exist");
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
            System.out.println("[INFO] Article table created or already exists.");
        }catch (SQLException e) {
            System.out.println("[ERROR] Error creating article table: " + e.getMessage());
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
            System.out.println("[INFO] Grouping Identifiers table created or already exists.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Error creating Grouping Identifiers table: " + e.getMessage());
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
            System.out.println("[INFO] Inserted grouping identifier into database: " + identifier);
        } catch (SQLException e) {
            System.out.println("[ERROR] Error inserting grouping identifier: " + e.getMessage());
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
			System.out.println("Database is empty " + dbStatus);
			return resultSet.getInt("count") == 0;
		}
		return false;
	}
	/**
	 * Creates new article in the database
	 * @param title
	 * @param authors
	 * @param abstractText
	 * @param keywords
	 * @param body
	 * @param references
	 * @throws Exception
	 */
	public void createArticle(char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body,
			char[] references, String level, List<String> groupingIdentifiers, String permissions, Date dateAdded,
			String version) throws Exception {
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
			pstmt.setString(9, String.join(",", groupingIdentifiers)); // Join grouping identifiers as a comma-separated
																		// string
			pstmt.setString(10, permissions); // Store the permissions
			pstmt.setDate(11, new java.sql.Date(dateAdded.getTime())); // Store the date added
			pstmt.setString(12, version); // Store the article version
			pstmt.executeUpdate();
		}
	}

    /**
     * Gets article from the database and decrypts it
     * @param id
     * @return
     * @throws Exception
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
	    System.out.println("Fetching all articles from database...");
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

	            // Create and add Article object
	            Article article = new Article(id, title.toCharArray(), authors.toCharArray(), abstractText.toCharArray(),
	                    keywords.toCharArray(), body.toCharArray(), references.toCharArray(), level, 
	                    List.of(groupingIdentifiers.split(",")), permissions, dateAdded, version);
	            articles.add(article);
	        }
	    }
	    return articles;
	}


    	/**
    	 * Helper function to clear character arrays from memory
    	 * @param array
    	 */
		private void clearCharArray(char[] array) {
		    Arrays.fill(array, ' ');
		}
		
		/**
		 * Lists all articles
		 * @throws Exception
		 */
		public void listArticles() throws Exception {
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
		 * filterArticlesByLevel will only select articles from the specified level
		 * @param level
		 * @return
		 * @throws Exception
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
		 * 
		 * @param searchQuery
		 * @return
		 * @throws Exception
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
		 * 
		 * @param article
		 * @param searchQuery
		 * @return
		 */
		private boolean matchesSearchQuery(Article article, String searchQuery) {
		    // Check if title, authors, or keywords contain the search query
		    return new String(article.getTitle()).toLowerCase().contains(searchQuery) ||
		           new String(article.getAuthors()).toLowerCase().contains(searchQuery) ||
		           new String(article.getKeywords()).toLowerCase().contains(searchQuery);
		}

		
		/**
		 * 
		 * @param rs
		 * @return
		 * @throws Exception
		 */
		public Article decryptArticleFromResultSet(ResultSet rs) throws Exception {
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

		    // Get non-encrypted fields (e.g., level, grouping identifiers, permissions)
		    String level = rs.getString("level");
		    String groupingIdentifiers = rs.getString("grouping_identifiers");
		    String permissions = rs.getString("permissions");
		    java.sql.Date dateAdded = rs.getDate("date_added");
		    String version = rs.getString("version");

		    // Create and return the Article object
		    return new Article(
		        title, authors, abstractText, keywords, body, references, level,
		        List.of(groupingIdentifiers.split(",")), permissions, dateAdded, version
		    );
		}


		/**
		 * Backups articles from database
		 * @param filename
		 * @throws SQLException
		 * @throws IOException
		 */
		public void backupArticles(String filename) throws SQLException, IOException {
		    String sql = "SELECT * FROM articles";
		    try (Statement stmt = connection.createStatement();
		         ResultSet rs = stmt.executeQuery(sql);
		         BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
		        
		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String iv = rs.getString("iv");
		            String title = rs.getString("title");
		            String authors = rs.getString("authors");
		            String abstractText = rs.getString("abstract");
		            String keywords = rs.getString("keywords");
		            String body = rs.getString("body");
		            String references = rs.getString("references");
		            String level = rs.getString("level");
		            String groupingIdentifiers = rs.getString("grouping_identifiers");
		            String permissions = rs.getString("permissions");
		            Date dateAdded = rs.getDate("date_added");
		            String version = rs.getString("version");

		            // Write each field to the backup file
		            writer.write(id + "\n");
		            writer.write(iv + "\n");
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
		    System.out.println("Articles backed up to " + filename);
		}


	    /**
	     * restores articles
	     * @param filename
	     * @throws SQLException
	     * @throws IOException
	     */
		public void restoreArticles(String filename) throws SQLException, IOException {
		    String deleteSql = "DELETE FROM articles"; // Clears current data before restore
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
		    System.out.println("Articles restored from " + filename);
		}
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
	     * Deletes an article
	     * @param id
	     * @throws SQLException
	     */
	    public void deleteArticle(int id) throws SQLException {
	        String sql = "DELETE FROM articles WHERE id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, id);
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("Article with ID " + id + " deleted successfully.");
	            } else {
	                System.out.println("No article found with ID " + id + ".");
	            }
	        } catch (SQLException e) {
	            System.err.println("Error deleting article: " + e.getMessage());
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
	            System.out.println("[INFO] Error fetching grouping identifiers: " + e.getMessage());
	            throw e;
	        }
	        return identifiers;
	    }
	}