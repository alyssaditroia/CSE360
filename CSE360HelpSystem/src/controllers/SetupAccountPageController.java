package controllers;

import java.io.IOException;
import java.sql.SQLException;

import database.database; // Ensure your Database class is properly named (uppercase D)
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import models.TextValidation;

public class SetupAccountPageController extends PageController {

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

    private database database = new database(); // Ensure this is properly defined
    TextValidation validator = new TextValidation();

    @FXML
    private void handleSetupButtonAction() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate the username and password using model's TextValidation
        if (areFieldsValid(username, password, confirmPassword)) {
            // Create a new user object
            User newUser = new User(username, password.toCharArray()); // Use the relevant constructor
            // Set up the account using the model
            if (validator.textValidation(username, password)) {
                // Add the user with a default role (modify as necessary)
				newUser.setAdmin(false);
				newUser.setStudent(true); // Example role setting
				newUser.setInstructor(false);

				// Save the user using the database method
				database.addUser(newUser);
				statusLabel.setText("Account setup successful! Redirecting to login...");
				statusLabel.setStyle("-fx-text-fill: green;"); // Example success feedback

				// Redirect to login page (make sure to define this logic)
				try {
					redirectToLogin();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else {
                statusLabel.setText("Invalid input data.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private boolean areFieldsValid(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Username and password fields are required.");
            return false;
        }

        // Validate password using model's TextValidation method
        if (!validator.validatePassword(password)) {
            statusLabel.setText("Password does not meet the requirements.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return false;
        }

        return true;
    }

    private void redirectToLogin() throws IOException {
        // Logic to navigate to the login page
        // Example: Load the login FXML and set it in the scene
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        // Parent root = loader.load();
        // Scene scene = new Scene(root);
        // Stage stage = (Stage) setupButton.getScene().getWindow();
        // stage.setScene(scene);
        // stage.show();
        
        // After logging in, redirect to finishAccountSetup page
        // This would be handled in the Login controller or after login validation
    }

    @Override
    public void handlePageLogic() {
        // Logic for handling page-specific actions, if needed
    }
}

