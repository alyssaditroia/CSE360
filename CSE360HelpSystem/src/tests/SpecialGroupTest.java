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

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testConstructorLoadedGroup() {
        assertEquals(1, loadedGroup.getGroupId());
        assertEquals("Existing Group", loadedGroup.getName());
        assertEquals(2, loadedGroup.getGroupMembers().size());
        assertEquals(1, loadedGroup.getGroupAdmins().size());
        assertEquals(2, loadedGroup.getGroupArticles().size());
    }

    @Test
    void testSetGroupId() {
        newGroup.setGroupId(10);
        assertEquals(10, newGroup.getGroupId());
    }

    @Test
    void testSetName() {
        newGroup.setName("New Study Group");
        assertEquals("New Study Group", newGroup.getName());
    }

    @Test
    void testAddMember() {
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertEquals(1, newGroup.getGroupMembers().size());
    }

    @Test
    void testAddDuplicateMember() {
        newGroup.addMember("user3");
        newGroup.addMember("user3");
        assertEquals(1, newGroup.getGroupMembers().size());
    }

    @Test
    void testAddAdmin() {
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertTrue(newGroup.isMember("admin1")); // Admins should also be members
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
    }

    @Test
    void testAddDuplicateAdmin() {
        newGroup.addAdmin("admin1");
        newGroup.addAdmin("admin1");
        assertEquals(1, newGroup.getGroupAdmins().size());
        assertEquals(1, newGroup.getGroupMembers().size());
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
    }

    @Test
    void testAddArticle() {
        newGroup.addArticle("article1");
        assertTrue(newGroup.getGroupArticles().contains("article1"));
        assertEquals(1, newGroup.getGroupArticles().size());
    }

    @Test
    void testAddDuplicateArticle() {
        newGroup.addArticle("article1");
        newGroup.addArticle("article1");
        assertEquals(1, newGroup.getGroupArticles().size());
    }

    @Test
    void testRemoveArticle() {
        newGroup.addArticle("article1");
        newGroup.removeArticle("article1");
        assertFalse(newGroup.getGroupArticles().contains("article1"));
        assertEquals(0, newGroup.getGroupArticles().size());
    }

    @Test
    void testIsAdmin() {
        newGroup.addAdmin("admin1");
        assertTrue(newGroup.isAdmin("admin1"));
        assertFalse(newGroup.isAdmin("user3"));
    }

    @Test
    void testIsMember() {
        newGroup.addMember("user3");
        assertTrue(newGroup.isMember("user3"));
        assertFalse(newGroup.isMember("user4"));
    }
}
