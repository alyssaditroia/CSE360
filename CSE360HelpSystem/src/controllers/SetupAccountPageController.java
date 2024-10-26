package controllers;

import java.sql.SQLException;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.UserSession;
import models.TextValidation;

/**
 * <p>
 * The {@code SetupAccountPageController} handles the user input for setting up their account with a username and password
 * </p>
 * 
 * <p>
 * Description: Controls the view for the page where the user's account is being
 * setup Initial setup consists of the user setting their username and password.
 * </p>
 * 
 * @author Alyssa DiTroia
 */
public class SetupAccountPageController extends PageController {
	private Database db;

	// Constructor with for FXMLLoader
	public SetupAccountPageController() {
		super();
	}

	// Constructor with dependency injection for Stage and Database
	public SetupAccountPageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private Button setupButton;

	@FXML
	private Label statusLabel;

	/**
	 * handleSetupButtonAction() Below is the implementation for handling the user's
	 * account setup and updating their information in the database The details are
	 * validated and the user's information associated with that invite code is
	 * updated in the database The invite code is set to null after use
	 * 
	 */
	@FXML
	private void handleSetupButtonAction() {
		db = Database.getInstance(); // Get the current instance of the Database
		statusLabel.setText(""); // Clear previous status messages
		UserSession currentUser = UserSession.getInstance(); // Get the current user instance
		String inviteCode = currentUser.getInviteCode(); // Get the current user instance invite code
		String currentUsername = currentUser.getUsername();
		System.out.println("Current user invite code: " + inviteCode);
		System.out.println("Current user username: " + currentUsername);

		// Trim whitespace from username field
		String username = usernameField.getText().trim();
		char[] password = passwordField.getText().toCharArray();
		char[] confirmPassword = confirmPasswordField.getText().toCharArray();

		// Validate the username and password using TextValidation model
		String validationMessage = TextValidation.validateSetupFields(username, password, confirmPassword);

		// If the fields are valid
		if (validationMessage.isEmpty()) {
			try {
				if (inviteCode != null) {
					// User has an invite code, complete the invite process
					User newUser = new User("", "", "", "", username, password, null, null, null);
					db.completeInvite(inviteCode, username, password);

					// Show success message for account setup
					statusLabel.setText("Account setup successful! Redirecting to login...");
					statusLabel.setStyle("-fx-text-fill: green;");

					// Redirect to login page after 2 seconds
					PauseTransition pause = new PauseTransition(Duration.seconds(2));
					pause.setOnFinished(event -> redirectToLogin());
					pause.play();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				statusLabel.setText("Error processing request. Please try again.");
				statusLabel.setStyle("-fx-text-fill: red;");
			}
		} else {
			// If the fields don't meet validation criteria, show error message
			statusLabel.setText(validationMessage);
			statusLabel.setStyle("-fx-text-fill: red;");
		}
	}

	private void redirectToLogin() {
		navigateTo("/views/LoginPageView.fxml");
	}
}