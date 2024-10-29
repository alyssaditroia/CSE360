package models;

import java.sql.Timestamp;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/***********
 * 
 * The {@code User} Class Models the User information in memory before storing to the database
 * 
 * @author Alyssa DiTroia
 **************/
public class User {
	/**
     * Unique identifier for the user.
     */
	private int id;
	
	/**
     * Token used for user invitation/registration process.
     */
	private String inviteToken;
	
	/**
     * User's login username stored as a JavaFX property.
     */
	private SimpleStringProperty username;
	
	/**
     * User's password stored as a character array for security.
     */
	private char[] password;
	
	/**
     * User's email address stored as a JavaFX property.
     */
	private SimpleStringProperty email;
	
	/**
     * User's first name stored as a JavaFX property.
     */
	private SimpleStringProperty firstName;
	
	/**
     * User's last name stored as a JavaFX property.
     */
	private SimpleStringProperty lastName;
	
	/**
     * User's middle name stored as a JavaFX property.
     */
	private SimpleStringProperty middleName;
	
	/**
     * User's preferred name stored as a JavaFX property.
     */
	private SimpleStringProperty preferredName;
	
	/**
     * Flag indicating if user has administrator privileges.
     */
	private SimpleBooleanProperty isAdmin;
	
	/**
     * Flag indicating if user has student role.
     */
	private SimpleBooleanProperty isStudent;
	
	/**
     * Flag indicating if user has instructor role.
     */
	private SimpleBooleanProperty isInstructor;
	
	/**
     * One-time password for authentication.
     */
	private String Otp;
	
	/**
     * Timestamp indicating when the OTP expires.
     */
	private Timestamp OtpExpiration;

	/**
     * Constructs a new User with specified details.
     * Initializes all properties with provided values or defaults if null.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param preferredName User's preferred name
     * @param email User's email address
     * @param username User's login username
     * @param password User's password
     * @param isAdmin Administrator role flag
     * @param isStudent Student role flag
     * @param isInstructor Instructor role flag
     */
	public User(String firstName, String lastName, String preferredName, String email, String username, char[] password,
			Boolean isAdmin, Boolean isStudent, Boolean isInstructor) {
		this.username = new SimpleStringProperty(username != null ? username : "");
		this.password = password != null ? password : null;
		this.firstName = new SimpleStringProperty(firstName != null ? firstName : "");
		this.lastName = new SimpleStringProperty(lastName != null ? lastName : "");
		this.preferredName = new SimpleStringProperty(preferredName != null ? preferredName : "");
		this.email = new SimpleStringProperty(email != null ? email : "");
		this.isAdmin = new SimpleBooleanProperty(isAdmin != null ? isAdmin : false);
		this.isStudent = new SimpleBooleanProperty(isStudent != null ? isStudent : false);
		this.isInstructor = new SimpleBooleanProperty(isInstructor != null ? isInstructor : false);
	}

	/**
     * Default constructor for User.
     * Initializes all string properties to empty strings and boolean properties to false.
     */
	public User() {
		this.username = new SimpleStringProperty("");
		this.firstName = new SimpleStringProperty("");
		this.lastName = new SimpleStringProperty("");
		this.middleName = new SimpleStringProperty("");
		this.preferredName = new SimpleStringProperty("");
		this.email = new SimpleStringProperty("");
		this.isAdmin = new SimpleBooleanProperty(false);
		this.isStudent = new SimpleBooleanProperty(false);
		this.isInstructor = new SimpleBooleanProperty(false);
	}

	 /**
     * Gets the user's unique identifier.
     *
     * @return the user's ID
     */
	public int getId() {
		return id;
	}

	/**
     * Sets the user's unique identifier.
     *
     * @param id the ID to set
     */
	public void setId(int id) {
		this.id = id;
	}

	/**
     * Gets the user's invitation token.
     *
     * @return the invitation token
     */
	public String getInviteToken() {
		return inviteToken;
	}

	/**
     * Sets the user's invitation token.
     *
     * @param inviteToken the invitation token to set
     */
	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	/**
     * Gets the user's username.
     *
     * @return the username
     */
	public String getUsername() {
		return username.get();
	}

	/**
     * Sets the user's username.
     *
     * @param username the username to set
     */
	public void setUsername(String username) {
		this.username.set(username);
	}

	/**
     * Gets the user's password as a string.
     * Note: This method creates a new String from the password char array.
     *
     * @return the password as a string
     */
	public String getPassword() {
		return new String(password);
	}

	/**
     * Sets the user's password.
     * Converts the input string to a char array for storage.
     *
     * @param password the password to set
     */
	public void setPassword(String password) {
		this.password = password.toCharArray();
	}

	/**
     * Gets the user's email address.
     *
     * @return the email address
     */
	public String getEmail() {
		return email.get();
	}

	/**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
	public void setEmail(String email) {
		this.email.set(email);
	}

	/**
     * Gets the user's first name.
     *
     * @return the first name
     */
	public String getFirstName() {
		return firstName.get();
	}

	/**
     * Sets the user's first name.
     *
     * @param firstName the first name to set
     */
	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	/**
     * Gets the user's last name.
     *
     * @return the last name
     */
	public String getLastName() {
		return lastName.get();
	}

	/**
     * Sets the user's last name.
     *
     * @param lastName the last name to set
     */
	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	/**
     * Gets the user's middle name.
     *
     * @return the middle name
     */
	public String getMiddleName() {
		return middleName.get();
	}

	/**
     * Sets the user's middle name.
     *
     * @param middleName the middle name to set
     */
	public void setMiddleName(String middleName) {
		this.middleName.set(middleName);
	}

	/**
     * Gets the user's preferred name.
     *
     * @return the preferred name
     */
	public String getPreferredName() {
		return preferredName.get();
	}

	/**
     * Sets the user's preferred name.
     *
     * @param preferredName the preferred name to set
     */
	public void setPreferredName(String preferredName) {
		this.preferredName.set(preferredName);
	}

	/**
     * Checks if the user has administrator privileges.
     *
     * @return true if user is an administrator, false otherwise
     */
	public Boolean isAdmin() {
		return isAdmin.get();
	}

	/**
     * Sets the user's administrator status.
     *
     * @param isAdmin the administrator status to set
     */
	public void setAdmin(Boolean isAdmin) {
		this.isAdmin.set(isAdmin);
	}

	/**
     * Checks if the user has student role.
     *
     * @return true if user is a student, false otherwise
     */
	public Boolean isStudent() {
		return isStudent.get();
	}

	/**
     * Sets the user's student status.
     *
     * @param isStudent the student status to set
     */
	public void setStudent(Boolean isStudent) {
		this.isStudent.set(isStudent);
	}

	/**
     * Checks if the user has instructor role.
     *
     * @return true if user is an instructor, false otherwise
     */
	public Boolean isInstructor() {
		return isInstructor.get();
	}

	/**
     * Sets the user's instructor status.
     *
     * @param isInstructor the instructor status to set
     */
	public void setInstructor(Boolean isInstructor) {
		this.isInstructor.set(isInstructor);
	}

	/**
     * Gets the user's one-time password.
     *
     * @return the OTP
     */
	public String getOtp() {
		return Otp;
	}

	/**
     * Sets the user's one-time password.
     *
     * @param Otp the OTP to set
     */
	public void setOtp(String Otp) {
		this.Otp = Otp;
	}

	/**
     * Gets the expiration timestamp of the user's OTP.
     *
     * @return the OTP expiration timestamp
     */
	public Timestamp getOtpExpiration() {
		return OtpExpiration;
	}

	/**
     * Sets the expiration timestamp for the user's OTP.
     *
     * @param timestamp the expiration timestamp to set
     */
	public void setOtpExpiration(Timestamp timestamp) {
		this.OtpExpiration = timestamp;
	}
}