package controllers;

import java.sql.SQLException;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.UserSession;
import models.TextValidation;
/**
 * <p> Title: SetupAccountPageController </p>
 * <p> Description: Controls the view for the page where the user's account is being setup </p>
 */
public class SetupAccountPageController extends PageController {
  private Database db;
  // Constructor with for FXMLLoader
  public SetupAccountPageController() {
    super();
  }

  // Constructor with dependency injection for Stage and Database
  public SetupAccountPageController(Stage primaryStage, Database db) {
    super(primaryStage, db);
  }

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private Button setupButton;

  @FXML
  private Label statusLabel;

  /**
   * handleSetupButtonAction()
   * Below is the implementation for handling the user's account setup and updating their information in the database
   * The details are validated and the user's information associated with that invite code is updated in the database
   * The invite code is set to null after use
   * 
   */
  @FXML
  private void handleSetupButtonAction() {
    db = Database.getInstance(); // Get the current instance of the Database
    statusLabel.setText(""); // Clear previous status messages
    UserSession currentUser = UserSession.getInstance(); // Get the current user instance
    String inviteCode = currentUser.getInviteCode(); // Set the current user instance invite code
    System.out.println("Current user invite code: " + inviteCode);

    // Trim whitespace from username field
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    // Validate the username and password using TextValidation model
    String validationMessage = TextValidation.validateSetupFields(username, password, confirmPassword);
    // If the username and password properly meet the requirements then the user will be updated in the database
    if (validationMessage.isEmpty()) {
      try {
        User newUser = new User("", "", "", "", username, password.toCharArray(), null, null, null);
        db.completeInvite(inviteCode, username, password);

        // Success message will show when user was properly updated in the database
        statusLabel.setText("Account setup successful! Redirecting to login...");
        statusLabel.setStyle("-fx-text-fill: green;");

        // Create a PauseTransition for 2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
          // Redirect to login page after 2 seconds
          redirectToLogin();
        });
        pause.play(); // Start the pause
      } catch (SQLException e) {
        e.printStackTrace();
        statusLabel.setText("error setting up account"); // If user is not properly setup then an error will show
        statusLabel.setStyle("-fx-text-fill: red;");

      }
    } else {
      // If the user's password or username does not meet the criteria
      // Display the validation error message in the GUI
      statusLabel.setText(validationMessage);
      statusLabel.setStyle("-fx-text-fill: red;");
    }
  }

  private void redirectToLogin() {
    navigateTo("/views/LoginPageView.fxml");
  }
}