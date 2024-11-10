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
	/**
	 * Stage instance used for scene navigation
	 */
	protected Stage stage; 
	
	/**
	 * Database instance for data operations
	 */
	protected Database db; 

	/**
	 * Default constructor required for FXML loader
	 */
	public PageController() {
	}

	/**
	 * Constructor with stage and database parameters
	 * 
	 * @param primaryStage the main application window
	 * @param db the database instance
	 */
	public PageController(Stage primaryStage, Database db) {
		this.stage = primaryStage;
		this.db = db;

		// Access the username from UserSession
		String username = UserSession.getInstance().getUsername();
		System.out.println("Current user: " + username);
	}

	/**
	 * Sets the user session for the current page
	 * 
	 * @param userSession the session to be set
	 */
	public void setUserSession(UserSession userSession) {
		// Optionally handle setting the user session or any specific user
		UserSession.setInstance(userSession);
	}

	/**
	 * Navigates to a different view within the application
	 * 
	 * @param fxmlPath the path to the FXML file to load
	 */
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

	/**
	 * Shows an error alert with a single message
	 * 
	 * @param message the error message to display
	 */
	public void showError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	/**
	 * Shows an error alert with a title and message
	 * @param title the title of the error alert
	 * @param message the error message to display
	 */
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Logs out the current user and navigates to the login page
     */
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
        navigateTo("/views/StudentHomePageView.fxml");
    }
    
    /**
     * Navigates user to their appropriate homepage based on their current role.
     * Shows error and returns to login page if session is invalid.
     */
    @FXML
    public void goHome() {
        String currentRole = UserSession.getInstance().getCurrentRole();
        System.out.println("Current role in goHome(): " + currentRole);

        if (currentRole == null) {
            System.out.println("Error: User session not set or current role is null.");
            showErrorAlert("Session Error", "The user session is not set. Please log in again.");
            navigateTo("/views/LoginPageView.fxml"); // Navigate to login page
            return; // Stop further execution
        }

        // Check the role and navigate accordingly
        if ("admin".equals(currentRole)) {
            System.out.println("Navigating to Admin Homepage");
            goToAdminHomepage();
        } else if ("instructor".equals(currentRole)) {
            System.out.println("Navigating to Instructor Homepage");
            goToInstructorHomepage();
        } else {
            System.out.println("Navigating to Student Homepage");
            goToStudentHomepage();
        }
	}
    

    /**
     * Initializes the controller with stage and database instances
     * 
     * @param stage the application window
     * @param db the database instance
     */
	public void initialize(Stage stage, Database db) {
		this.stage = stage;
		this.db = db;
	}
}
