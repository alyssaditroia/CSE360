package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.SpecialGroup;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecialGroupTest {
    private SpecialGroup newGroup;
    private SpecialGroup loadedGroup;
    private static int testcount = 0;
    private static int testPass = 0; 

    @BeforeEach
    void setUp() {
		System.out.println("\n =========== SPECIAL GROUP TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test Group # %d%n", testcount);
        // Set up a new group
        newGroup = new SpecialGroup("Study Group");

        // Set up a preloaded group
        ArrayList<String> members = new ArrayList<>(List.of("user1", "user2"));
        ArrayList<String> admins = new ArrayList<>(List.of("user1"));
        ArrayList<String> articles = new ArrayList<>(List.of("article1", "article2"));
        loadedGroup = new SpecialGroup(1, "Existing Group", members, admins, articles);
    }

	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== SPECIAL GROUP TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}
	
    @Test
    void testConstructorNewGroup() {
    	System.out.println("\nTesting Constructor for a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");
        assertEquals("Study Group", newGroup.getName());
        assertTrue(newGroup.getGroupMembers().isEmpty());
        assertTrue(newGroup.getGroupAdmins().isEmpty());
        assertTrue(newGroup.getGroupArticles().isEmpty());
        System.out.println("New special group created successfully");
        System.out.println("Special Group Constructor test passed");
        testPass+= 4; 
    }

    @Test
    void testConstructorLoadedGroup() {
    	System.out.println("\nTesting Loaded Constructor for a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 5\n");
        assertEquals(1, loadedGroup.getGroupId());
        assertEquals("Existing Group", loadedGroup.getName());
        assertEquals(2, loadedGroup.getGroupMembers().size());
        assertEquals(1, loadedGroup.getGroupAdmins().size());
        assertEquals(2, loadedGroup.getGroupArticles().size());
        System.out.println("New special group created successfully");
        System.out.println("Special Group Loaded Constructor test passed");
        testPass+= 5; 
    }

    @Test
    void testSetGroupId() {
    	System.out.println("\nTesting Set Group ID for Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        newGroup.setGroupId(10);
        assertEquals(10, newGroup.getGroupId());
        System.out.println("Special Group set ID test passed");
        testPass++;
    }

    @Test
    void testSetName() {
    	System.out.println("\nTesting Set Name for Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        newGroup.setName("New Study Group");
        assertEquals("New Study Group", newGroup.getName());
        System.out.println("Special Group set Name test passed");
        testPass++;
    }

    @Test
    void testAddMember() {
    	System.out.println("\nTesting Add Member to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add Member test passed");
        testPass+= 2; 
    }

    @Test
    void testAddDuplicateMember() {
    	System.out.println("\nTesting Add Duplicate Member to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        newGroup.addMember("user3");
        newGroup.addMember("user3");
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add duplicate member test passed");
        testPass+= 1;
    }

    @Test
    void testAddAdmin() {
    	System.out.println("\nTesting Add Admin to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertTrue(newGroup.isMember("admin1"));
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add admin test passed");
        testPass+= 4; 
    }

    @Test
    void testAddDuplicateAdmin() {
    	System.out.println("\nTesting Add Duplicate Admin to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addAdmin("admin1");
        newGroup.addAdmin("admin1");
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add duplicate admin test passed");
        testPass+= 2; 
    }

    @Test
    void testRemoveMember() {
    	System.out.println("\nTesting Remove Member From a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");
        newGroup.addMember("user3");
        newGroup.addAdmin("admin1");
        newGroup.removeMember("user3");
        assertFalse(newGroup.isMember("user3"));
        assertEquals(1, newGroup.getGroupMembers().size());
        newGroup.removeMember("admin1");
        assertFalse(newGroup.isAdmin("admin1"));
        assertEquals(0, newGroup.getGroupAdmins().size());
        System.out.println("Special Group remove member test passed");
        testPass+= 4; 
    }

    @Test
    void testAddArticle() {
    	System.out.println("\nTesting Add Article to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addArticle("article1");
        assertTrue(newGroup.getGroupArticles().contains("article1"));
        assertEquals(1, newGroup.getGroupArticles().size());
        System.out.println("Special Group add article test passed");
        testPass+= 2; 
    }

    @Test
    void testAddDuplicateArticle() {
    	System.out.println("\nTesting Add Duplicate Article to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        newGroup.addArticle("article1");
        newGroup.addArticle("article1");
        assertEquals(1, newGroup.getGroupArticles().size());
        System.out.println("Special Group add duplicate article test passed");
        testPass++; 
    }

    @Test
    void testRemoveArticle() {
    	System.out.println("\nTesting Remove Article from a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addArticle("article1");
        newGroup.removeArticle("article1");
        assertFalse(newGroup.getGroupArticles().contains("article1"));
        assertEquals(0, newGroup.getGroupArticles().size());
        System.out.println("Special Group remove article test passed");
        testPass+= 2; 
    }

    @Test
    void testIsAdmin() {
    	System.out.println("\nTesting Admin Access to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertFalse(newGroup.isAdmin("user3"));
        System.out.println("Special Group is Admin test passed");
        testPass+= 2; 
    }

    @Test
    void testIsMember() {
       	System.out.println("\nTesting Member Access to a Special Group");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertFalse(newGroup.isMember("user4"));
        System.out.println("Special Group is member test passed");
        testPass+= 2; 
    }
}
