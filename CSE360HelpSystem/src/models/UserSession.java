package models;

/***********
 * The {@code UserSession} class handles the current sessions User, Roles, and Selected Article
 * 
 **************/
public class UserSession {
	/**
     * Singleton instance of UserSession.
     */
	private static UserSession instance;
	
	/**
     * The currently logged-in user object.
     */
	private User user;
	
	/**
     * The username of the currently logged-in user.
     */
	private String username;
	
	/**
     * The invite code used for account creation.
     */
	private String inviteCode;
	
	/**
     * The email address of the current user.
     */
	private String email;
	
	/**
     * The current role of the user (admin, instructor, or student).
     */
	private String currentRole;
	
	/**
     * The currently selected article for viewing or editing.
     */
	private Article selectedArticle;
	
	// PHASE 3 ADDITIONS
	// 1 is view - 'student'
	// 2 is create and delete - 'admin'
	// 3 is create, delete, edit, and view - 'instructor'
	private int specialGroupAccessLevel = 0;
	
	private SpecialGroup selectedSpecialGroup;

	 /**
     * Private constructor to enforce singleton pattern.
     */
	private UserSession() {
	}

	 /**
     * Gets the singleton instance of UserSession.
     * Creates a new instance if one doesn't exist.
     *
     * @return The singleton UserSession instance
     */
	public static UserSession getInstance() {
		if (instance == null) {
			instance = new UserSession();
		}
		return instance;
	}
	

	/**
     * Gets the current user's username.
     *
     * @return The username of the current user, or null if no user is set
     */
	public String getUsername() {
		return (user != null) ? user.getUsername() : null;
	}

	/**
     * Gets the current user object.
     *
     * @return The current User object, or null if not set
     */
	public User getCurrentUser() {
		return user; 
	}

	/**
     * Sets the current user object.
     *
     * @param user The User object to set as current user
     */
	public void setCurrentUser(User user) {
		this.user = user; 
	}

	/**
     * Sets the username for the current user.
     * Creates a new User object if one doesn't exist.
     *
     * @param username The username to set
     */
	public void setUsername(String username) {
		if (this.user == null) {
			this.user = new User(); // Ensure user object is created
		}
		this.user.setUsername(username); // Set the username for the user
	}

	/**
     * Gets the invite code for account creation.
     *
     * @return The current invite code
     */
	public String getInviteCode() {
		System.out.println("[INFO in UserSession] getInviteCode(), Invite code: " + inviteCode);
		return inviteCode;
	}

	/**
     * Sets the invite code for account creation.
     *
     * @param inviteCode The invite code to set
     */
	public void setInviteCode(String inviteCode) {
		System.out.println("[INFO in UserSession] setInviteCode() Invite Code: " + inviteCode);
		this.inviteCode = inviteCode;
	}

	 /**
     * Gets the email address of the current user.
     *
     * @return The current user's email address
     */
	public String getEmail() {
		System.out.println("[INFO in UserSession] getEmail() Email: " + email);
		return email;
	}

	/**
     * Sets the email address for the current user.
     *
     * @param email The email address to set
     */
	public void setEmail(String email) {
		System.out.println("[INFO in UserSession] setEmail() Email: " + email);
		this.email = email;
	}
	
	/**
     * Gets the current role of the user.
     *
     * @return The current role (admin, instructor, or student)
     */
	public String getCurrentRole() {
		System.out.println("[INFO in UserSession] getCurrentRole() current role: " + currentRole);
		return currentRole;
	}
	
	/**
     * Sets the current role of the user.
     *
     * @param currentRole The role to set (admin, instructor, or student)
     */
	public void setCurrentRole(String currentRole) {
		System.out.println("[INFO in UserSession] setCurrentRole() current role: " + currentRole);
		this.currentRole = currentRole;
	}

	/**
     * Sets the currently selected article.
     *
     * @param selectedArticle The Article object to set as selected
     */
	public void setSelectedArticle(Article selectedArticle) {
	    
	    if (selectedArticle != null) {
	        System.out.println("[INFO in UserSession] setSelectedArticle() Selected Article: " + selectedArticle.getTitle());
	    } else {
	        System.out.println("[INFO in UserSession] Selected Article is set to null");
	    }
	    
	    this.selectedArticle = selectedArticle;
	}

	/**
     * Gets the currently selected article.
     *
     * @return The currently selected Article object
     */
	public Article getSelectedArticle() {
		System.out.println("[INFO in UserSession] getSelectedArticle() Selected Article: " + selectedArticle.getTitle());
		return selectedArticle;
	}

	/**
     * Sets the singleton instance of UserSession.
     * Used for testing and ensuring flexibility in instance management.
     *
     * @param userSession The UserSession instance to set
     */
	public static void setInstance(UserSession userSession) {
		System.out.println("[INFO in UserSession] setInstance() called");
		instance = userSession;
	}
	
	
	public void setAccessLevel(int level) {
		this.specialGroupAccessLevel = level;
	}
	
	
	public int getAccessLevel() {
		return this.specialGroupAccessLevel;
	}
	
	
	public void setSelectedSpecialGroup(SpecialGroup group) {
	    
	    if (selectedArticle != null) {
	        System.out.println("[INFO in UserSession] setSelectedSpecialGroup Selected Group: " + selectedSpecialGroup.getName());
	    } else {
	        System.out.println("[INFO in UserSession] Selected Group is set to null");
	    }
	    
	    this.selectedSpecialGroup = group;
	}

	
	public SpecialGroup getSelectedSpecialGroup() {
		System.out.println("[INFO in UserSession] setSelectedSpecialGroup Selected Group: " + selectedSpecialGroup.getName());
		return this.selectedSpecialGroup;
	}
}

