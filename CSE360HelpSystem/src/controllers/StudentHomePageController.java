package controllers;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Student Home Page Controller Class
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

    @FXML
    public void logout() {
        System.out.println("Student logged out.");
        goBack();
    }

 // Method to navigate back to the Login Page
    @FXML
    public void goBack() {
        // Handle navigation back (adjust the path as needed)
        navigateTo("/views/LoginPageView.fxml");
    }

}
