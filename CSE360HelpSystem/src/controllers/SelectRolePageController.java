package controllers;

import java.sql.*;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.User;
import models.UserSession;

/**
 * <p>
 * The {@code SelectRolePageController} handles the user input for selecting their role for the current session
 * </p>
 *
 * <p>
 * Description: A JavaFX controller that manages the functionality of the role
 * selection page in this application. This class extends the PageController
 * base class, allowing it to inherit common page-related behavior while
 * introducing role selection-specific actions. It handles the display of
 * role-specific buttons based on the current user's permissions and manages
 * navigation to the appropriate role-specific homepages.
 * </p>
 *
 * <p>
 * The controller interacts with the Database class to retrieve user role
 * information and uses the UserSession to manage the current user's data. It
 * provides methods for redirecting to Admin, Instructor, and Student homepages,
 * as well as navigation back to the login page.
 * </p>
 *
 * @author Justin Faris
 *
 */
public class SelectRolePageController extends PageController {
	/** Database instance */
	Database db;

	/** FXML annotated buttons for each possible role a user can have */
	@FXML
	private Button adminButton;

	@FXML
	private Button instructorButton;

	@FXML
	private Button studentButton;

	/** Button to navigate back */
	@FXML
	private Button backButton; // Button to navigate back

	/** Current logged-in user accessing the system */
	private User currentUser; // handling users

	// Needs current user session from previous page

	/**
	 * Default Constructor
	 */
	public SelectRolePageController() {
		super();
	}

	/**
	 * Constructor with stage and database parameters
	 * 
	 * @param primaryStage The primary stage of the application
	 * @param db           The database instance being used
	 */
	public SelectRolePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

	/**
	 * Initializes the controller for the FXML page
	 */
	@FXML
	public void initialize() {
		if (db == null) {
			db = Database.getInstance();
		}
		currentUser = UserSession.getInstance().getCurrentUser();
		if (currentUser != null) {
			setButtonVisibility();
		} else {
			System.out.println("Current user is null");
			navigateTo("/views/LoginPageView.fxml");
		}
	}

	/**
	 * Sets the visibility of the role buttons so that only the button options for
	 * roles that the user has privileges for are displayed
	 */
	private void setButtonVisibility() {
		try {
			Boolean isAdmin = db.isUserAdmin(currentUser.getUsername());
			Boolean isInstructor = db.isUserInstructor(currentUser.getUsername());
			Boolean isStudent = db.isUserStudent(currentUser.getUsername());

			adminButton.setVisible(isAdmin != null && isAdmin);
			instructorButton.setVisible(isInstructor != null && isInstructor);
			studentButton.setVisible(isStudent != null && isStudent);
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Error retrieving user roles.");
		}
	}

	/**
	 * Redirects to the Admin homepage
	 */
	@FXML
	public void redirectToAdminHomepage() {
		String currentRole = "admin";
		UserSession userSession = UserSession.getInstance(); // Getting the userSession Instance
		userSession.setCurrentRole(currentRole);
		navigateTo("/views/AdminHomePageView.fxml");
	}

	/**
	 * Redirects to the Instructor homepage
	 */
	@FXML
	public void redirectToInstructorHomepage() {
		String currentRole = "instructor";
		UserSession userSession = UserSession.getInstance(); // Getting the userSession Instance
		userSession.setCurrentRole(currentRole);
		navigateTo("/views/InstructorHomePageView.fxml");
	}

	/**
	 * Redirects to the Student homepage
	 */
	@FXML
	public void redirectToStudentHomepage() {
		String currentRole = "student";
		UserSession userSession = UserSession.getInstance(); // Getting the userSession Instance
		userSession.setCurrentRole(currentRole);
		navigateTo("/views/StudentHomePageView.fxml");
	}

	/**
	 * Navigates back to the login page
	 */
	@FXML
	public void goBack() {
		navigateTo("/views/LoginPageView.fxml");
	}

	// Optionally override the initialize method if needed
	@Override
	public void initialize(Stage stage, Database db) {
		super.initialize(stage, db);
		initialize();
	}

}
