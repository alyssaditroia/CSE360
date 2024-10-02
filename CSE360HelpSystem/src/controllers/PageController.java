package controllers;

import java.io.IOException;

import database.Database;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.UserSession;

public class PageController {
    protected Stage stage; // Protected to allow access in subclasses
    protected Database db; // Make this protected if needed in subclasses

    // Default constructor, called by FXMLLoader
    public PageController() {
        // This constructor will be called by FXMLLoader
    }

    // Constructor to initialize the primary stage and database
    public PageController(Stage primaryStage, Database db) {
        this.stage = primaryStage;
        this.db = db;

        // Access the username from UserSession
        String username = UserSession.getInstance().getUsername();
        System.out.println("Current user: " + username);
    }

 // Method to navigate to a different view
    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load(); // Load the new FXML view
            
            // Get the controller after loading the FXML
            PageController newController = loader.getController(); 

            // Pass the stage and database to the new controller
            newController.initialize(this.stage, this.db);

            // Set the new scene on the stage
            Scene newScene = new Scene(newView);
            this.stage.setScene(newScene);
            this.stage.setTitle("CSE360 Help System");
            this.stage.show(); // Optional, but it's a good practice to show the stage
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the page: " + e.getMessage());
        }
    }


    // Method to show error alerts to the user
    public void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Initialize method can be overridden in subclasses
    public void initialize(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
    }
}
