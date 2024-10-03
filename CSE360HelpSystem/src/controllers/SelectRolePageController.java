package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.User;

public class SelectRolePageController extends PageController{
	Database db;
	public SelectRolePageController() {
		super();
	}
	    public SelectRolePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

		@FXML
	    private ComboBox<String> roleComboBox; // ComboBox for selecting a role

	    @FXML
	    private Button selectRoleButton; // Button to confirm the role selection

	    @FXML
	    private Button backButton; // Button to navigate back

	    private User currentUser; // handling users
	    
	    // Needs current user session from previous page


	    // Method to initialize the view components
	    @FXML
	    public void initialize() {
	        // Populate the ComboBox with available roles based on the boolean values
	        if (currentUser.isAdmin()) {
	            roleComboBox.getItems().add("Admin");
	        }
	        if (currentUser.isStudent()) {
	            roleComboBox.getItems().add("Student");
	        }
	        if (currentUser.isInstructor()) {
	            roleComboBox.getItems().add("Instructor");
	        }
	    }

	    // Method to handle role selection
	    @FXML
	    public void selectRole() {
	        String selectedRole = roleComboBox.getValue();
	        if (selectedRole != null) {
	            // Handle the role selection (e.g., navigate to the corresponding home page)
	            System.out.println("Selected Role: " + selectedRole); // Replace with actual navigation logic
	            navigateTo("/views/" + selectedRole + "HomePageView.fxml"); // Example: navigate to corresponding home page
	        } else {
	            // Show an error message (optional)
	            System.out.println("Please select a role."); // Replace with UI feedback
	        }
	    }

	    // Method to navigate back to the previous page
	    @FXML
	    public void goBack() {
	        // Handle navigation back (adjust the path as needed)
	        navigateTo("/views/LoginPageView.fxml");
	    }


	}
