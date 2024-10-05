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
import models.OTP;
import models.TextValidation;
import models.User;
import models.UserSession;
/**
 *  LoginPageController
 *  Implements login page
 */
public class LoginPageController extends PageController {
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
  private Button inviteButton;

  @FXML
  private Label errorLabel;

  @FXML
  private Label statusLabel;

  // Default constructor, called by FXMLLoader
  public LoginPageController() {
    super(); 
  }

  // Constructor with Stage and Database
  public LoginPageController(Stage primaryStage, Database db) {
    super(primaryStage, db);
  }

  /**
   *  ****** BELOW HANDLES WHEN THE USER LOGS IN FOR ALL CASES *******
   *  1. USER IS FIRST EVER USER = ADMIN
   *  2. USER IS LOGGING IN FOR THE FIRST TIME AND HAS NOT FINISHED ACCOUNT SETUP
   *  3. USER IS FULLY SET UP AND LOGGING IN
   */
  @FXML
  public void handleLogin() {
    db = Database.getInstance();
    errorLabel.setText(""); // Clearing any previous error messaages

    String username = usernameField.getText(); // Fetch username from the text field
    String password = passwordField.getText(); // Fetch password from text field

    // Validate username and password fields to ensure they are not empty
    String usernameError = TextValidation.isFieldEmpty(username);
    String passwordError = TextValidation.isFieldEmpty(password);

    // If the error message returned from text validation is not an empty string then display an error
    if (!usernameError.isEmpty()) {
      showError(usernameError);
      return;
    }
    // If the error message returned from text validation is not an empty string then display an error
    if (!passwordError.isEmpty()) {
      showError(passwordError);
      return;
    }

    // Check if the database is empty. If the database is empty then setup the admin account
    try {
      if (db.isDatabaseEmpty()) {
        setupAdministrator();
        return;
      }

      // Check the user's credentials with the database and ensure they match
      if (db.validateCredentials(username, password)) {
        String firstName = db.getFirstName(username); // Fetch user's first name to check if account is fully set up
        
        // ****** BELOW CHECKS IF THE USER'S PASSWORD IS A ONE TIME PASSWORD *******
        Boolean otpFlag = db.getOTPFlag(username);
        if(otpFlag == true) {
        OTP otp = new OTP();
        String validOTP = otp.validateOTP(username, password);
        if(validOTP == "") {
        	User loggedInUser = new User();
        	loggedInUser.setUsername(username);
        	otp.invalidateOTP(username);
        	UserSession userSession = UserSession.getInstance(); // Getting the userSession Instance
            userSession.setCurrentUser(loggedInUser); 
        	redirectToUpdatePassword();
        	return;
        }else {
        	System.out.println("OTP VALIDATION MESSAGE: " + validOTP);
        	// NEED TO IMPLEMENT THE CASE WHERE THE VALIDATION MESSAGE IS FALSE
        }
        }
        // ****** BELOW CHECKS IF THE USER IS FULLY SET UP OR NOT *******
        // If the user in the database associated with the username entered does not have a first name
        // The user will be navigated to the Finish Account Setup Page
        if (firstName == null || firstName.isEmpty()) {
          User loggedInUser = new User(); // Creating a new User 
          loggedInUser.setUsername(username); // Setting the username of the User
          UserSession userSession = UserSession.getInstance(); // Getting the userSession Instance
          userSession.setCurrentUser(loggedInUser); // Setting the userSession to the current user 
          // ****** FINISH SETTING UP ACCOUNT *******
          redirectToFinishSetupAccount();
          return;
        }

        // ****** BELOW IS THE LOGIC FOR WHEN THE USER LOGS IN AND IS FULLY SET UP ******
        // Otherwise if the user is fully setup, the user will be redirected to the Select Role Page View
        User loggedInUser = new User();
        loggedInUser.setUsername(username);
        loggedInUser.setInviteToken("");
        UserSession.getInstance().setCurrentUser(loggedInUser);
        System.out.println("User logged in: " + UserSession.getInstance().getUsername());

        // CHANGED: Justin Faris 10-3-24 
        // Check number of roles and redirect accordingly
        int numRoles = checkNumRoles(username);
        if (numRoles == 1) {
            redirectBasedOnSingleRole(username);
        } else {
            redirectToSelectRolePageView();
        }
      } else {
        // Show error if credentials are invalid
        showError("Invalid credentials. Please try again.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      showError("An error occurred while processing your request.");
    }
  }

  /**
   *  ****** BELOW HANDLES WHEN THE USER ENTERS AN INVITE CODE TO SETUP THEIR ACCOUNT *******
   */
  @FXML
  public void handleInviteButton() {
    db = Database.getInstance();
    // Clear previous error messages
    errorLabel.setText("");

    String inviteCode = inviteCodeField.getText();

    // Validate user credentials from database
    try {
      if (db.validateInvite(inviteCode)) {
        System.out.println("User invite code validated");
        User userWithInvite = new User();
        userWithInvite.setInviteToken(inviteCode);
        UserSession userSession = UserSession.getInstance();
        userSession.setCurrentUser(userWithInvite); // Assume loggedInUser is of type User
        userSession.setInviteCode(inviteCode);
        System.out.println("User Logged in with invite code " + inviteCode);
        redirectToSetupAccount();
        return; // Exit after redirection
      } else {
        // Show error if invite code is invalid
        showError("Invalid invite code. Please try again.");
        return;
      }
    } catch (SQLException e) {
      System.out.println("Error when trying to validate invite code");
      e.printStackTrace();
    }
  }
  /**
   *  ****** BELOW HANDLES WHEN THE USER IS THE FIRST EVER USER *******
   */
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
      redirectToLoginPageViewAdmin();
    } catch (SQLException e) {
      e.printStackTrace();
      showError("An error occurred while trying to create an administrator.");
    }
  }

private int checkNumRoles(String username) throws SQLException {
    int numRoles = 0;
    if (db.isUserAdmin(username)) numRoles++;
    if (db.isUserInstructor(username)) numRoles++;
    if (db.isUserStudent(username)) numRoles++;
    return numRoles;
}

private void redirectBasedOnSingleRole(String username) throws SQLException {
    if (db.isUserAdmin(username)) {
        navigateTo("/views/AdminHomePageView.fxml");
    } else if (db.isUserInstructor(username)) {
        navigateTo("/views/InstructorHomePageView.fxml");
    } else if (db.isUserStudent(username)) {
        navigateTo("/views/StudentHomePageView.fxml");
    }
 }

  private void redirectToLoginPageViewAdmin() {
    statusLabel.setText("Admin account setup successful! Redirecting to login...");
    statusLabel.setStyle("-fx-text-fill: green;");

    PauseTransition pause = new PauseTransition(Duration.seconds(2));
    pause.setOnFinished(event -> navigateTo("/views/LoginPageView.fxml"));
    pause.play();
  }

  @FXML
  public void redirectToSetupAccount() {
	    statusLabel.setText("Invite code validated! Redirecting to account setup...");
	    statusLabel.setStyle("-fx-text-fill: green;");

	    PauseTransition pause = new PauseTransition(Duration.seconds(2));
	    pause.setOnFinished(event -> navigateTo("/views/SetupAccountPageView.fxml"));
	    pause.play();
  }
  @FXML
  public void redirectToUpdatePassword() {
	    statusLabel.setText("One-time password validated! Redirecting to update password...");
	    statusLabel.setStyle("-fx-text-fill: green;");

	    PauseTransition pause = new PauseTransition(Duration.seconds(2));
	    pause.setOnFinished(event -> navigateTo("/views/UpdatePasswordPageView.fxml"));
	    pause.play();
  }
  @FXML
  public void redirectToFinishSetupAccount() {
	    statusLabel.setText("Login successful, finish setup required! Redirecting to finish account setup...");
	    statusLabel.setStyle("-fx-text-fill: green;");

	    PauseTransition pause = new PauseTransition(Duration.seconds(2));
	    pause.setOnFinished(event -> navigateTo("/views/FinishAccountSetupView.fxml"));
	    pause.play();
  }

  @FXML
  public void redirectToSelectRolePageView() {
	    statusLabel.setText("Login successful! Redirecting to select role page...");
	    statusLabel.setStyle("-fx-text-fill: green;");

	    PauseTransition pause = new PauseTransition(Duration.seconds(2));
	    pause.setOnFinished(event -> navigateTo("/views/SelectRolePageView.fxml"));
	    pause.play();
  }

  public void showError(String message) {
    errorLabel.setText(message);
  }

  // OVERRIDE the initialize method if needed
  @Override
  public void initialize(Stage stage, Database db) {
    super.initialize(stage, db);
  }
}