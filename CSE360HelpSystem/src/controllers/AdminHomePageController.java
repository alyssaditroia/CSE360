package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * AdminHomePageController
 * Receives user input
 * Updates model(s)
 * Selects and displays the view
 */
public class AdminHomePageController extends PageController {
	private Database db;
	
	// Default constructor, called by FXMLLoader
	public AdminHomePageController() {
        super();
    }
    // Constructor with Stage and Database
    public AdminHomePageController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }
	// Button for logout functionality
	@FXML
    private Button logoutButton;
	
	// Method for logout functionality
    @FXML
    public void logout() {
        System.out.println("Admin logged out.");
        navigateTo("/views/LoginPageView.fxml");
    }



}
