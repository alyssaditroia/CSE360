/**
 * 
 */
/**
 * 
 */
package controllers;

import java.io.IOException;
import java.sql.SQLException;

import database.database; // Make sure your Database class is properly named
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.TextValidation;

public class finishAccountSetupController extends PageController {

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

    @FXML
    public void redirectToSelectRolePageView() {
        // Ensure that the button is already part of the scene
        Stage stage = (Stage) finishSetupButton.getScene().getWindow(); // Get the current stage
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
    private User user; // The user object

    @Override
    public void handlePageLogic() {
        // Any additional logic that needs to be handled on page load
    }

    // Method to set the user for this setup
    public void setUser(User user) {
        this.user = user;
    }

    // Method to finalize the account setup
    @FXML
    private void finishSetup() throws SQLException {
        // Retrieve the input values
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String preferredName = preferredNameField.getText();

        // Validate inputs using the TextValidation Class
        TextValidation validator = new TextValidation();
        if (!validator.validateEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }
        if (firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("First name and last name cannot be empty.");
            return;
        }
        // Update user Object with additional information
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPreferredName(preferredName);

        database db = new database();
		db.updateUser(user); // Update User, the user should already exist
		redirectToSelectRolePageView(); // Navigate to role selection page after successful setup
    }
   
}
