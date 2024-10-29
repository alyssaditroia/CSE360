package tests;

import models.Article;
import models.UserSession;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class UserSessionTest {

    private UserSession userSession;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up UserSession instance for tests...");
        userSession = UserSession.getInstance();
        userSession.setCurrentUser(null); // Reset the user for each test
        userSession.setSelectedArticle(null); // Reset the selected article for each test
        System.out.println("Setup complete.");
    }

    @Test
    public void testSingletonInstance() {
        System.out.println("Testing singleton instance...");
        UserSession anotherInstance = UserSession.getInstance();
        assertSame(userSession, anotherInstance);
        System.out.println("Singleton instance test passed.");
    }

    @Test
    public void testSetAndGetInviteCode() {
        System.out.println("Testing set and get invite code...");

        userSession.setInviteCode("INVITE123");
        assertEquals("INVITE123", userSession.getInviteCode());

        System.out.println("Invite code set and retrieved successfully.");
    }

    @Test
    public void testSetAndGetEmail() {
        System.out.println("Testing set and get email...");

        userSession.setEmail("test@example.com");
        assertEquals("test@example.com", userSession.getEmail());

        System.out.println("Email set and retrieved successfully.");
    }

    @Test
    public void testSetAndGetCurrentRole() {
        System.out.println("Testing set and get current role...");

        userSession.setCurrentRole("Admin");
        assertEquals("Admin", userSession.getCurrentRole());

        System.out.println("Current role set and retrieved successfully.");
    }

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

    @Test
    public void testGetCurrentUserWhenNotSet() {
        System.out.println("Testing get current user when user is not set...");

        assertNull(userSession.getCurrentUser());

        System.out.println("Current user is null as expected.");
    }


    @Test
    public void testSetInstance() {
        System.out.println("Testing setInstance method...");

        UserSession newUserSession = UserSession.getInstance();
        UserSession.setInstance(newUserSession);

        assertSame(newUserSession, UserSession.getInstance());

        System.out.println("Instance set and retrieved successfully.");
    }
}

