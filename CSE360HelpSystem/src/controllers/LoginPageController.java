package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.OTP;
import models.TextValidation;

public class LoginPageController extends PageController {

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

    TextValidation validator = new TextValidation();
    OTP otp = new OTP();

    public LoginPageController() {
        // Initialize the LoginPageModel
    }

    // Method to handle login action
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getText().toCharArray();
        String oTP = OTPField.getText();

        // Validate credentials using the model
        if (validator.textValidation(username, password)) {
            // If login is successful, check for OTP
        	redirectToSelectRolePageView();
        } 
        else if(otp.validateOTP(oTP)){
        	handleOtpVerification();
        }
        else {
            // Show error message (this can be implemented with a dialog or label in the UI)
            showError("Invalid credentials. Please try again.");
        }
    }

    // Method to handle OTP verification
    public void handleOtpVerification() {
        String otpInput = OTPField.getText(); // Get OTP from the field

        if (otp.validateOTP(otpInput)) {
            // OTP is valid, navigate to setup account
            redirectToSetupAccount(); // Adjust path to your home page FXML
        } else {
            showError("Invalid OTP. Please try again.");
        }
    }

    // Method to handle account setup navigation
    @FXML
    public void redirectToSetupAccount() {
        // Ensure that the button is already part of the scene
        Stage stage = (Stage) setupAccountButton.getScene().getWindow(); // Get the current stage
        try {
            // Load the new page and set it on the stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SetupAccountPageView.fxml"));
            Parent setupAccountView = loader.load();
            Scene setupAccountScene = new Scene(setupAccountView);
            stage.setScene(setupAccountScene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load setup account page.");
        }
    }
    // Method to handle account setup navigation
    @FXML
    public void redirectToSelectRolePageView() {
        // Ensure that the button is already part of the scene
        Stage stage = (Stage) setupAccountButton.getScene().getWindow(); // Get the current stage
        try {
            // Load the new page and set it on the stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SelectRolePageView.fxml"));
            Parent SelectRolePageView = loader.load();
            Scene SelectRolePageScene = new Scene(SelectRolePageView);
            stage.setScene(SelectRolePageScene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load select Role page.");
        }
    }

    // Optional: Logout method if needed
    @FXML
    public void logout() {
        // Handle logout logic, such as clearing session data
        System.out.println("Logged out successfully."); // Replace with actual logout logic
        navigateTo("/views/LoginPageView.fxml"); // Navigate back to login page
    }

    @Override
    public void handlePageLogic() {
        // Logic to handle OTP input and login - this is already implemented in handleLogin()
        handleLogin();
    }

}
