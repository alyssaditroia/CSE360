package controllers;

import java.sql.SQLException;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.OTP;
import models.TextValidation;
import models.UserSession;

public class LoginPageController extends PageController {
    
    // No-argument constructor required by FXML
    public LoginPageController() {
        super(null); // Pass null or a default stage if needed
    }

    // Constructor with stage
    public LoginPageController(Stage primaryStage) {
        super(primaryStage);
    }

    // FXML elements
    @FXML
    private TextField usernameField; // Field for entering the username

    @FXML
    private PasswordField passwordField; // Field for entering the password

    @FXML
    private Button loginButton; // Button to trigger login

    @FXML
    private Label errorLabel; // Label to display error messages

    // Model classes for validation
    TextValidation validator = new TextValidation();
    Database db = new Database();

 // Method to handle login action
    @FXML
    public void handleLogin() {
        errorLabel.setText(""); // Clear previous error messages
        String username = usernameField.getText();
        String password = passwordField.getText();
        String otp = passwordField.getText(); // This seems incorrect, should be an OTP field

        // Validate credentials using the TextValidation model
        try {
            if (db.validateCredentials(username, password)) {
            	UserSession.getInstance().setUsername(username); // Store username in session
                // If login is successful, check OTP and redirect accordingly
                if (db.getOTP(username, otp)) {
                    redirectToUpdatePassword();
                    return; // Exit after redirection
                }

                if (db.getFirstName(username) == false) { // Check if firstName is null, assuming that the getFirstName method returns a String
                    redirectToSetupAccount();
                    return; // Exit after redirection
                }

                redirectToSelectRolePageView();
            } else {
                // Show error message
                showError("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while processing your request."); // Show a user-friendly error message
        }
    }


    // Method to handle account setup navigation using the PageController's navigateTo method
    @FXML
    public void redirectToSetupAccount() {
        navigateTo("/views/SetupAccountPageView.fxml");
    }
    @FXML
    public void redirectToUpdatePassword() {
        navigateTo("/views/UpdatePasswordPageView.fxml");
    }

    // Method to handle role selection page navigation using the PageController's navigateTo method
    @FXML
    public void redirectToSelectRolePageView() {
        navigateTo("/views/SelectRolePageView.fxml");
    }

    // Optional: Logout method if needed
    @FXML
    public void logout() {
        // Handle logout logic, such as clearing session data
        System.out.println("Logged out successfully."); // Replace with actual logout logic
        navigateTo("/views/LoginPageView.fxml"); // Navigate back to login page
    }

    public void showError(String message) {
        errorLabel.setText(message); // Set the error message in the label
    }
}

