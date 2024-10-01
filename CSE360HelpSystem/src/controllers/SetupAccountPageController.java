package controllers;

import java.io.IOException;
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
import models.TextValidation;

public class SetupAccountPageController extends PageController {
    private Database db;
    private TextValidation validator;
    
 // Constructor with dependency injection for Stage
    public SetupAccountPageController() {
        super(null);
    }

    // Constructor with dependency injection for Stage
    public SetupAccountPageController(Stage primaryStage) {
        super(primaryStage);
        validator = new TextValidation(); // Initialize the TextValidation object
        db = new Database();
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField inviteCodeField;

    @FXML
    private Button setupButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleSetupButtonAction() {
        // Clear previous status messages
        statusLabel.setText("");

        // Trim whitespace from username and get password values
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String inviteCode = inviteCodeField.getText();

        // Validate the username and password using model's TextValidation
        String validationMessage = validator.validateSetupFields(username, password, confirmPassword);
        if (validationMessage.isEmpty()) {
        	db.setupAccount(inviteCode, username, password);

            
            // Show success message
            statusLabel.setText("Account setup successful! Redirecting to login...");
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

