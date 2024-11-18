package controllers;

import java.sql.SQLException;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.UserSession;
import models.TextValidation;

/********
 * 
 * <p>
 * Title: FinishAccountSetupController
 * </p>
 * 
 * <p>
 * Description: Handles the Finish Account Setup View where the user enter's the
 * rest of their information to finish setting up their account. This
 * information includes first name, last name, email, preferred name.
 * </p>
 * 
 * @author Alyssa DiTroia
 **************/
public class FinishAccountSetupController extends PageController {
	/**
     * Database instance
     */
	private Database db;

	/**
	 * Default constructor required for FXML loader initialization.
	 */
	public FinishAccountSetupController() {
		super();
	}

	/**
	 * Constructs a FinishAccountSetupController with the specified stage and database.
	 *
	 * @param primaryStage The main application window
	 * @param db The database instance to be used
	 */
	public FinishAccountSetupController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}
	
	/**
     * FXML injected UI elements for the Finish Account Setup page
     */
	@FXML
	private TextField emailField; // TextField for email
	@FXML
	private TextField firstNameField; // TextField for first name
	@FXML
	private TextField lastNameField; // TextField for last name
	@FXML
	private TextField preferredNameField; // TextField for preferred name (optional)
	@FXML
	private Button finishSetupButton; // Button to finalize account setup
	@FXML
	private Label errorLabel; // Label to display error messages

	/**
	 * ***** BELOW FINIALIZES THE USER'S ACCOUNT DETAILS IN THE DATABASE ********
	 */
	@FXML
	private void finishSetup() {
		db = Database.getInstance();

		// Retrieve the user's input
		String email = emailField.getText();
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		String preferredName = preferredNameField.getText();

		// Validate email format
		if (!TextValidation.validateEmail(email).isEmpty()) {
			errorLabel.setText("Invalid email format.");
			return;
		}
		// Check if first name and last name are empty
		if (firstName.isEmpty() || lastName.isEmpty()) {
			errorLabel.setText("First name and last name cannot be empty.");
			return;
		}

		// Get the current user from the session
		User user = UserSession.getInstance().getCurrentUser();
		String currentUsername = UserSession.getInstance().getUsername();

		// Check if the user is null
		if (user == null) {
			errorLabel.setText("No user is currently logged in. Please log in again.");
			return; // Early exit to prevent NullPointerException
		}
		System.out.println("Current logged in user: " + currentUsername);

		// Update user object with additional information
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPreferredName(preferredName);
		String username = user.getUsername();

		try {
			// Update user in the database
			db.updateUser(username, firstName, lastName, preferredName, email);
			
			// Get UserSession and update the user's ID
		    UserSession userSession = UserSession.getInstance();
		    userSession.getCurrentUser().setId(db.getUserId(username));
	        
			int numRoles = checkNumRoles(username);
			if (numRoles == 1) {
				redirectBasedOnSingleRole(username);
			} else {
				redirectToSelectRolePageView();
			}
		} catch (SQLException e) {
			errorLabel.setText("Database update failed: " + e.getMessage());
		}
	}
	
	/**
	 * Redirects the user to the role selection page view.
	 */
	private void redirectToSelectRolePageView() {
		navigateTo("/views/SelectRolePageView.fxml");
	}

	/**
	 * Checks the number of roles assigned to a user.
	 *
	 * @param username The username to check roles for
	 * @return The number of roles the user has
	 * @throws SQLException if there is an error accessing the database
	 */
	private int checkNumRoles(String username) throws SQLException {
		int numRoles = 0;
		db = Database.getInstance();
		if (db.isUserAdmin(username)) {
			numRoles++;
		}
		if (db.isUserInstructor(username)) {
			numRoles++;
		}
		if (db.isUserStudent(username)) {
			numRoles++;
		}
		System.out.println("Number of roles for user: " + username + ": " + numRoles);
		return numRoles;
	}

	/**
	 * Redirects the user to their appropriate home page based on their single role.
	 * Sets the user's current role in the session and navigates to the corresponding view.
	 *
	 * @param username The username to check and redirect
	 * @throws SQLException if there is an error accessing the database
	 */
	private void redirectBasedOnSingleRole(String username) throws SQLException {
		UserSession userSession = UserSession.getInstance();
		db = Database.getInstance();
		if (db.isUserAdmin(username)) {
			String currentRole = "admin";
			userSession.setCurrentRole(currentRole);
			navigateTo("/views/AdminHomePageView.fxml");
		} else if (db.isUserInstructor(username)) {
			String currentRole = "instructor";
			userSession.setCurrentRole(currentRole);
			navigateTo("/views/InstructorHomePageView.fxml");
		} else if (db.isUserStudent(username)) {
			String currentRole = "student";
			userSession.setCurrentRole(currentRole);
			navigateTo("/views/StudentHomePageView.fxml");
		}
	}

	/**
	 * Initializes the controller with the specified stage and database.
	 * Extends the parent class initialization.
	 *
	 * @param stage The main application window
	 * @param db The database instance to be used
	 */
	@Override
	public void initialize(Stage stage, Database db) {
		super.initialize(stage, db);

	}
}