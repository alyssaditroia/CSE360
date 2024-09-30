package controllers;

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
	public AdminHomePageController() {
        super(null);
    }
	// Constructor
    public AdminHomePageController(Stage primaryStage) {
        super(primaryStage);
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

	@Override
	public void handlePageLogic() {
		// TODO Auto-generated method stub

	}


}
