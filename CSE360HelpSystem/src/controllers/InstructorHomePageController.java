package controllers;

import java.io.IOException;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InstructorHomePageController extends PageController {
	Database db;
	
	//Default constructor for FXML loader
	public InstructorHomePageController() {
		super();
	}

    public InstructorHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private Button logoutButton;

    @FXML
    public void logout() {
        System.out.println("Instructor logged out.");
        navigateToLoginPage();
    }

    private void navigateToLoginPage() {
        navigateTo("/views/LoginPageView.fxml");
    }


}
