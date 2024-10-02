package controllers;

import java.io.IOException;
import java.sql.SQLException;

import database.Database; // Make sure your Database class is properly named
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.UserSession;
import models.TextValidation;

public class FinishAccountSetupController extends PageController {
    private Database db;

    // Default constructor, called by FXMLLoader
    public FinishAccountSetupController() {
        super(); // Call the default constructor of PageController
    }

    // Constructor with Stage and Database
    public FinishAccountSetupController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

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

    // Method to finalize the account setup
    @FXML
    private void finishSetup() {
        db = Database.getInstance();

        // Retrieve the input values
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

        // Check if the user is null
        if (user == null) {
            errorLabel.setText("No user is currently logged in. Please log in again.");
            return; // Early exit to prevent NullPointerException
        }

        // Update user object with additional information
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPreferredName(preferredName);

        try {
            // Update user in the database
            db.updateUser(user.getUsername(), firstName, lastName, preferredName, email);
            redirectToSelectRolePageView(); // Navigate to role selection page after successful setup
        } catch (SQLException e) {
            errorLabel.setText("Database update failed: " + e.getMessage());
        }
    }

    private void redirectToSelectRolePageView() {
        navigateTo("/views/SelectRolePageView.fxml");
    }

    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);

        User user = UserSession.getInstance().getCurrentUser();
        if (user == null) {
            // Handle the case where no user is logged in
            errorLabel.setText("No user is currently logged in. Please log in again.");
        } else {
            // Proceed with normal operations
            System.out.println("Current user: " + user.getUsername());
        }
    }
}


