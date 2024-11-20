package tests;

import models.Article;
import models.UserSession;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the user session class using JUnit tests
 * 
 *  @author Alyssa DiTroia
 *  @author Cooper Anderson
 */
public class UserSessionTest {

    private UserSession userSession;
    private static int testcount = 0;
    /**
     * Setup for each test includes getting a user instance and setting the session user and article to null
     */
    @BeforeEach
    public void setUp() {
		System.out.println("\n =========== USER SESSION TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test # %d%n", testcount);
        System.out.println("Setting up UserSession instance for tests...");
        userSession = UserSession.getInstance();
        userSession.setCurrentUser(null); // Reset the user for each test
        userSession.setSelectedArticle(null); // Reset the selected article for each test
        System.out.println("Setup complete.");
    }

    /**
     * Tests an instance of the userSession Instance
     */
    @Test
    public void testSingletonInstance() {
        System.out.println("Testing singleton instance...");
        UserSession anotherInstance = UserSession.getInstance();
        assertSame(userSession, anotherInstance);
        System.out.println("Singleton instance test passed.");
    }

    /**
     * Tests the set and get invite code for the specific instance
     */
    @Test
    public void testSetAndGetInviteCode() {
        System.out.println("Testing set and get invite code...");

        userSession.setInviteCode("INVITE123");
        assertEquals("INVITE123", userSession.getInviteCode());

        System.out.println("Invite code set and retrieved successfully.");
    }

    /**
     * Tests the set and get email for the specific instance
     */
    @Test
    public void testSetAndGetEmail() {
        System.out.println("Testing set and get email...");

        userSession.setEmail("test@example.com");
        assertEquals("test@example.com", userSession.getEmail());

        System.out.println("Email set and retrieved successfully.");
    }

    /**
     * Tests the set and get current role for the specific instance
     */
    @Test
    public void testSetAndGetCurrentRole() {
        System.out.println("Testing set and get current role...");

        userSession.setCurrentRole("Admin");
        assertEquals("Admin", userSession.getCurrentRole());

        System.out.println("Current role set and retrieved successfully.");
    }

    /**
     * Tests the set and get selected article for the specific instance
     */
    @Test
    public void testSetAndGetSelectedArticle() {
        System.out.println("Testing set and get selected article...");

        char[] title = "Sample Title".toCharArray();
        char[] authors = "John Doe".toCharArray();
        char[] abstractText = "Sample Abstract".toCharArray();
        char[] keywords = "keyword1, keyword2".toCharArray();
        char[] body = "This is the body of the article.".toCharArray();
        char[] references = "Sample References".toCharArray();
        String level = "beginner";
        List<String> groupingIdentifiers = Arrays.asList("Group1", "Group2");
        String permissions = "Admin,Student";
        Date dateAdded = new Date();
        String version = "1.0";

        Article article = new Article(1, title, authors, abstractText, keywords, body, references, level,
                              groupingIdentifiers, permissions, dateAdded, version);

        System.out.println("Setup complete: Article object initialized.");

        userSession.setSelectedArticle(article);
        assertEquals(article, userSession.getSelectedArticle());

        System.out.println("Selected article set and retrieved successfully.");
    }

    /**
     * Tests the get user session when it is not set, should trigger null
     */
    @Test
    public void testGetCurrentUserWhenNotSet() {
        System.out.println("Testing get current user when user is not set...");

        assertNull(userSession.getCurrentUser());

        System.out.println("Current user is null as expected.");
    }


    /**
     * Tests setting the current instance
     */
    @Test
    public void testSetInstance() {
        System.out.println("Testing setInstance method...");

        UserSession newUserSession = UserSession.getInstance();
        UserSession.setInstance(newUserSession);

        assertSame(newUserSession, UserSession.getInstance());

        System.out.println("Instance set and retrieved successfully.");
    }
}

