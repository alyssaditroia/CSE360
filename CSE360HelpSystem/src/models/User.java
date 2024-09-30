package models;


public class User {
    private String email;
    private String username;
    private char[] password;
    private String firstName;
    private String middleName; // optional
    private String lastName;
    private String preferredName; // optional
    private boolean isAdmin;
    private boolean isStudent;
    private boolean isInstructor;

    
    public User(String email, char[] password) {
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPreferredName(preferredName);
        this.setMiddleName(middleName);
        this.isAdmin = false;
        this.isInstructor = false;
        this.isStudent = false;
    }

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return preferredName;
	}

	/**
	 * @param preferredName the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the isStudent
	 */
	public boolean isStudent() {
		return isStudent;
	}

	/**
	 * @param isStudent the isStudent to set
	 */
	public void setStudent(boolean isStudent) {
		this.isStudent = isStudent;
	}

	/**
	 * @return the isInstructor
	 */
	public boolean isInstructor() {
		return isInstructor;
	}

	/**
	 * @param isInstructor the isInstructor to set
	 */
	public void setInstructor(boolean isInstructor) {
		this.isInstructor = isInstructor;
	}



}
