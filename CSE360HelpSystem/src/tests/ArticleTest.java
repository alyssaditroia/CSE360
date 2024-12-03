package tests;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Article;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * {@code ArticleTest} class contains Junit tests for the Article class. 
 * @author Alyssa DiTroia
 */
public class ArticleTest {

    private Article article;
    private static int testcount = 1;
    private static int testsPassed = 1;

    /**
     * Setup for each test includes creating a mock article for testing
     */
    @BeforeEach
    public void setUp() {
		System.out.println("\n =========== ARTICLE TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test Group # %d%n", testcount);
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

        article = new Article(1, title, authors, abstractText, keywords, body, references, level,
                              groupingIdentifiers, permissions, dateAdded, version);

        System.out.println("[Article Test] Setup complete: Article object initialized.");
    }
    @AfterAll
    static void afterAll() {
    	System.out.println("\n =========== ARTICLE TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testsPassed + "\nTESTS PASSED: " + testsPassed + "\n");
    }
    /**
     * A Test that tests the constructor of the article class
     */
    @Test
    public void testFullConstructor() {
        System.out.println("[Article Test] Testing full constructor...");
        System.out.println("\nTESTS IN THIS TEST GROUP: 12\n");

        assertEquals(1, article.getId());
        assertEquals("Sample Title", article.getTitle());
        assertEquals("John Doe", article.getAuthors());
        assertEquals("Sample Abstract", article.getAbstractText());
        assertEquals("keyword1, keyword2", article.getKeywords());
        assertEquals("This is the body of the article.", article.getBody());
        assertEquals("Sample References", article.getReferences());
        assertEquals("beginner", article.getLevel());
        assertEquals(Arrays.asList("Group1", "Group2"), article.getGroupingIdentifiers());
        assertEquals("Admin,Student", article.getPermissions());
        assertNotNull(article.getDateAdded());
        assertEquals("1.0", article.getVersion());

        System.out.println("[Article Test] Full constructor test passed.");
        testsPassed+= 12;
    }

    /**
     * A Test for the partial constructor of the article class
     */
    @Test
    public void testPartialConstructor() {
        System.out.println("[Article Test] Testing partial constructor...");
        System.out.println("\nTESTS IN THIS TEST GROUP: 11\n");

        char[] title = "Another Title".toCharArray();
        char[] authors = "Jane Smith".toCharArray();
        char[] abstractText = "Another Abstract".toCharArray();
        char[] keywords = "keyword3, keyword4".toCharArray();
        char[] body = "Different article body.".toCharArray();
        char[] references = "Different References".toCharArray();
        String level = "intermediate";
        List<String> groupingIdentifiers = Arrays.asList("Group3");
        String permissions = "Instructor";
        Date dateAdded = new Date();
        String version = "2.0";

        Article newArticle = new Article(title, authors, abstractText, keywords, body, references, level,
                                         groupingIdentifiers, permissions, dateAdded, version);

        assertEquals("Another Title", newArticle.getTitle());
        assertEquals("Jane Smith", newArticle.getAuthors());
        assertEquals("Another Abstract", newArticle.getAbstractText());
        assertEquals("keyword3, keyword4", newArticle.getKeywords());
        assertEquals("Different article body.", newArticle.getBody());
        assertEquals("Different References", newArticle.getReferences());
        assertEquals("intermediate", newArticle.getLevel());
        assertEquals(Arrays.asList("Group3"), newArticle.getGroupingIdentifiers());
        assertEquals("Instructor", newArticle.getPermissions());
        assertNotNull(newArticle.getDateAdded());
        assertEquals("2.0", newArticle.getVersion());

        System.out.println("[Article Test] Partial constructor test passed.");
        testsPassed+= 11;
    }

    /**
     * A test for all the setters and getters in the article class
     */
    @Test
    public void testSettersAndGetters() {
        System.out.println("[Article Test] Testing setters and getters...");
        System.out.println("\nTESTS IN THIS TEST GROUP: 13\n");

        article.setId(2);
        assertEquals(2, article.getId());
        System.out.println("[Article Test] ID set and retrieved successfully.");

        char[] newTitle = "Updated Title".toCharArray();
        article.setTitle(newTitle);
        assertEquals("Updated Title", article.getTitle());
        System.out.println("[Article Test] Title set and retrieved successfully.");

        char[] newAuthors = "Updated Author".toCharArray();
        article.setAuthors(newAuthors);
        assertEquals("Updated Author", article.getAuthors());
        System.out.println("[Article Test] Authors set and retrieved successfully.");

        char[] newAbstract = "Updated Abstract".toCharArray();
        article.setAbstractText(newAbstract);
        assertEquals("Updated Abstract", article.getAbstractText());
        System.out.println("[Article Test] Abstract text set and retrieved successfully.");

        char[] newKeywords = "updated, keywords".toCharArray();
        article.setKeywords(newKeywords);
        assertEquals("updated, keywords", article.getKeywords());
        System.out.println("[Article Test] Keywords set and retrieved successfully.");

        char[] newBody = "Updated body content.".toCharArray();
        article.setBody(newBody);
        assertEquals("Updated body content.", article.getBody());
        System.out.println("[Article Test] Body set and retrieved successfully.");

        char[] newReferences = "Updated references".toCharArray();
        article.setReferences(newReferences);
        assertEquals("Updated references", article.getReferences());
        System.out.println("[Article Test] References set and retrieved successfully.");

        article.setLevel("expert");
        assertEquals("expert", article.getLevel());
        System.out.println("[Article Test] Level set and retrieved successfully.");

        article.setGroupingIdentifiers(Arrays.asList("Group4"));
        assertEquals(Arrays.asList("Group4"), article.getGroupingIdentifiers());
        System.out.println("[Article Test] Grouping identifiers set and retrieved successfully.");

        article.setPermissions("Admin,Instructor");
        assertEquals("Admin,Instructor", article.getPermissions());
        System.out.println("[Article Test] Permissions set and retrieved successfully.");

        Date newDate = new Date();
        article.setDateAdded(newDate);
        assertEquals(newDate, article.getDateAdded());
        System.out.println("[Article Test] Date added set and retrieved successfully.");

        article.setVersion("2.1");
        assertEquals("2.1", article.getVersion());
        System.out.println("[Article Test] Version set and retrieved successfully.");
        
        article.setAsPartOfSpecialGroup();
        assertEquals(true, article.checkSpecialGroupArticle());
        System.out.println("[Article Test] special group check completed successfully.");
        testsPassed+= 13;
    }

    /**
     * A test for the search function in the article class
     */
    @Test
    public void testSearchArticle() {
        System.out.println("Testing searchArticle method...");
        System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");

        assertTrue(article.searchArticle("keyword1"));
        System.out.println("[Article Test] Search for 'keyword1' passed.");

        assertTrue(article.searchArticle("Sample Title"));
        System.out.println("[Article Test] Search for 'Sample Title' passed.");

        assertTrue(article.searchArticle("body of the article"));
        System.out.println("[Article Test] Search for 'body of the article' passed.");

        assertFalse(article.searchArticle("nonexistent keyword"));
        System.out.println("[Article Test] Search for 'nonexistent keyword' passed.");

        System.out.println("[Article Test] searchArticle method test passed.");
        testsPassed+= 4;
    }
}

