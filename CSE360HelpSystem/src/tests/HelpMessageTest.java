package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.HelpMessage;

class HelpMessageTest {

    private HelpMessage helpMessage;

    @BeforeEach
    void setUp() {
        // Initialize a HelpMessage object before each test
        helpMessage = new HelpMessage("Need help finding articles", true, 101);
        helpMessage.setMessageId(1);
    }

    @Test
    void testConstructorInitialization() {
        assertEquals("Need help finding articles", helpMessage.getMessageBody());
        assertTrue(helpMessage.getIsSpecificMessage());
        assertEquals(101, helpMessage.getUserId());
        assertNotNull(helpMessage.getSearchRequests());
        assertTrue(helpMessage.getSearchRequests().isEmpty());
        assertEquals(1, helpMessage.getMessageId());
    }

    @Test
    void testSettersAndGetters() {
        helpMessage.setMessageBody("New message body");
        assertEquals("New message body", helpMessage.getMessageBody());

        helpMessage.setIsSpecificMessage(false);
        assertFalse(helpMessage.getIsSpecificMessage());

        ArrayList<String> newSearchRequests = new ArrayList<>();
        newSearchRequests.add("Java");
        newSearchRequests.add("JUnit");
        helpMessage.setSearchRequests(newSearchRequests);
        assertEquals(2, helpMessage.getSearchRequests().size());
        assertTrue(helpMessage.getSearchRequests().contains("Java"));

        helpMessage.setUserId(202);
        assertEquals(202, helpMessage.getUserId());

        helpMessage.setMessageId(2);
        assertEquals(2, helpMessage.getMessageId());
    }

    @Test
    void testSearchRequestsManipulation() {
        helpMessage.getSearchRequests().add("Security");
        assertEquals(1, helpMessage.getSearchRequests().size());
        assertTrue(helpMessage.getSearchRequests().contains("Security"));
    }

    @Test
    void testEdgeCases() {
        helpMessage.setMessageBody(null);
        assertNull(helpMessage.getMessageBody());

        helpMessage.setMessageBody("");
        assertEquals("", helpMessage.getMessageBody());

        helpMessage.setIsSpecificMessage(null);
        assertNull(helpMessage.getIsSpecificMessage());
    }
}

