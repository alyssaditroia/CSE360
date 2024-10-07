package controllers;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * <p> Title: Student Home Page Controller </p>
 * 
 * <p> Description: Home page for individuals with Student permissions </p>
 * 
 * 
 * @author Alyssa DiTroia
 */
public class StudentHomePageController extends PageController {
	 Database db;
	
	 //Default constructor of FXML
	public StudentHomePageController() {
			super();
		}

	// Constructor
    public StudentHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

	@FXML
    private Button logoutButton;

    // Logout functionality
    @FXML
    public void logout() {
        System.out.println("Student logged out.");
        navigateTo("/views/LoginPageView.fxml");
    }

}
