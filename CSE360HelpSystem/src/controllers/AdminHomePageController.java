package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.TextValidation;
import models.User;
import models.UserSession;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: AdminHomePageController class </p>
 * 
 * <p> Description: a JavaFX controller that manages the functionality of the admin home page in this application. 
 *     This class extends the PageController base class, allowing it to inherit common page-related behavior
 *     while introducing admin-specific actions. </p>
 * 
 * @author Alyssa DiTroia
 * 
 */
public class AdminHomePageController extends PageController {
    // Default constructor, needed for FXML 
    public AdminHomePageController() {
        super();
    }

    // Constructor with Stage and Database
    public AdminHomePageController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    @FXML
    private Label adminHomeTitle;

    @FXML
    private CheckBox admin;

    @FXML
    private CheckBox student;

    @FXML
    private CheckBox instructor;
    
    @FXML
    private TextField emailField;

    @FXML
    private Button sendInviteButton;

    @FXML
    private Button logoutButton;

    /**
     * handleInvite()
     * Handles when the generate invite button is pressed
     * This method will generate an invite and add a user to the database
     */
    @FXML
    public void handleInvite() {
        // Validate the email field
        String email = emailField.getText();
        String validationResult = TextValidation.validateEmail(email);

        if (!validationResult.isEmpty()) {
            // Email is invalid, show alert and return
            showAlert("Invalid Email", validationResult, "");
            return;
        }

        // Check to make sure permissions are set using checkboxes
        if (!admin.isSelected() && !student.isSelected() && !instructor.isSelected()) {
            showAlert("No permissions selected!", "Please select at least one permission.", "");
            return;
        }

        // Generate a random invite token (5 characters long)
        String inviteToken = generateInviteToken();

        // Get the permissions from checkboxes
        boolean isAdmin = admin.isSelected();
        boolean isStudent = student.isSelected();
        boolean isInstructor = instructor.isSelected();

        // Create a new user
        User newUser = new User(); // Using the default constructor

        // Set permissions for the User object based on selected checkboxes
        setPermissions(newUser, inviteToken);

        try {
            // Call inviteUser function to add the user into the database
        	// The new user is added with their invite code and permissions
        	db = Database.getInstance();
            String generatedInviteCode = db.inviteUser(inviteToken, isAdmin, isStudent, isInstructor);
            showAlert("User Invited!", "User invited with invite code: " + generatedInviteCode, "Invite sent to: " + email);
            System.out.println("User invited with invite code: " + generatedInviteCode);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while accessing the database.", "");
        }
    }

    /**
     * logout()
     *  Method for logout functionality
     *  Redirects to login page
     */
    @FXML
    public void logout() {
        System.out.println("Admin logged out.");
        navigateTo("/views/LoginPageView.fxml");
    }

    /**
     * Sets the permissions for the user object
     * @param user
     * @param inviteToken
     */
    public void setPermissions(User user, String inviteToken) {
        // Depending on the state of the checkboxes, set the user's permissions accordingly
        user.setAdmin(admin.isSelected());
        user.setStudent(student.isSelected());
        user.setInstructor(instructor.isSelected());
        user.setInviteToken(inviteToken);
    }

    /**
     * Generate a random string of 5 characters including numbers and letters
     * @return
     */
    private String generateInviteToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(characters.charAt(random.nextInt(characters.length())));
        }
        return token.toString();
    }

    private List<String> alertMessages = new ArrayList<>();

    /**
     * Shows alerts for the Admin 
     * @param title
     * @param message
     */
    private void showAlert(String title, String message, String message2) {
        // Store both messages for verification (optional)
        alertMessages.add(message);
        alertMessages.add(message2);

        // Create an alert and set its properties
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        // Concatenate the messages and set them as the alert content
        alert.setContentText(message + "\n" + message2); // "\n" adds a line break between the two messages
        
        alert.showAndWait();
    }
    /**
     * Function to initialize the database for this specific controller
     */
    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);
    }

}

