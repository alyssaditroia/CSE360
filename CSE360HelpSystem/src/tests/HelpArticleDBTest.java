package tests;


import database.Database;
import database.HelpArticleDatabase;
import models.Article;
import org.junit.jupiter.api.*;
import Encryption.EncryptionUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HelpArticleDatabase
 * Tests functionality for managing articles and grouping identifiers in the database.
 * 
 * @author Alyssa
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelpArticleDBTest {

    private HelpArticleDatabase helpArticleDatabase;
    private Database database;
    private Connection connection;
    private Article article;
    private static int testcount = 1;
    private static int testPass = 0;


    @BeforeEach
    void clearDatabase() throws SQLException {
		Database database = Database.getInstance();
		database.connectToDatabase();
		connection = database.getConnection();
		clearTestDatabase(); 
		database.createTables();
		try {
			helpArticleDatabase = new HelpArticleDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Clear articles and grouping identifiers before each test
        clearArticles();
        clearGroupingIdentifiers();
        helpArticleDatabase.createArticleTables();
        System.out.println("\n =========== HELP ARTICLE DATABASE TEST ===========\n");
        System.out.printf("Test Group # %d%n", testcount++);
    }

	@AfterEach
	void tearDown() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				System.out.println("Database Connection Closed");
			}
		} catch (SQLException e) {
			System.err.println("[HelpArticleDatabaseTest] Error closing the connection: " + e.getMessage());
		}
	}

	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== HELP ARTICLE DATABASE TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
		deleteTestDatabase();
	}
	
    @Test
    void createArticle() throws Exception {
    	System.out.println("\nTesting Article Creation In Database");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
    	
        char[] title = "Sample Title".toCharArray();
        char[] authors = "John Doe".toCharArray();
        char[] abstractText = "Sample Abstract".toCharArray();
        char[] keywords = "keyword1, keyword2".toCharArray();
        char[] body = "This is the body of the article.".toCharArray();
        char[] references = "Sample References".toCharArray();
        String level = "beginner";
        List<String> groupingIdentifiers = Arrays.asList("Group1", "Group2");
        String permissions = "Admin,Student";
        Date dateAdded = new Date(0);
        String version = "1.0";

        article = new Article(1, title, authors, abstractText, keywords, body, references, level,
                              groupingIdentifiers, permissions, dateAdded, version);
        helpArticleDatabase.createArticle(article);
        assertFalse(helpArticleDatabase.areArticlesEmpty());
        helpArticleDatabase.updateArticle(article);
        testPass++;
    }
    /**
     * Tests creating and retrieving an article.
     */
    @Test
    void testCreateAndRetrieveArticle() throws Exception {
    	System.out.println("\nTesting Article Creation And Retrieval In Database");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 14\n");
        char[] title = "Sample Title".toCharArray();
        char[] authors = "John Doe".toCharArray();
        char[] abstractText = "Abstract for the article".toCharArray();
        char[] keywords = "sample, article".toCharArray();
        char[] body = "This is the body of the article".toCharArray();
        char[] references = "Reference 1, Reference 2".toCharArray();
        String level = "beginner";
        List<String> groupingIdentifiers = List.of("Education", "Java");
        String permissions = "public";
        Date dateAdded = new Date(System.currentTimeMillis());
        String version = "1.0";

        // Create the article
        helpArticleDatabase.createArticle(title, authors, abstractText, keywords, body, references, level, groupingIdentifiers, permissions, dateAdded, version);

        // Retrieve all articles
        List<Article> articles = helpArticleDatabase.getAllDecryptedArticles();

        // Validate the article
        assertEquals(1, articles.size(), "There should be exactly one article in the database");
        Article article = articles.get(0);
        
        List<Article>searchedArticles = helpArticleDatabase.searchArticles("Sample Title");
        Article searched = searchedArticles.get(0);
        assertEquals("Sample Title", new String(searched.getTitle()), "Search did not provide proper results");
        assertEquals("Sample Title", new String(article.getTitle()), "Title does not match");
        assertEquals("John Doe", new String(article.getAuthors()), "Authors do not match");
        assertEquals("Abstract for the article", new String(article.getAbstractText()), "Abstract does not match");
        assertEquals("sample, article", new String(article.getKeywords()), "Keywords do not match");
        assertEquals("This is the body of the article", new String(article.getBody()), "Body does not match");
        assertEquals("Reference 1, Reference 2", new String(article.getReferences()), "References do not match");
        assertEquals(level, article.getLevel(), "Level does not match");
        assertEquals(groupingIdentifiers, article.getGroupingIdentifiers(), "Grouping Identifiers do not match");
        assertEquals(permissions, article.getPermissions(), "Permissions do not match");
        assertEquals(dateAdded.toString(), article.getDateAdded().toString(), "Date added does not match");
        assertEquals(version, article.getVersion(), "Version does not match");
        helpArticleDatabase.deleteArticle(0);
        assertFalse(helpArticleDatabase.areArticlesEmpty());
        helpArticleDatabase.listArticles();
        testPass+= 14;
    }

    /**
     * Tests creating and retrieving grouping identifiers.
     */
    @Test
    void testGroupingIdentifiers() throws SQLException {
    	System.out.println("\nTesting Article Grouping Identifiers In Database");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        // Insert grouping identifiers
        helpArticleDatabase.insertGroupingIdentifier("Education");
        helpArticleDatabase.insertGroupingIdentifier("Technology");

        // Fetch grouping identifiers
        List<String> identifiers = helpArticleDatabase.fetchGroupingIdentifiers();

        // Validate identifiers
        assertTrue(identifiers.contains("Education"), "Education should be present in grouping identifiers");
        assertTrue(identifiers.contains("Technology"), "Technology should be present in grouping identifiers");
        assertEquals(2, identifiers.size(), "There should be exactly 2 grouping identifiers");
        testPass+= 3;
    }

    /**
     * Tests the backup and restore functionality for articles.
     */
    @Test
    void testBackupAndRestoreArticles() throws Exception {
    	System.out.println("\nTesting Article Backup and Restore");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        // Setup an article
        char[] title = "Backup Article".toCharArray();
        char[] authors = "Author".toCharArray();
        char[] body = "Body of backup article".toCharArray();
        Date dateAdded = new Date(System.currentTimeMillis());
        List<String> groupingIdentifiers = List.of("Backup");
        String level = "intermediate";
        String permissions = "private";
        String version = "1.0";
        char[] none = "none".toCharArray();

        // Create an article
        helpArticleDatabase.createArticle(title, authors, none, none, body, none, level, groupingIdentifiers, permissions, dateAdded, version);

        // Backup articles to a file
        String backupFile = "articles_backup.txt";
        helpArticleDatabase.backupArticles(backupFile);

        // Clear the articles from the database
        clearArticles();

        // Ensure articles are empty
        assertTrue(helpArticleDatabase.areArticlesEmpty(), "Articles table should be empty after clearing");
        testPass++;

    }

    /**
     * Tests filtering articles by difficulty level.
     */
    @Test
    void testFilterArticlesByLevel() throws Exception {
    	System.out.println("\nTesting Article Level Filter From Database");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        char[] none = "none".toCharArray();

        // Setup articles
        helpArticleDatabase.createArticle("Beginner Article".toCharArray(), none, none, none, none, none, "beginner", new ArrayList<>(), "public", new Date(System.currentTimeMillis()), "1.0");
        helpArticleDatabase.createArticle("Advanced Article".toCharArray(), none, none, none, none, none, "advanced", new ArrayList<>(), "public", new Date(System.currentTimeMillis()), "1.0");

        // Validate that articles are created
        List<Article> allArticles = helpArticleDatabase.getAllDecryptedArticles();
        assertEquals(2, allArticles.size(), "There should be exactly 2 articles in the database");

        // Filter articles by "beginner" level
        List<Article> beginnerArticles = helpArticleDatabase.filterArticlesByLevel("beginner");

        // Validate filtering
        assertEquals(1, beginnerArticles.size(), "There should be exactly one beginner-level article");
        assertEquals("Beginner Article", new String(beginnerArticles.get(0).getTitle()), "Beginner article title does not match");
        testPass+= 3;
    }

    /**
     * Clears all articles from the articles table.
     * 
     * @throws SQLException if a database access error occurs
     */
    public void clearArticles() throws SQLException {
        String sql = "DELETE FROM articles";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[HelpArticleDB] All articles cleared from test database.");
        }
    }

    /**
     * Clears all grouping identifiers from the GroupingIdentifiers table.
     * 
     * @throws SQLException if a database access error occurs
     */
    public void clearGroupingIdentifiers() throws SQLException {
        String sql = "DELETE FROM GroupingIdentifiers";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[HelpArticleDB] All test grouping identifiers cleared from the database.");
        }
    }
    /**
     * Clears TestDatabase table.
     * 
     * @throws SQLException if a database access error occurs
     */
	private void clearTestDatabase() throws SQLException {
		// Clear the database (truncate or delete rows) before each test
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("TRUNCATE TABLE cse360users"); 
			System.out.println("[DatabaseTest] Test database cleared.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error clearing test database: " + e.getMessage());
		}
	}
	/**
	 * Deletes the test database after testing
	 **/
	private static void deleteTestDatabase() {
		String url = "jdbc:h2:~/CSE360HelpTest";
		String user = "user"; // Default user for H2
		String password = ""; // Default password for H2 (empty)

		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
			stmt.execute("DROP ALL OBJECTS");
			System.out.println("[DatabaseTest] Test database deleted successfully.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error deleting test database: " + e.getMessage());
		}
	}

}

