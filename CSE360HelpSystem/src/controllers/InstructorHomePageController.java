package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
/**
 * <p> Title: InstructorHomePageController </p>
 * 
 * <p> Description: Home page for individuals with instructor permissions </p>
 */
public class InstructorHomePageController extends PageController {
	Database db;
	
	//Default constructor for FXML loader
	public InstructorHomePageController() {
		super();
	}

    public InstructorHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

	@FXML
    private Button logoutButton;

    @FXML
    public void logout() {
        System.out.println("Instructor logged out.");
        navigateTo("/views/LoginPageView.fxml");
    }


}
