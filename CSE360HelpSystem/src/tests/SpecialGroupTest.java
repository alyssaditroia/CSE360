package tests;

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

    @BeforeEach
    void setUp() {
		System.out.println("\n =========== SPECIAL GROUP TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test # %d%n", testcount);
        // Set up a new group
        newGroup = new SpecialGroup("Study Group");

        // Set up a preloaded group
        ArrayList<String> members = new ArrayList<>(List.of("user1", "user2"));
        ArrayList<String> admins = new ArrayList<>(List.of("user1"));
        ArrayList<String> articles = new ArrayList<>(List.of("article1", "article2"));
        loadedGroup = new SpecialGroup(1, "Existing Group", members, admins, articles);
    }

    @Test
    void testConstructorNewGroup() {
        assertEquals("Study Group", newGroup.getName());
        assertTrue(newGroup.getGroupMembers().isEmpty());
        assertTrue(newGroup.getGroupAdmins().isEmpty());
        assertTrue(newGroup.getGroupArticles().isEmpty());
        System.out.println("New special group created successfully");
        System.out.println("Special Group Constructor test passed");
    }

    @Test
    void testConstructorLoadedGroup() {
        assertEquals(1, loadedGroup.getGroupId());
        assertEquals("Existing Group", loadedGroup.getName());
        assertEquals(2, loadedGroup.getGroupMembers().size());
        assertEquals(1, loadedGroup.getGroupAdmins().size());
        assertEquals(2, loadedGroup.getGroupArticles().size());
        System.out.println("New special group created successfully");
        System.out.println("Special Group Loaded Constructor test passed");
    }

    @Test
    void testSetGroupId() {
        newGroup.setGroupId(10);
        assertEquals(10, newGroup.getGroupId());
        System.out.println("Special Group set ID test passed");
    }

    @Test
    void testSetName() {
        newGroup.setName("New Study Group");
        assertEquals("New Study Group", newGroup.getName());
        System.out.println("Special Group set Name test passed");
    }

    @Test
    void testAddMember() {
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add Member test passed");
    }

    @Test
    void testAddDuplicateMember() {
        newGroup.addMember("user3");
        newGroup.addMember("user3");
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add duplicate member test passed");
    }

    @Test
    void testAddAdmin() {
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertTrue(newGroup.isMember("admin1")); // Admins should also be members
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add admin test passed");
    }

    @Test
    void testAddDuplicateAdmin() {
        newGroup.addAdmin("admin1");
        newGroup.addAdmin("admin1");
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
        System.out.println("Special Group add duplicate admin test passed");
    }

    @Test
    void testRemoveMember() {
        newGroup.addMember("user3");
        newGroup.addAdmin("admin1");
        newGroup.removeMember("user3");
        assertFalse(newGroup.isMember("user3"));
        assertEquals(1, newGroup.getGroupMembers().size());
        newGroup.removeMember("admin1");
        assertFalse(newGroup.isAdmin("admin1"));
        assertEquals(0, newGroup.getGroupAdmins().size());
        System.out.println("Special Group remove member test passed");
    }

    @Test
    void testAddArticle() {
        newGroup.addArticle("article1");
        assertTrue(newGroup.getGroupArticles().contains("article1"));
        assertEquals(1, newGroup.getGroupArticles().size());
        System.out.println("Special Group add article test passed");
    }

    @Test
    void testAddDuplicateArticle() {
        newGroup.addArticle("article1");
        newGroup.addArticle("article1");
        assertEquals(1, newGroup.getGroupArticles().size());
        System.out.println("Special Group add duplicate article test passed");
    }

    @Test
    void testRemoveArticle() {
        newGroup.addArticle("article1");
        newGroup.removeArticle("article1");
        assertFalse(newGroup.getGroupArticles().contains("article1"));
        assertEquals(0, newGroup.getGroupArticles().size());
        System.out.println("Special Group remove article test passed");
    }

    @Test
    void testIsAdmin() {
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertFalse(newGroup.isAdmin("user3"));
        System.out.println("Special Group is Admin test passed");
    }

    @Test
    void testIsMember() {
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertFalse(newGroup.isMember("user4"));
        System.out.println("Special Group is member test passed");
    }
}
