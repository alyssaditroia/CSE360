package models;

/***********
 * 
 * User Session Class
 * Handles the current user's session for the logged in user
 * 
 * 
 **************/
public class UserSession {
	private static UserSession instance;
	private User user; // Current logged-in user
	private String username;
	private String inviteCode;
	private String email;
	private String currentRole;
	private Article selectedArticle;

	// Private constructor to prevent instantiation
		private UserSession() {
		}

		// Singleton pattern to get the instance
		public static UserSession getInstance() {
			if (instance == null) {
				instance = new UserSession();
			}
			return instance;
		}

		// Get the current user's username, with null check
		public String getUsername() {
			return (user != null) ? user.getUsername() : null;
		}

		// Get the current user object
		public User getCurrentUser() {
			return user; // Returns the user or null if not set
		}

		// Set the current user
		public void setCurrentUser(User user) {
			this.user = user; // Set the user object
		}

		// Set the username, ensuring the user object exists
		public void setUsername(String username) {
			if (this.user == null) {
				this.user = new User(); // Ensure user object is created
			}
			this.user.setUsername(username); // Set the username for the user
		}

		// Getter and Setter for inviteCode
		public String getInviteCode() {
			System.out.println("[INFO in UserSession] getInviteCode(), Invite code: " + inviteCode);
			return inviteCode;
		}

		public void setInviteCode(String inviteCode) {
			System.out.println("[INFO in UserSession] setInviteCode() Invite Code: " + inviteCode);
			this.inviteCode = inviteCode;
		}

		// Getter and Setter for email
		public String getEmail() {
			System.out.println("[INFO in UserSession] getEmail() Email: " + email);
			return email;
		}

		public void setEmail(String email) {
			System.out.println("[INFO in UserSession] setEmail() Email: " + email);
			this.email = email;
		}
		
		public String getCurrentRole() {
			System.out.println("[INFO in UserSession] getCurrentRole() current role: " + currentRole);
			return currentRole;
		}
		public void setCurrentRole(String currentRole) {
			System.out.println("[INFO in UserSession] setCurrentRole() current role: " + currentRole);
			this.currentRole = currentRole;
		}

		// Methods for managing selectedArticle
		public void setSelectedArticle(Article selectedArticle) {
		    
		    if (selectedArticle != null) {
		        System.out.println("[INFO in UserSession] setSelectedArticle() Selected Article: " + selectedArticle.getTitle());
		    } else {
		        System.out.println("[INFO in UserSession] Selected Article is set to null");
		    }
		    
		    this.selectedArticle = selectedArticle;
		}


		public Article getSelectedArticle() {
			System.out.println("[INFO in UserSession] getSelectedArticle() Selected Article: " + selectedArticle.getTitle());
			return selectedArticle;
		}

		// Method to set the instance (optional, but ensures flexibility)
		public static void setInstance(UserSession userSession) {
			System.out.println("[INFO in UserSession] setInstance() called");
			instance = userSession;
		}
	}

