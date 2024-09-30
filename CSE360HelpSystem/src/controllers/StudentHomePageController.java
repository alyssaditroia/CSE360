package controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StudentHomePageController extends PageController {

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

	@Override
	public void handlePageLogic() {
		// TODO Auto-generated method stub

	}
}
