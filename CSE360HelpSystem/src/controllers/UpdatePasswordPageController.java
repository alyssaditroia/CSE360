/**
 * 
 */
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
 * <p> Title: UpdatePasswordPageController </p>
 * 
 * 
 * <p> Description: Controls the view for the page where the user's password is being changed </p>
 * 
 * 
 * @author Alyssa DiTroia
 */
public class UpdatePasswordPageController extends PageController {
    private Database db;

    // Constructor for FXMLLoader
    public UpdatePasswordPageController() {
        super();
    }

    // Constructor with dependency injection for Stage and Database
    public UpdatePasswordPageController(Stage primaryStage, Database db) {
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
     * handleSetupButtonAction()
     * Below is the implementation for handling the user's account setup and updating their information in the database.
     * The details are validated and the user's information associated with that invite code is updated in the database.
     * The invite code is set to null after use.
     */
    @FXML
    private void handleSetupButtonAction() {
        db = Database.getInstance(); // Get the current instance of the Database
        statusLabel.setText(""); // Clear previous status messages
        UserSession currentUser = UserSession.getInstance(); // Get the current user instance
        String inviteCode = currentUser.getInviteCode(); // Get the current user instance invite code
        String currentUsername = currentUser.getUsername();
        System.out.println("Current user username: " + currentUsername);

        // Trim whitespace from username field
        String username = usernameField.getText().trim();
        char [] password = passwordField.getText().toCharArray();
        char [] confirmPassword = confirmPasswordField.getText().toCharArray();

        // Validate the username and password using TextValidation model
        String validationMessage = TextValidation.validateSetupFields(username, password, confirmPassword);

        // If the fields are valid
        if (validationMessage.isEmpty()) {
            try {
                // No invite code, update the password for the user
                User newUser = new User("", "", "", "", username, password, null, null, null);
                db.updatePassword(username, password);

                // Show success message for password update
                statusLabel.setText("Password updated successfully! Redirecting to login...");
                statusLabel.setStyle("-fx-text-fill: green;");

                // Redirect to login page after 2 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> redirectToLogin());
                pause.play();
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
