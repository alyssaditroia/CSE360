/**
 * 
 */
package controllers;

import database.Database;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.TextValidation;
import models.User;
import models.UserSession;

/**
 * When user's password is an OTP, then this is the page the user is directed to 
 */
public class UpdatePasswordPageController extends PageController {
	 private Database db;
	 
	// Default constructor for FXML
	public UpdatePasswordPageController() {
		super();
	}
	public UpdatePasswordPageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}
	// Gets the current logged in user by username
	String username = UserSession.getInstance().getUsername();
	
    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button updateButton;

    @FXML
    private Label statusLabel;
    
    @FXML
    private void handleUpdatePasswordAction() {
        // Clear previous status messages
        statusLabel.setText("");

        // Trim whitespace from password values
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate the username and password using model's TextValidation
        // Will make sure the username and password meet specific criteria
        String validationMessage = TextValidation.validateChangePassword(password, confirmPassword);
        if (validationMessage.isEmpty()) {
            // Create a new user object
            db.updatePassword(username, password);
            
            // Show success message
            statusLabel.setText("Password Successfully Updated! Redirecting to login...");
            statusLabel.setStyle("-fx-text-fill: green;"); // Example success feedback

            // Create a PauseTransition for 2 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                // Redirect to login page after 2 seconds
                redirectToLogin();
            });
            pause.play(); // Start the pause
        } else {
            // Display the validation error message in the GUI
            statusLabel.setText(validationMessage);
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void redirectToLogin() {
        navigateTo("/views/LoginPageView.fxml");
    }

}
