package main;

import java.sql.SQLException;

import controllers.LoginPageController;
import controllers.PageController;
import database.Database;
import database.HelpArticleDatabase;
import javafx.application.Application;
import javafx.stage.Stage;

/***********
 * 
 * Main Class
 * 
 * @version v.1
 * @author Alyssa DiTroia
 **************/

public class Main extends Application {

	private Database db;
	private HelpArticleDatabase had;
	private PageController pageController;

	@Override
	public void start(Stage primaryStage) {
		try {
			db = new Database();
			db.connectToDatabase();

			// Check if the database is empty (no users registered)
			if (db.isDatabaseEmpty()) {
				System.out.println("In-Memory Database is empty. Navigating to login page for admin setup.");
				// Initialize the LoginPageController and pass the database instance
				LoginPageController loginPageController = new LoginPageController(primaryStage, db);
				pageController = loginPageController; // Set the page controller to the login controller
				pageController.navigateTo("/views/LoginPageView.fxml"); // Show the login page for admin setup
			} else {
				System.out.println("Database is not empty. Navigating to login page.");
				// Initialize the main page controller with the database instance
				LoginPageController loginPageController = new LoginPageController(primaryStage, db);
				pageController = loginPageController;

				pageController.navigateTo("/views/LoginPageView.fxml");
			}

			// Set the title for the primary stage
			primaryStage.setTitle("CSE360 Help System");
			primaryStage.show();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Database connection failed. Please check your database configuration.");
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		System.err.println("Closing database connection.");
		// Close database connection when the application stops
		if (db != null) {
			db.closeConnection();
			System.err.println("User Database connection closed.");
		}
		if (had != null) {
			had.closeConnection();
			System.err.println("Help Article Database connection closed.");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}






