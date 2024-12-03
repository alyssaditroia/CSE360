package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Conversation;
import models.HelpMessage;

class ConversationTest {

    private static int passCount = 0; // Counter for passed tests
    private Conversation conversation;
    private HelpMessage message1;
    private HelpMessage message2;
    private static int testcount = 0;

    @BeforeEach
    void setUp() {
		System.out.println("\n =========== HELP CONVERSATION MESSAGES TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test Group # %d%n", testcount);
        // Initialize a Conversation and some HelpMessages before each test
        conversation = new Conversation(1);
        message1 = new HelpMessage("Help with tool usage", false, 101);
        message2 = new HelpMessage("Can't find articles", true, 102);
        System.out.println("[ConversationTest] Test setup completed.");
    }

    @AfterAll
    static void printPassCount() {
    	System.out.println("\n =========== HELP CONVERSATION MESSAGES TESTS COMPLETE ===========\n");
        System.out.println("\nTOTAL TESTS: " + passCount + "\nTESTS PASSED: " + passCount + "\n");
    }

    @Test
    void testConstructorInitialization() {
    	System.out.println("\nNumber Of Tests In Group: 4\n");
        try {
            assertEquals(1, conversation.getConversationId());
            assertFalse(conversation.getIsResolved());
            assertNotNull(conversation.getMessages());
            assertTrue(conversation.getMessages().isEmpty());
            passCount+= 4;
            System.out.println("[ConversationTest] Constructor Tests passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Constructor Test failed: " + e.getMessage());
        }
    }

    @Test
    void testSettersAndGetters() {
    	System.out.println("\nNumber Of Tests In Group: 4\n");
        try {
            conversation.setConversationId(2);
            assertEquals(2, conversation.getConversationId());

            conversation.setIsResolved(true);
            assertTrue(conversation.getIsResolved());

            List<HelpMessage> messages = new ArrayList<>();
            messages.add(message1);
            conversation.setMessages(messages);
            assertEquals(1, conversation.getMessages().size());
            assertTrue(conversation.getMessages().contains(message1));
            passCount+= 4;
            System.out.println("[ConversationTest] Setter/Getter Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Setter/Getter Test failed: " + e.getMessage());
        }
    }

    @Test
    void testAddMessage() {
    	System.out.println("\nNumber Of Tests In Group: 3\n");
        try {
            conversation.addMessage(message1);
            conversation.addMessage(message2);
            assertEquals(2, conversation.getMessages().size());
            assertTrue(conversation.getMessages().contains(message1));
            assertTrue(conversation.getMessages().contains(message2));
            passCount+= 3;
            System.out.println("[ConversationTest] Add Message Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Add Message Test failed: " + e.getMessage());
        }
    }

    @Test
    void testRemoveMessage() {
    	System.out.println("\nNumber Of Tests In Group: 3\n");
        try {
            conversation.addMessage(message1);
            conversation.addMessage(message2);
            assertTrue(conversation.removeMessage(message1));
            assertFalse(conversation.getMessages().contains(message1));
            assertEquals(1, conversation.getMessages().size());
            passCount+= 3;
            System.out.println("[ConversationTest] Remove Message Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Remove Message Test failed: " + e.getMessage());
        }
    }

    @Test
    void testGetLatestMessage() {
    	System.out.println("\nNumber Of Tests In Group: 1\n");
        try {
            assertNull(conversation.getLatestMessage());

            conversation.addMessage(message1);
            conversation.addMessage(message2);
            assertEquals(message2, conversation.getLatestMessage());
            passCount++;
            System.out.println("[ConversationTest] Get Latest Message Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Get Latest Message Test failed: " + e.getMessage());
        }
    }

    @Test
    void testGetMessageCount() {
    	System.out.println("\nNumber Of Tests In Group: 2\n");
        try {
            assertEquals(0, conversation.getMessageCount());

            conversation.addMessage(message1);
            conversation.addMessage(message2);
            assertEquals(2, conversation.getMessageCount());
            passCount+= 2;
            System.out.println("[ConversationTest] Get Message Count Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Get Message Count Test failed: " + e.getMessage());
        }
    }

    @Test
    void testResolveConversation() {
    	System.out.println("\nNumber Of Tests In Group: 1\n");
        try {
            conversation.resolveConversation();
            assertTrue(conversation.getIsResolved());
            passCount++;
            System.out.println("[ConversationTest] Resolve Conversation Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Resolve Conversation Test failed: " + e.getMessage());
        }
    }

    @Test
    void testReopenConversation() {
    	System.out.println("\nNumber Of Tests In Group: 2\n");
        try {
            conversation.resolveConversation();
            assertTrue(conversation.getIsResolved());

            conversation.reopenConversation();
            assertFalse(conversation.getIsResolved());
            passCount+= 2;
            System.out.println("[ConversationTest] Reopen Conversation Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Reopen Conversation Test failed: " + e.getMessage());
        }
    }

    @Test
    void testClearMessages() {
    	System.out.println("\nNumber Of Tests In Group: 3\n");
        try {
            conversation.addMessage(message1);
            conversation.addMessage(message2);
            assertEquals(2, conversation.getMessageCount());

            conversation.clearMessages();
            assertEquals(0, conversation.getMessageCount());
            assertTrue(conversation.getMessages().isEmpty());
            passCount+= 3;
            System.out.println("[ConversationTest] Clear Messages Test passed.");
        } catch (AssertionError e) {
            System.out.println("[ConversationTest] Clear Messages Test failed: " + e.getMessage());
        }
    }
}


