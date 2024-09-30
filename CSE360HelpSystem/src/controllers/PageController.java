package controllers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PageController {
    protected Stage stage; // Change to protected to allow subclasses to access

    // Constructor to initialize the primary stage
    public PageController(Stage primaryStage) {
        this.stage = primaryStage;
    }

    // Method to navigate to a different view
    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            // Get the controller instance and pass the stage if needed
            Object controller = loader.getController();
            if (controller instanceof PageController) {
                ((PageController) controller).stage = this.stage; // Share the stage with subclasses
            }

            Scene newScene = new Scene(newView);
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the page.");
        }
    }

    public void showError(String message) {
        System.out.println("Error: " + message); // You can replace this with an actual UI alert
    }
}

