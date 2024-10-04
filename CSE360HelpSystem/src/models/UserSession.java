package models;
/***********
 * 
 *  FINISHED PAGE DO NOT EDIT
 *  
 *  
 **************/
public class UserSession {
    private static UserSession instance;
    private User user; // Current logged-in user
    private String username;
    private String inviteCode;
    private String email;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Singleton pattern to get the instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Get the current user's username, with null check
    public String getUsername() {
        return (user != null) ? user.getUsername() : null; // Return null if user is not set
    }

    // Get the current user object
    public User getCurrentUser() {
        return user; // Returns the user or null if not set
    }

    // Set the current user
    public void setCurrentUser(User user) {
        this.user = user; // Set the user object
    }

	public static void setInstance(UserSession userSession) {
		// TODO Auto-generated method stub
		
	}

	public void setUsername(String username2) {
		this.user.setUsername(username2);
		setCurrentUser(user);
		
	}

	/**
	 * @return the inviteCode
	 */
	public String getInviteCode() {
		return inviteCode;
	}

	/**
	 * 
	 * @param inviteCode
	 */
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	public String getEmail() {
		return email;
	}
	/**
	 * 
	 * @param inviteCode
	 */
	public void setEmail(String email) {
		this.inviteCode = email;
	}
}

