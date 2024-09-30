package controllers;

/****
 * Focuses on UI logic, event handling, and communicating between the view and model.
 * TBH i am not sure if this is doing anything anymore but I am too scared to delete lol
 *
 */
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class PageController {
    // Method to navigate to a different view
    public void navigateTo(String fxmlPath) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            // Get the current stage (window) and set the new scene
            Stage currentStage = (Stage) newView.getScene().getWindow();
            currentStage.setScene(new Scene(newView, 800, 600)); // You can set your desired width and height

            // Show the new scene
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void handlePageLogic();

    public void showError(String message) {
        System.out.println("Error: " + message);  // You can replace this with an actual UI alert
    }


    }
