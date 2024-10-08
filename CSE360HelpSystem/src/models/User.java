package models;

import java.sql.Timestamp;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/***********
 * 
 * User Class Models the User Entity
 * 
 * @author Alyssa DiTroia
 **************/
public class User {
	private int id;
	private String inviteToken;
	private SimpleStringProperty username;
	private char[] password;
	private SimpleStringProperty email;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private SimpleStringProperty middleName;
	private SimpleStringProperty preferredName;
	private SimpleBooleanProperty isAdmin;
	private SimpleBooleanProperty isStudent;
	private SimpleBooleanProperty isInstructor;
	private String Otp;
	private Timestamp OtpExpiration;

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

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInviteToken() {
		return inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getPassword() {
		return new String(password);
	}

	public void setPassword(String password) {
		this.password = password.toCharArray();
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getMiddleName() {
		return middleName.get();
	}

	public void setMiddleName(String middleName) {
		this.middleName.set(middleName);
	}

	public String getPreferredName() {
		return preferredName.get();
	}

	public void setPreferredName(String preferredName) {
		this.preferredName.set(preferredName);
	}

	public Boolean isAdmin() {
		return isAdmin.get();
	}

	public void setAdmin(Boolean isAdmin) {
		this.isAdmin.set(isAdmin);
	}

	public Boolean isStudent() {
		return isStudent.get();
	}

	public void setStudent(Boolean isStudent) {
		this.isStudent.set(isStudent);
	}

	public Boolean isInstructor() {
		return isInstructor.get();
	}

	public void setInstructor(Boolean isInstructor) {
		this.isInstructor.set(isInstructor);
	}

	public String getOtp() {
		return Otp;
	}

	public void setOtp(String Otp) {
		this.Otp = Otp;
	}

	public Timestamp getOtpExpiration() {
		return OtpExpiration;
	}

	public void setOtpExpiration(Timestamp timestamp) {
		this.OtpExpiration = timestamp;
	}
}