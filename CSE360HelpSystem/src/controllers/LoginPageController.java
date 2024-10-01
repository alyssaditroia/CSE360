package controllers;

import java.sql.SQLException;

import database.Database;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.OTP;
import models.TextValidation;
import models.UserSession;

public class LoginPageController extends PageController {

    // Constructor for FXML
    public LoginPageController() {
        super(null);
    }

    // Constructor with stage
    public LoginPageController(Stage primaryStage) {
        super(primaryStage);
    }

    // FXML elements
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;

    // Model classes
    private TextValidation validator = new TextValidation();
    private Database db = new Database();

    // Method to handle login action
    @FXML
    public void handleLogin() {
        // Clear previous error messages
        errorLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText(); // Using password field for both password and OTP

        try {
            // Check if the database is empty
            if (db.isDatabaseEmpty()) {
                // If the database is empty, we treat this as setting up the administrator
                setupAdministrator(); // Call the setup method
                return; // Exit after attempting to set up the administrator
            }

            // Validate user credentials
            if (db.validateCredentials(username, password)) {
                // Proceed with normal login flow
                String firstName = db.getFirstName(username);
                if (firstName == null || firstName.isEmpty()) {
                    redirectToSetupAccount(); // Redirect to account setup if first name is not set
                    return;  // Exit after redirection
                }

                // Redirect to role selection
                redirectToSelectRolePageView();
            } else {
                // Show error if credentials are invalid
                showError("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while processing your request.");
        }
    }


    @FXML
    public void setupAdministrator() {
        errorLabel.setText(""); // Clear previous error messages
        statusLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            db.setupAdministrator(username, password, true);
            //UserSession.getInstance().setUsername(username);

            redirectToLoginPageView();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while trying to create an administrator.");
        }
    }

    private void redirectToLoginPageView() {
        statusLabel.setText("Account setup successful! Redirecting to login...");
        statusLabel.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("/views/LoginPageView.fxml"));
        pause.play();
    }

    @FXML
    public void redirectToSetupAccount() {
        navigateTo("/views/SetupAccountPageView.fxml");
    }

    @FXML
    public void redirectToUpdatePassword() {
        navigateTo("/views/UpdatePasswordPageView.fxml");
    }

    @FXML
    public void redirectToSelectRolePageView() {
        navigateTo("/views/SelectRolePageView.fxml");
    }

    @FXML
    public void logout() {
        System.out.println("Logged out successfully.");
        navigateTo("/views/LoginPageView.fxml");
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }
}


