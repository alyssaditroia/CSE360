package controllers;

import java.sql.SQLException;

import database.Database;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.TextValidation;
import models.User;
import models.UserSession;
/**
 *  LoginPageController
 *  Implements login page
 */
public class LoginPageController extends PageController {
	private Stage stage;
	private User user;
    // FXML elements 
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField inviteCodeField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;

    // Default constructor, called by FXMLLoader
    public LoginPageController() {
        super(); // Call the default constructor of PageController
    }

    // Constructor with Stage and Database
    public LoginPageController(Stage primaryStage, Database db) {
        super(primaryStage, db);
        this.stage = primaryStage;
    }


    /**
     *  Method to handle login action
     */
    @FXML
    public void handleLogin() {
        db = Database.getInstance();
        // Clear previous error messages
        errorLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate input fields
        String usernameError = TextValidation.validateUsername(username);
        String passwordError = TextValidation.validatePassword(password);

        if (!usernameError.isEmpty()) {
            showError(usernameError);
            return;
        }
        if (!passwordError.isEmpty()) {
            showError(passwordError);
            return;
        }

        try {
            // Check if the database is empty
            if (db.isDatabaseEmpty()) {
                setupAdministrator(); // Setup admin if database is empty
                return; // Exit after setting up the administrator
            }

            // Validate user credentials from database
            if (db.validateCredentials(username, password)) {
                // Fetch user details and check if account is fully set up
                String firstName = db.getFirstName(username);
                if (firstName == null || firstName.isEmpty()) {
                	User loggedInUser = new User();
                	loggedInUser.setUsername(username);
                	// After successful login in LoginPageController
                	UserSession userSession = UserSession.getInstance();
                	userSession.setCurrentUser(loggedInUser); // Assume loggedInUser is of type User
                	navigateTo("/views/FinishAccountSetupView.fxml");

                    redirectToFinishAccountSetup(); // Redirect to account setup if not completed
                    return;  // Exit after redirection
                }

                // Successful login, set user session, and redirect to role selection
                User loggedInUser = new User();
                loggedInUser.setUsername(username);
                UserSession.getInstance().setCurrentUser(loggedInUser);
                System.out.println("User logged in: " + UserSession.getInstance().getUsername());
                redirectToSelectRolePageView();
            } else {
                // Show error if credentials are invalid
                showError("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while processing your request.");
        }
    }

 private void redirectToFinishAccountSetup() {
    navigateTo("/views/FinishAccountSetupView.fxml");
		
}

	@FXML
    public void setupAdministrator() {
        db = Database.getInstance();
        errorLabel.setText(""); // Clear previous error messages
        statusLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Create administrator in the database
            db.setupAdministrator(username, password);
            // Redirect to login page after admin setup
            redirectToLoginPageView();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while trying to create an administrator.");
        }
    }

    private void redirectToLoginPageView() {
        statusLabel.setText("Account setup successful! Redirecting to login...");
        statusLabel.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("/views/LoginPageView.fxml"));
        pause.play();
    }

    @FXML
    public void redirectToSetupAccount() {
        navigateTo("/views/SetupAccountPageView.fxml");
    }

    @FXML
    public void redirectToSelectRolePageView() {
        navigateTo("/views/SelectRolePageView.fxml");
    }

    @FXML
    public void logout() {
        System.out.println("Logged out successfully.");
        navigateTo("/views/LoginPageView.fxml");
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    // Optionally override the initialize method if needed
    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);
        // Additional initialization if necessary
    }
}



