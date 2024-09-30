package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.OTP;
import models.TextValidation;

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
    private TextField OTPField; // Field for entering OTP

    @FXML
    private Button loginButton; // Button to trigger login

    @FXML
    private Button setupAccountButton; // Button to navigate to account setup

    @FXML
    private Label errorLabel; // Label to display error messages

    // Model classes for validation
    TextValidation validator = new TextValidation();
    OTP otp = new OTP();

    // Method to handle login action
    @FXML
    public void handleLogin() {
        errorLabel.setText(""); // Clear previous error messages
        String username = usernameField.getText();
        char[] password = passwordField.getText().toCharArray();
        String oTP = OTPField.getText();

        // Validate credentials using the TextValidation model
        if (validator.textValidation(username, password) == "") {
            // If login is successful, navigate to Select Role Page
            redirectToSelectRolePageView();
        } else if (otp.validateOTP(oTP)) {
            handleOtpVerification();
        } else {
            // Show error message
            showError("Invalid credentials. Please try again.");
        }
    }

    // Method to handle OTP verification
    public void handleOtpVerification() {
        errorLabel.setText(""); // Clear previous error messages
        String otpInput = OTPField.getText(); // Get OTP from the field

        if (otp.validateOTP(otpInput)) {
            // OTP is valid, navigate to Setup Account page
            redirectToSetupAccount();
        } else {
            showError("Invalid OTP. Please try again.");
        }
    }

    // Method to handle account setup navigation using the PageController's navigateTo method
    @FXML
    public void redirectToSetupAccount() {
        navigateTo("/views/SetupAccountPageView.fxml");
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

