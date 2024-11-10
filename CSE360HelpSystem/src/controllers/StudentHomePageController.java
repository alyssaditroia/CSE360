package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * <p>
 * Title: Student Home Page Controller
 * </p>
 * 
 * <p>
 * Description: Home page for individuals with Student permissions
 * </p>
 * 
 * 
 * @author Alyssa DiTroia
 */
public class StudentHomePageController extends PageController {
	/**
     * Database instance
     */
	Database db;
	
	@FXML
	private Button logoutButton;
	
	@FXML
	private Button generalGroupButton;

	// Default constructor of FXML
	public StudentHomePageController() {
		super();
	}

	// Constructor
	public StudentHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}
	
	/**
	 * Navigates to the backup and restore view
	 */
	@FXML
	private void navigateToGeneralGroup() {
	    navigateTo("/views/StudentGeneralView.fxml");
	}

	// Logout functionality
	@FXML
	public void logout() {
		System.out.println("Student logged out.");
		navigateTo("/views/LoginPageView.fxml");
	}
	
	@FXML
	public void goToMessagingSystem() {
		navigateTo("/views/MessagingSystemView.fxml"); 
	}

}
