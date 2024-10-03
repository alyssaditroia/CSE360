package tests;

import controllers.AdminHomePageController;
import controllers.PageController;
import database.Database;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class AdminHomePageControllerTest {

    private AdminHomePageController controller;
    private Database db;
	private Stage stage;

        @BeforeEach
        public void setUp() throws Exception {
            db = new Database(); // Initialize your database here
            db.connectToDatabase(); // Ensure a test database connection

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminHomePageView.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            
            stage = new Stage(); // Create a new Stage instance
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // Show the stage for UI tests
        }
        

    @Test
    public void testHandleInviteNoPermissions() {

    }

    @Test
    public void testHandleInviteSuccess() throws Exception {

    }

    // Add more test cases as needed
}



