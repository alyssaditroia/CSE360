package models;

public class User {
    private int id;  // Added ID for primary key tracking
    private String email;
    private String username;
    private char[] password;
    private String firstName;
    private String middleName; // optional
    private String lastName;
    private String preferredName; // optional
    private Boolean isAdmin;  // Changed to Boolean wrapper to handle null cases
    private Boolean isStudent;
    private Boolean isInstructor;

    // Constructor for user registration without additional profile info, only username and password
    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
    }
    // Constructor for user registration including email and permissions for invite code
    public User(int id, String email, boolean isAdmin, boolean isStudent, boolean isInstructor) {
    	this.id = id;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isStudent = isStudent;
        this.isInstructor = isInstructor;
    }

    // Constructor with full profile details
    public User(int id, String firstName, String lastName, String preferredName, String email, String username, char[] password, boolean isAdmin, boolean isStudent, boolean isInstructor) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isStudent = isStudent;
        this.isInstructor = isInstructor;
    }

    // Getters and setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean isStudent() {
        return isStudent;
    }

    public void setStudent(Boolean isStudent) {
        this.isStudent = isStudent;
    }

    public Boolean isInstructor() {
        return isInstructor;
    }

    public void setInstructor(Boolean isInstructor) {
        this.isInstructor = isInstructor;
    }
}

