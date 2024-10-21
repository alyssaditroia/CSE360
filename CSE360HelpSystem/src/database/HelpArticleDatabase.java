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
    	System.out.println("Initializing HelpArticleDatabase...");
        // Initialize the Database instance and connection
        db = Database.getInstance(); 
        connection = db.getConnection(); // Get the shared connection
        if (connection != null) {
            System.out.println("Connection established successfully in HelpArticleDatabase.");
        } else {
            System.out.println("Connection is null in HelpArticleDatabase.");
        }
        encryptionHelper = new EncryptionHelper();
        createArticleTables(); // Create tables for articles if they don't exist
    }

    /**
     * Creates the articles table if it doesn't exist.
     *
     * @throws SQLException if a database access error occurs
     */
    public void createArticleTables() throws SQLException {
    	System.out.println("Creating Help Article Table....");
        String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "iv VARCHAR(255), "  // Store Base64 encoded IV
                + "title CLOB, "        // Store encrypted title
                + "authors CLOB, "      // Store encrypted authors
                + "abstract CLOB, "     // Store encrypted abstract
                + "keywords CLOB, "     // Store encrypted keywords
                + "body CLOB, "         // Store encrypted body
                + "references CLOB)";   // Store encrypted references
        try (Statement statement = connection.createStatement()) {
            statement.execute(articleTable);
            System.out.println("Article table created or already exists.");
        }catch (SQLException e) {
            System.out.println("Error creating article table: " + e.getMessage());
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
    public void createArticle(char[] title, char[] authors, char[] abstractText, char[] keywords, char[] body, char[] references) throws Exception {
    	
    	System.out.println("Creating new article...");
        System.out.println("Title: " + new String(title));
        
        byte[] iv = EncryptionUtils.getInitializationVector(title);  // Generate an IV based on the title
        System.out.println("Generated IV for encryption.");

        byte[] encryptedTitle = encryptionHelper.encrypt(EncryptionUtils.toByteArray(title), iv);
        byte[] encryptedAuthors = encryptionHelper.encrypt(EncryptionUtils.toByteArray(authors), iv);
        byte[] encryptedAbstract = encryptionHelper.encrypt(EncryptionUtils.toByteArray(abstractText), iv);
        byte[] encryptedKeywords = encryptionHelper.encrypt(EncryptionUtils.toByteArray(keywords), iv);
        byte[] encryptedBody = encryptionHelper.encrypt(EncryptionUtils.toByteArray(body), iv);
        byte[] encryptedReferences = encryptionHelper.encrypt(EncryptionUtils.toByteArray(references), iv);

        // Store encrypted data in the database as Base64-encoded strings
        String sql = "INSERT INTO articles (iv, title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, Base64.getEncoder().encodeToString(iv));
            pstmt.setString(2, Base64.getEncoder().encodeToString(encryptedTitle));
            pstmt.setString(3, Base64.getEncoder().encodeToString(encryptedAuthors));
            pstmt.setString(4, Base64.getEncoder().encodeToString(encryptedAbstract));
            pstmt.setString(5, Base64.getEncoder().encodeToString(encryptedKeywords));
            pstmt.setString(6, Base64.getEncoder().encodeToString(encryptedBody));
            pstmt.setString(7, Base64.getEncoder().encodeToString(encryptedReferences));
            pstmt.executeUpdate();    
            System.out.println("Article created successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting article: " + e.getMessage());
            throw e;
        }

        // Clear sensitive data from memory
        clearCharArray(title);
        clearCharArray(authors);
        clearCharArray(abstractText);
        clearCharArray(keywords);
        clearCharArray(body);
        clearCharArray(references);
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
                    // Retrieve encrypted data and IV
                    String ivBase64 = rs.getString("iv");
                    byte[] iv = Base64.getDecoder().decode(ivBase64);

                    byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
                    byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));
                    byte[] encryptedAbstract = Base64.getDecoder().decode(rs.getString("abstract"));
                    byte[] encryptedKeywords = Base64.getDecoder().decode(rs.getString("keywords"));
                    byte[] encryptedBody = Base64.getDecoder().decode(rs.getString("body"));
                    byte[] encryptedReferences = Base64.getDecoder().decode(rs.getString("references"));

                    // Decrypt the data
                    char[] title = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedTitle, iv));
                    char[] authors = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAuthors, iv));
                    char[] abstractText = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedAbstract, iv));
                    char[] keywords = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedKeywords, iv));
                    char[] body = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedBody, iv));
                    char[] references = EncryptionUtils.toCharArray(encryptionHelper.decrypt(encryptedReferences, iv));

                    // Return the article details as a String array
                    return new String[]{
                            new String(title),
                            new String(authors),
                            new String(abstractText),
                            new String(keywords),
                            new String(body),
                            new String(references)
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
        String sql = "SELECT id, iv, title, authors, abstract, keywords, body, references FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");

                // Retrieve encrypted data and IV
                String ivBase64 = rs.getString("iv");
                byte[] iv = Base64.getDecoder().decode(ivBase64);

                byte[] encryptedTitle = Base64.getDecoder().decode(rs.getString("title"));
                byte[] encryptedAuthors = Base64.getDecoder().decode(rs.getString("authors"));
                byte[] encryptedAbstract = Base64.getDecoder().decode(rs.getString("abstract"));
                byte[] encryptedKeywords = Base64.getDecoder().decode(rs.getString("keywords"));
                byte[] encryptedBody = Base64.getDecoder().decode(rs.getString("body"));
                byte[] encryptedReferences = Base64.getDecoder().decode(rs.getString("references"));

                // Decrypt the data
                String title = new String(encryptionHelper.decrypt(encryptedTitle, iv));
                String authors = new String(encryptionHelper.decrypt(encryptedAuthors, iv));
                String abstractText = new String(encryptionHelper.decrypt(encryptedAbstract, iv));
                String keywords = new String(encryptionHelper.decrypt(encryptedKeywords, iv));
                String body = new String(encryptionHelper.decrypt(encryptedBody, iv));
                String references = new String(encryptionHelper.decrypt(encryptedReferences, iv));

                // Create an Article object and add it to the list
                Article article = new Article(id, title.toCharArray(), authors.toCharArray(), abstractText.toCharArray(), keywords.toCharArray(), body.toCharArray(), references.toCharArray());
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

	                writer.write(id + "\n");
	                writer.write(iv + "\n");
	                writer.write(title + "\n");
	                writer.write(authors + "\n");
	                writer.write(abstractText + "\n");
	                writer.write(keywords + "\n");
	                writer.write(body + "\n");
	                writer.write(references + "\n");
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
	                reader.readLine(); // Skip "END_OF_ARTICLE"

	                String insertSql = "INSERT INTO articles (id, iv, title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	                try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
	                    pstmt.setInt(1, id);
	                    pstmt.setString(2, iv);
	                    pstmt.setString(3, title);
	                    pstmt.setString(4, authors);
	                    pstmt.setString(5, abstractText);
	                    pstmt.setString(6, keywords);
	                    pstmt.setString(7, body);
	                    pstmt.setString(8, references);
	                    pstmt.executeUpdate();
	                }
	            }
	        }
	        System.out.println("Articles restored from " + filename);
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
}