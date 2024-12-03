package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import models.SpecialGroup;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecialGroupPermissionsTest {
    private SpecialGroup specialGroup1;
    private SpecialGroup specialGroup2;
    private static int testCount = 0;
    private static int testPass = 0;

    @BeforeEach
    void setUp() {
    	System.out.println("\n =========== SPECIAL GROUP PERMISSIONS TEST ===========\n");
        testCount++;
        System.out.printf("Test Group #%d%n", testCount);

        // Initialize special groups
        specialGroup1 = new SpecialGroup(1, "SpecialGroup1", 
            new ArrayList<>(List.of("user1", "user2")), 
            new ArrayList<>(List.of("user1")), 
            new ArrayList<>(List.of("article1", "article2"))
        );

        specialGroup2 = new SpecialGroup(2, "SpecialGroup2", 
            new ArrayList<>(List.of("user3")), 
            new ArrayList<>(List.of("user3")), 
            new ArrayList<>(List.of("article3", "article4"))
        );
    }
    
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== SPECIAL GROUP PERMISSIONS TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}

    @Test
    void testCreateSpecialGroupByUser1() {
    	System.out.println("\nTesting Creation of a Special Group And User Special Group Permissions");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        assertEquals("SpecialGroup1", specialGroup1.getName());
        assertTrue(specialGroup1.isAdmin("user1"));
        System.out.println("Verified: SpecialGroup1 created by user1 with admin permissions.");
        System.out.println();
        testPass+= 2;
    }

    @Test
    void testCreateSpecialGroupByUser3() {
    	System.out.println("\nTesting Creation of a Special Group And User Special Group Permissions");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        assertEquals("SpecialGroup2", specialGroup2.getName());
        assertTrue(specialGroup2.isAdmin("user3"));
        System.out.println("Verified: SpecialGroup2 created by user3 with admin permissions.");
        System.out.println();
        testPass+= 2; 
    }

    @Test
    void testArticlesInSpecialGroup1() {
    	System.out.println("\nTesting User's Special Group Article Permissions");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        List<String> articles = specialGroup1.getGroupArticles();
        assertEquals(2, articles.size());
        assertTrue(articles.contains("article1"));
        assertTrue(articles.contains("article2"));
        System.out.println("Verified: Articles tied to SpecialGroup1 are correct.");
        System.out.println();
        testPass+= 3; 
    }

    @Test
    void testArticlesInSpecialGroup2() {
    	System.out.println("\nTesting User's Special Group Article Permissions");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        List<String> articles = specialGroup2.getGroupArticles();
        assertEquals(2, articles.size());
        assertTrue(articles.contains("article3"));
        assertTrue(articles.contains("article4"));
        System.out.println("Verified: Articles tied to SpecialGroup2 are correct.");
        System.out.println();
        testPass+= 3; 
    }

    @Test
    void testAddArticleToSpecialGroup2() {
    	System.out.println("\nTesting Add Special Group Article");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        specialGroup2.addArticle("article5");
        List<String> articles = specialGroup2.getGroupArticles();
        assertTrue(articles.contains("article5"));
        assertEquals(3, articles.size());
        System.out.println("Verified: Additional article added to SpecialGroup2 successfully.");
        System.out.println();
        testPass+= 2; 
    }

    @Test
    void testUsersInSpecialGroup1() {
    	System.out.println("\nTesting Users in a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        List<String> members = specialGroup1.getGroupMembers();
        assertEquals(2, members.size());
        assertTrue(members.contains("user1"));
        assertTrue(members.contains("user2"));
        System.out.println("Verified: Users tied to SpecialGroup1 are correct.");
        System.out.println();
        testPass+= 3; 
    }

    @Test
    void testUsersInSpecialGroup2() {
    	System.out.println("\nTesting Users in a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        List<String> members = specialGroup2.getGroupMembers();
        assertEquals(1, members.size());
        assertTrue(members.contains("user3"));
        System.out.println("Verified: Users tied to SpecialGroup2 are correct.");
        System.out.println();
        testPass+= 2; 
    }

    @Test
    void testAddUserToSpecialGroup2() {
    	System.out.println("\nTesting Adding Users to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        specialGroup2.addMember("user4");
        List<String> members = specialGroup2.getGroupMembers();
        assertTrue(members.contains("user4"));
        assertEquals(2, members.size());
        System.out.println("Verified: Additional user added to SpecialGroup2 successfully.");
        testPass+= 2; 
    }
    
    @Test
    void testAddUserToSpecialGroup1() {
    	System.out.println("\nTesting Adding Users to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        specialGroup1.addMember("user5");
        List<String> members = specialGroup1.getGroupMembers();
        assertTrue(members.contains("user5"));
        assertEquals(3, members.size());
        System.out.println("Verified: Additional user added to SpecialGroup1 successfully.");
        testPass+= 2; 
    }
}

