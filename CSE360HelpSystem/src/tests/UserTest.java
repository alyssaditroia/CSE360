package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import models.User;
import java.sql.Timestamp;

import models.User;

import java.sql.Timestamp;

public class UserTest {

    private User user;
    private static int testcount = 0;
    private static int testsPassed = 0;

    @BeforeEach
    public void setUp() {
		System.out.println("\n =========== USER TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test Group # %d%n", testcount);
        // Initialize a new User object before each test
        user = new User();
    }
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== USER TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testsPassed + "\nTESTS PASSED: " + testsPassed + "\n");
	}

    @Test
    public void testDefaultConstructor() {
        System.out.println("\nTesting default constructor");
        System.out.println("\nTESTS IN THIS TEST GROUP: 7\n");
        
        assertEquals("", user.getUsername());
        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getEmail());
        assertFalse(user.isAdmin());
        assertFalse(user.isStudent());
        assertFalse(user.isInstructor());
        testsPassed+= 7;
    }

    @Test
    public void testParameterizedConstructor() {
        System.out.println("\nTesting parameterized constructor");
        System.out.println("\nTESTS IN THIS TEST GROUP: 8\n");

        char[] password = {'S', 'e', 'c', 'r', 'e', 't', '!', '1'};
        User paramUser = new User("Alyssa", "Ditroia", "Aly", "ad.itroia@example.com", "userAlyssa", password, true, true, false);

        assertEquals("userAlyssa", paramUser.getUsername());
        assertEquals("Alyssa", paramUser.getFirstName());
        assertEquals("Ditroia", paramUser.getLastName());
        assertEquals("ad.itroia@example.com", paramUser.getEmail());
        assertArrayEquals(password, paramUser.getPassword().toCharArray());
        assertTrue(paramUser.isAdmin());
        assertTrue(paramUser.isStudent());
        assertFalse(paramUser.isInstructor());
        testsPassed+= 8;
    }

    @Test
    public void testSetAndGetId() {
        System.out.println("\nTesting setId and getId");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        
        user.setId(1001);
        assertEquals(1001, user.getId());
        testsPassed++;
    }

    @Test
    public void testSetAndGetInviteToken() {
        System.out.println("\nTesting setInviteToken and getInviteToken");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        
        user.setInviteToken("inv123");
        assertEquals("inv123", user.getInviteToken());
        testsPassed++;
    }

    @Test
    public void testSetAndGetUsername() {
        System.out.println("\nTesting setUsername and getUsername");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
        testsPassed++; 
    }

    @Test
    public void testSetAndGetPassword() {
        System.out.println("Testing setPassword and getPassword");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setPassword("mypassword");
        assertEquals("mypassword", user.getPassword());
        testsPassed++;
    }

    @Test
    public void testSetAndGetEmail() {
        System.out.println("Testing setEmail and getEmail");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setEmail("user@example.com");
        assertEquals("user@example.com", user.getEmail());
        testsPassed++; 
    }

    @Test
    public void testSetAndGetFirstName() {
        System.out.println("\nTesting setFirstName and getFirstName");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setFirstName("Alice");
        assertEquals("Alice", user.getFirstName());
        testsPassed++; 
    }

    @Test
    public void testSetAndGetLastName() {
        System.out.println("\nTesting setLastName and getLastName");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
        testsPassed++;
    }

    @Test
    public void testSetAndGetMiddleName() {
        System.out.println("\nTesting setMiddleName and getMiddleName");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setMiddleName("Taylor");
        assertEquals("Taylor", user.getMiddleName());
        testsPassed++;
    }

    @Test
    public void testSetAndGetPreferredName() {
        System.out.println("\nTesting setPreferredName and getPreferredName");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setPreferredName("Ally");
        assertEquals("Ally", user.getPreferredName());
        testsPassed++;
    }

    @Test
    public void testSetAndCheckAdminStatus() {
        System.out.println("\nTesting setAdmin and isAdmin");
        System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");

        user.setAdmin(true);
        assertTrue(user.isAdmin());

        user.setAdmin(false);
        assertFalse(user.isAdmin());
        testsPassed+= 2; 
    }

    @Test
    public void testSetAndCheckStudentStatus() {
        System.out.println("\nTesting setStudent and isStudent");
        System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");

        user.setStudent(true);
        assertTrue(user.isStudent());

        user.setStudent(false);
        assertFalse(user.isStudent());
        testsPassed+= 2; 
    }

    @Test
    public void testSetAndCheckInstructorStatus() {
        System.out.println("Testing setInstructor and isInstructor");
        System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");

        user.setInstructor(true);
        assertTrue(user.isInstructor());

        user.setInstructor(false);
        assertFalse(user.isInstructor());
        testsPassed+= 2; 
    }

    @Test
    public void testSetAndGetOtp() {
        System.out.println("\nTesting setOtp and getOtp");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        user.setOtp("123456");
        assertEquals("123456", user.getOtp());
        testsPassed++;
    }

    @Test
    public void testSetAndGetOtpExpiration() {
        System.out.println("Testing setOtpExpiration and getOtpExpiration");
        System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setOtpExpiration(timestamp);
        assertEquals(timestamp, user.getOtpExpiration());
        testsPassed++; 
    }
}

