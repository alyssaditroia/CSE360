package models;
/***********
 * 
 *  FINISHED PAGE DO NOT EDIT
 *  
 *  
 **************/
public class User {
	private int id;
	private String inviteToken;
    private String username;
    private char[] password;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private Boolean isAdmin;  // Use a capital B for the Boolean wrapper class, it is able to hold null value
    private Boolean isStudent; // Use a capital B for the Boolean wrapper class, it is able to hold null value
    private Boolean isInstructor; // Use a capital B for the Boolean wrapper class, it is able to hold null value


    public User(String firstName, String lastName, String preferredName, String email, String username, char[] password, Boolean isAdmin, Boolean isStudent, Boolean isInstructor) {
        this.username = username != null ? username : "";
        this.password = password != null ? password: null;
        this.firstName = firstName != null ? firstName: "";
        this.lastName = lastName != null ? lastName : "";
        this.lastName = preferredName != null ? preferredName : ""; 
        this.email = email != null ? email : "";
        this.isAdmin = (isAdmin != null) ? isAdmin: null;
        this.isStudent = (isStudent != null) ? isStudent: null;
        this.isInstructor = (isInstructor != null) ? isInstructor: null;
    }

    public User() {
	}

	// Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getInviteToken() { return inviteToken; }
    public void setInviteToken(String inviteToken) { this.inviteToken = inviteToken; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public char[] getPassword() { return password; }
    public void setPassword(char[] password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    public String getPreferredName() { return preferredName; }
    public void setPreferredName(String preferredName) { this.preferredName = preferredName; }
    
    public Boolean isAdmin() { return isAdmin; }
    public void setAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    
    public Boolean isStudent() { return isStudent; }
    public void setStudent(Boolean isStudent) { this.isStudent = isStudent; }
    
    public Boolean isInstructor() { return isInstructor; }
    public void setInstructor(Boolean isInstructor) { this.isInstructor = isInstructor; }
    

}

