package controllers;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminHomePageController extends PageController {
	@FXML
    private Button logoutButton;

    @FXML
    public void logout() {
        System.out.println("Admin logged out.");
        navigateToLoginPage();
    }

    private void navigateToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginPageView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void handlePageLogic() {
		// TODO Auto-generated method stub

	}


}
