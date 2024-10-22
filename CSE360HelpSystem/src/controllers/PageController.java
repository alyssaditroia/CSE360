package controllers;

import java.io.IOException;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.UserSession;

/***********
 * 
 * <p>
 * Title: PageController
 * </p>
 * 
 * <p>
 * The page controller handles navigation, sessions, and errors across all of
 * its subclasses
 * </p>
 * 
 * Helpful Universal Controller Methods:
 * 
 * - navigateTo("/views/toView.fxml")
 * - showError(message) -> Shows an error pop up with a message passed in as a string
 * - logout() -> Logs the user out and resets the user session
 * - goHome() -> Navigates the user to their home page based on their current selected role
 * - showErrorAlert(title, message) -> Shows error pop up with a Title and Message (Strings)
 * 
 * 
 **************/
public class PageController {
	protected Stage stage; // Protected to allow access in subclasses
	protected Database db; // Make this protected if needed in subclasses

	// Default constructor, called by FXMLLoader
	public PageController() {
	}

	// Constructor to initialize the primary stage and database
	public PageController(Stage primaryStage, Database db) {
		this.stage = primaryStage;
		this.db = db;

		// Access the username from UserSession
		String username = UserSession.getInstance().getUsername();
		System.out.println("Current user: " + username);
	}

	public void setUserSession(UserSession userSession) {
		// Optionally handle setting the user session or any specific user
		UserSession.setInstance(userSession);
	}

	// Method to navigate to a different view
	public void navigateTo(String fxmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent newView = loader.load(); // Load the new FXML view

			// Get the controller after loading the FXML
			PageController newController = loader.getController();

			// Pass the stage and database to the new controller
			newController.initialize(this.stage, this.db);

			// Set the new scene on the stage
			Scene newScene = new Scene(newView);
			this.stage.setScene(newScene);
			this.stage.setTitle("CSE360 Help System");
			this.stage.show(); // Optional, but it's a good practice to show the stage
		} catch (IOException e) {
			e.printStackTrace();
			showError("Failed to load the page: " + e.getMessage());
		}
	}

	// Method to show error alerts to the user
	public void showError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void logout() {
        // Reset the UserSession to null
        UserSession.setInstance(null);

        // Navigate to the login page
        navigateTo("/views/LoginPageView.fxml");  // Make sure this path is correct based on your project structure
    }
    /**
     * Navigate to AdminHomePage
     */
    @FXML
    public void goToAdminHomepage() {
        navigateTo("/views/AdminHomePageView.fxml");
    }
    /**
     * Navigate to Instructor Home Page
     */
    @FXML
    public void goToInstructorHomepage() {
        navigateTo("/views/InstructorHomePageView.fxml");
    }
    /**
     * Navigate to Student Homepage
     */
    @FXML
    public void goToStudentHomepage() {
        navigateTo("/views/InstructorHomePageView.fxml");
    }
    @FXML
    public void goHome() {
        String currentRole = UserSession.getInstance().getCurrentRole();
        
        if (currentRole == null) {
            System.out.println("Error: User session not set or current role is null.");
            showErrorAlert("Session Error", "The user session is not set. Please log in again.");
            navigateTo("/views/LoginPageView.fxml"); // Navigate to login page
            return; // Stop further execution
        }
        
        // Check the role and navigate accordingly
        if ("admin".equals(currentRole)) { 
            goToAdminHomepage();
        } else if ("instructor".equals(currentRole)) {
            goToInstructorHomepage();
        } else {
            goToStudentHomepage();
        }
    }
    
    


	// Initialize method can be overridden in subclasses
	public void initialize(Stage stage, Database db) {
		this.stage = stage;
		this.db = db;
	}
}
