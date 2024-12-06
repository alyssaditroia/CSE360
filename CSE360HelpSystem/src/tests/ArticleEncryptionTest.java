package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import database.Database;
import database.HelpArticleDatabase;

public class ArticleEncryptionTest {
    private Database database;
    private HelpArticleDatabase had;
    private Connection connection;
    private static int testcount = 1;
    private static int testsPassed = 1;

    @BeforeEach
    void setUp() throws SQLException {
        System.out.println("\n =========== ARTICLE ENCRYPTION TEST ===========\n");
        database = Database.getInstance();
        database.connectToDatabase();
        connection = database.getConnection();
        try {
			had = new HelpArticleDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
        had.createArticleTables();
        had.createGroupingIdentifiersTable();
    }

    @AfterEach
    void tearDown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database Connection Closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing the connection: " + e.getMessage());
        }
    }

    @AfterAll
    static void afterAll() {
    	System.out.println("\n =========== ARTICLE ENCRYPTION TEST COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testcount + "\nTESTS PASSED: " + testsPassed + "\n");
        deleteTestDatabase();
    }

    private static void deleteTestDatabase() {
        String url = "jdbc:h2:~/CSE360HelpTest1";
        String user = "user";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            System.out.println("Test database deleted successfully.");
        } catch (SQLException e) {
            System.err.println("[DatabaseTest] Error deleting test database: " + e.getMessage());
        }
    }

    @Test
    void testConnectToDatabase() throws SQLException {
        System.out.printf("\nTest # %d%n", testcount);
        System.out.println("\n ------- Testing Connection -------\n");
        assertNotNull(database.getConnection(), "Connection should not be null after connecting to the database");
        System.out.println(testsPassed + " / " + testcount + " Tests Passed\n");
    }

    @Test
    void testEncryptAndDecryptMultipleArticles() throws Exception {
        had.createArticleTables();
        
        // Input 1
        char[] title1 = "Title1".toCharArray();
        char[] body1 = "Body1".toCharArray();
        char[] metadata1 = "Author: Alyssa, Timestamp: 2024-11-25, GroupID: 1".toCharArray();
        char[] abstract1 = "Abstract: 123".toCharArray();
        char[] keywords1 = "special,groups,sharing".toCharArray();
        char[] references1 = "none".toCharArray();
        had.createArticle(title1, metadata1, abstract1, keywords1, body1, references1, "Intermediate",
                List.of("Group1"), "Admin", new java.sql.Date(new Date().getTime()), "1.0");

        // Input 2
        char[] title2 = "Title2".toCharArray();
        char[] body2 = "Body2".toCharArray();
        char[] metadata2 = "Author: Alyssa, Timestamp: 2024-11-25, GroupID: 2".toCharArray();
        char[] abstract2 = "Abstract: 345".toCharArray();
        char[] keywords2 = "cryptography,encryption,security".toCharArray();
        char[] references2 = "none".toCharArray();
        had.createArticle(title2, metadata2, abstract2, keywords2, body2, references2, "Expert",
                List.of("Group2"), "Student", new java.sql.Date(new Date().getTime()), "1.1");

        // Input 3
        char[] title3 = "Title3".toCharArray();
        char[] body3 = "Body3".toCharArray();
        char[] metadata3 = "Author: Alyssa, Timestamp: 2024-11-25, GroupID: 3".toCharArray();
        char[] abstract3 = "Abstract: 456".toCharArray();
        char[] keywords3 = "data,security,information".toCharArray();
        char[] references3 = "none".toCharArray();
        had.createArticle(title3, metadata3, abstract3, keywords3, body3, references3, "Beginner",
                List.of("Group3"), "Instructor",new java.sql.Date(new Date().getTime()), "1.2");

        // Validate each article
        validateArticle(1, title1, body1, metadata1);
        validateArticle(2, title2, body2, metadata2);
        validateArticle(3, title3, body3, metadata3);
    }

    private void validateArticle(int id, char[] expectedTitle, char[] expectedBody, char[] expectedMetadata) throws Exception {
        String[] decryptedArticle = had.getDecryptedArticle(id);

        // Assert decrypted content matches the expected plaintext
        System.out.printf("Test # %d%n", (testcount++) + 1);
        System.out.println("\n ------- Testing Encryption and Decryption for Articles -------\n");
        assertEquals(new String(expectedTitle), decryptedArticle[0], "Title should match after decryption");
        System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
        
        System.out.printf("Test # %d%n", (testcount++) + 1);
        System.out.println("\n ------- Testing Encryption and Decryption for Articles -------\n");
        assertEquals(new String(expectedMetadata), decryptedArticle[1], "Metadata should match after decryption");
        System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
        
        System.out.printf("Test # %d%n", (testcount++) + 1);
        System.out.println("\n ------- Testing Encryption and Decryption for Articles -------\n");
        assertEquals(new String(expectedBody), decryptedArticle[4], "Body should match after decryption");
        System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");

        // Fetch raw data and ensure encryption
        String sql = "SELECT * FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String ivBase64 = rs.getString("iv");
                    String encryptedTitle = rs.getString("title");
                    String encryptedBody = rs.getString("body");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article IV -------\n");
                    assertNotNull(ivBase64, "IV should not be null");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article IV -------\n");
                    assertFalse(ivBase64.isEmpty(), "IV should not be empty");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article Title -------\n");
                    assertNotNull(encryptedTitle, "Encrypted title should not be null");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article Title -------\n");
                    assertFalse(encryptedTitle.isEmpty(), "Encrypted title should not be empty");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article IV -------\n");
                    assertNotEquals(new String(expectedTitle), new String(Base64.getDecoder().decode(encryptedTitle)),
                            "Encrypted title should be different from plaintext title");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article Contents -------\n");
                    assertNotNull(encryptedBody, "Encrypted body should not be null");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article Contents -------\n");
                    assertFalse(encryptedBody.isEmpty(), "Encrypted body should not be empty");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                    System.out.printf("Test # %d%n", (testcount++) + 1);
                    System.out.println("\n ------- Testing Encryption Article Contents -------\n");
                    assertNotEquals(new String(expectedBody), new String(Base64.getDecoder().decode(encryptedBody)),
                            "Encrypted body should be different from plaintext body");
                    System.out.println(++testsPassed + " / " + testcount + " Tests Passed\n");
                    
                } else {
                    fail("Article with ID " + id + " not found in the database");
                }
            }
        }
    }
}

