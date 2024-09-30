package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InstructorHomePageController extends PageController {

	public InstructorHomePageController() {
		super(null);
		// TODO Auto-generated constructor stub
	}

    public InstructorHomePageController(Stage primaryStage) {
		super(primaryStage);
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

	@Override
	public void handlePageLogic() {
		// TODO Auto-generated method stub

	}

}
