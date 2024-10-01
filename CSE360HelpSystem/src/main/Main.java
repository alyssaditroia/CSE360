package main;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

import controllers.LoginPageController;
import controllers.PageController;
import database.Database;

public class Main extends Application {

    private PageController pageController;
    private Database db = new Database();

    @Override
    public void start(Stage primaryStage) {
        try {
            db.connectToDatabase();

            // Check if the database is empty (no users registered)
            if (db.isDatabaseEmpty()) {
                System.out.println("In-Memory Database is empty");
                // Navigate to the LoginPageController to allow admin setup
                this.pageController = new LoginPageController(primaryStage); // Initialize with the primary stage
                showLoginPage(); // Show the login page where admin can enter their details
                primaryStage.setTitle("CSE360 Help System");
                primaryStage.show();
                return; // Exit after navigating to login page
            }

            // Initialize the page controller after database check
            this.pageController = new PageController(primaryStage);
            primaryStage.setTitle("CSE360 Help System");
            showLoginPage();
            primaryStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showLoginPage() {
        pageController.navigateTo("/views/LoginPageView.fxml");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Close database connection when the application stops
        if (db != null) {
            db.closeConnection();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


