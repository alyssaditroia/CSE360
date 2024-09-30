package controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Student Home Page Controller Class
 */
public class StudentHomePageController extends PageController {
	 public StudentHomePageController() {
			super(null);
		}

    public StudentHomePageController(Stage primaryStage) {
		super(primaryStage);
	}

	@FXML
    private Button logoutButton;

    @FXML
    public void logout() {
        System.out.println("Student logged out.");
        goBack();
    }

 // Method to navigate back to the previous page
    @FXML
    public void goBack() {
        // Handle navigation back (adjust the path as needed)
        navigateTo("/views/LoginPageView.fxml");
    }

}
