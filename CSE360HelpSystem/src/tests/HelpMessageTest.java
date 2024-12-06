package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.HelpMessage;

class HelpMessageTest {

    private HelpMessage helpMessage;
    private static int testcount = 1;
    private static int testPass = 0;

    @BeforeEach
    void setUp() {
		System.out.println("\n =========== HELP MESSAGE TEST ===========\n");
		System.out.printf("Test Group # %d%n", testcount++);
        // Initialize a HelpMessage object before each test
        helpMessage = new HelpMessage("Need help finding articles", true, 101);
        helpMessage.setMessageId(1);
    }
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== HELP MESSAGE TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}

    @Test
    void testConstructorInitialization() {
    	System.out.println("\nTesting Help Message Constructor");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 6\n");
        assertEquals("Need help finding articles", helpMessage.getMessageBody());
        assertTrue(helpMessage.getIsSpecificMessage());
        assertEquals(101, helpMessage.getUserId());
        assertNotNull(helpMessage.getSearchRequests());
        assertTrue(helpMessage.getSearchRequests().isEmpty());
        assertEquals(1, helpMessage.getMessageId());
        testPass+= 6; 
    }

    @Test
    void testSettersAndGetters() {
    	System.out.println("\nTesting Help Message Getters and Setters");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 6\n");
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
        testPass+= 6;
    }

    @Test
    void testSearchRequestsManipulation() {
    	System.out.println("\nTesting Search History Availibility");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        helpMessage.getSearchRequests().add("Security");
        assertEquals(1, helpMessage.getSearchRequests().size());
        assertTrue(helpMessage.getSearchRequests().contains("Security"));
        testPass+= 2;
    }

    @Test
    void testEdgeCases() {
    	System.out.println("\nTesting Help Messaging Edge Cases");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        helpMessage.setMessageBody(null);
        assertNull(helpMessage.getMessageBody());

        helpMessage.setMessageBody("");
        assertEquals("", helpMessage.getMessageBody());

        helpMessage.setIsSpecificMessage(null);
        assertNull(helpMessage.getIsSpecificMessage());
        testPass+= 3;
    }
}

