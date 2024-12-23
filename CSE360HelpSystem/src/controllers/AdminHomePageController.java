package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.OTP;
import models.TextValidation;
import models.User;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*******
 * <p>
 * Title: AdminHomePageController class
 * </p>
 * 
 * <p>
 * Description: a JavaFX controller that manages the functionality of the admin
 * home page in this application. This class extends the PageController base
 * class, allowing it to inherit common page-related behavior while introducing
 * admin-specific actions.
 * </p>
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 * 
 */
public class AdminHomePageController extends PageController {
	
	/**
	 * Constructor for AdminHomePageController with no parameters
	 */
	public AdminHomePageController() {
		super();
	}
	
	/**
	 * Constructor for AdminHomePageController with stage and database parameters
	 * 
	 * @param primaryStage the main application window
	 * @param db the database instance to be used
	 */
	public AdminHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}
	
	/**
	 * Label displaying the admin home page title
	 */
	@FXML
	private Label adminHomeTitle;

	/**
	 * Checkbox for admin permission selection
	 */
	@FXML
	private CheckBox admin;

	/**
	 * Checkbox for student permission selection
	 */
	@FXML
	private CheckBox student;

	/**
	 * Checkbox for instructor permission selection
	 */
	@FXML
	private CheckBox instructor;

	/**
	 * Text field for entering email addresses
	 */
	@FXML
	private TextField emailField;

	/**
	 * Button to trigger sending invites
	 */
	@FXML
	private Button sendInviteButton;

	/**
	 * TableView displaying user information
	 */
	@FXML
	private TableView<User> userTable;

	/**
	 * Column for displaying usernames
	 */
	@FXML
	private TableColumn<User, String> usernameColumn;

	/**
	 * Column for displaying preferred names
	 */
	@FXML
	private TableColumn<User, String> preferrednameColumn;

	/**
	 * Column for displaying email addresses
	 */
	@FXML
	private TableColumn<User, String> emailColumn;

	/**
	 * Column for displaying admin status
	 */
	@FXML
	private TableColumn<User, Boolean> adminColumn;

	/**
	 * Column for displaying student status
	 */
	@FXML
	private TableColumn<User, Boolean> studentColumn;

	/**
	 * Column for displaying instructor status
	 */
	@FXML
	private TableColumn<User, Boolean> instructorColumn;

	/**
	 * Column containing update permissions controls
	 */
	@FXML
	private TableColumn<User, Void> updatePermissionsColumn;

	/**
	 * Column containing password reset controls
	 */
	@FXML
	private TableColumn<User, Void> resetPasswordColumn;

	/**
	 * Column containing delete user controls
	 */
	@FXML
	private TableColumn<User, Void> deleteColumn;

	/**
	 * Button for logging out of the application
	 */
	@FXML
	private Button logoutButton;
	
	/**
	 * Button for viewing articles
	 */
	@FXML
	private Button viewArticlesButton;
	
	@FXML
	private TableColumn<User, Integer> idColumn;
	
	@FXML
	private Button SpecialGroupButton;
	
	@FXML
	private Button backupRestoreButton;
	
	@FXML 
	private Button MessagesButton;

	/**
	 * Handles invite button click ensures email field contains proper input and the
	 * permission fields are checked
	 */
	@FXML
	public void handleInvite() {
		String email = emailField.getText();
		String validationResult = TextValidation.validateEmail(email);

		if (!validationResult.isEmpty()) {
			showAlert("Invalid Email", validationResult, "");
			return;
		}

		db = Database.getInstance();
		if (db.getEmail(email).isEmpty()) { // Check if email exists in the database

			if (!admin.isSelected() && !student.isSelected() && !instructor.isSelected()) {
				showAlert("No permissions selected!", "Please select at least one permission.", "");
				return;
			}

			// Generate a random invite token
			String inviteToken = generateInviteToken();
			boolean isAdmin = admin.isSelected();
			boolean isStudent = student.isSelected();
			boolean isInstructor = instructor.isSelected();

			User newUser = new User(); // Create new User instance
			setPermissions(newUser, inviteToken);

			try {
				String generatedInviteCode = db.inviteUser(inviteToken, email, isAdmin, isStudent, isInstructor);
				showAlert("User Invited!", "User invited with invite code: " + generatedInviteCode,
						"Invite sent to: " + email);
				System.out.println("[AdminHomePage] User invited with invite code: " + generatedInviteCode);
				loadUsers(); // Refresh the table with updated users

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Database Error", "An error occurred while accessing the database.", "");
			}
		} else {
			showAlert("User Already Exists!", "User already exists with the email", email);
		}
	}

	/**
	 * Go to Article View
	 */
	@FXML
	public void article() {
		System.out.println("[AdminHomePage] Navigating to Article View.");
		//navigateTo("/views/HelpArticleView.fxml");
		navigateTo("/views/SearchArticleView.fxml");
	}
	
	/**
	 * Set permissions for a new user
	 * 
	 * @param user type User
	 * @param inviteToken type String
	 */
	public void setPermissions(User user, String inviteToken) {
		user.setAdmin(admin.isSelected());
		user.setStudent(student.isSelected());
		user.setInstructor(instructor.isSelected());
		user.setInviteToken(inviteToken);
	}

	/**
	 * Initialize table columns and their associated action buttons
	 */
	private void initializeTable() {
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		preferrednameColumn.setCellValueFactory(new PropertyValueFactory<>("preferredName"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
		studentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
		instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));

		// Add buttons for actions like updating permissions or resetting password
		updatePermissionsColumn.setCellFactory(param -> new TableCell<>() {
			private final CheckBox updateAdmin = new CheckBox("Admin");
			private final CheckBox updateStudent = new CheckBox("Student");
			private final CheckBox updateInstructor = new CheckBox("Instructor");
			private final Button updateButton = new Button("Update Permissions");

			{
				// Action for updating permissions
				updateButton.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					Boolean isAdmin = updateAdmin.isSelected();
					Boolean isStudent = updateStudent.isSelected();
					Boolean isInstructor = updateInstructor.isSelected();
					// Pass the local checkboxes to the updatePermissions method
					updatePermissions(user, isAdmin, isStudent, isInstructor);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					HBox buttons = new HBox(5, updateAdmin, updateStudent, updateInstructor, updateButton);
					setGraphic(buttons);
				}
			}
		});

		resetPasswordColumn.setCellFactory(param -> new TableCell<>() {
			private final Button resetButton = new Button("Reset Password");

			{
				// Action for resetting password
				resetButton.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					resetPassword(user);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(resetButton);
				}
			}
		});

		deleteColumn.setCellFactory(param -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					deleteUser(user);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(deleteButton);
				}
			}
		});
	}

	/**
	 * Helper function for the admin to reset a user password that generates a OTP
	 * 
	 * @param user
	 */
	private void resetPassword(User user) {

		// Show a confirmation dialog before applying changes
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Reset Password");
		confirmationAlert.setHeaderText("Reset Password and Send One-Time Password For " + user.getEmail());
		confirmationAlert.setContentText("Are you sure you want to reset the password for this user?");

		// If admin confirms the change
		confirmationAlert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					// Create an instance of OTP
					OTP otp = new OTP();
					// Generate and save the OTP
					otp.generateAndSaveOTP(user.getEmail()); // Assuming you want to use email for OTP

					// Show a success message
					showAlert("Success", "One-Time Password sent to " + user.getEmail(), "");
				} catch (SQLException e) {
					e.printStackTrace();
					showAlert("Error", "Failed to generate OTP for " + user.getEmail(), "");
				}
			}
		});
	}

	/**
	 * Load users into the TableView TableView Exists in the UI
	 */
	public void loadUsers() {
		System.out.println("[AdminHomePage] Loading users...");
		db = Database.getInstance();
		List<User> users = new ArrayList<>();
		try {
			List<Map<String, Object>> rawUsers = db.getAllUsers(); // Fetch user data
			for (Map<String, Object> row : rawUsers) {
				User user = new User();
				// Use the keys to retrieve values from the map
				user.setId((Integer) row.get("id"));
				user.setUsername((String) row.get("username"));
				user.setPreferredName((String) row.get("preferredName"));
				user.setEmail((String) row.get("email"));
				user.setAdmin((Boolean) row.get("isAdmin"));
				user.setStudent((Boolean) row.get("isStudent"));
				user.setInstructor((Boolean) row.get("isInstructor"));
				user.setInviteToken((String) row.get("inviteToken")); // Set invite token if applicable

				// Add the User object to the list
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Populate TableView with the list of users
		userTable.getItems().setAll(users);
	}

	/**
	 * Generates a random invite token
	 * 
	 * @return
	 */
	private String generateInviteToken() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder token = new StringBuilder(5);
		for (int i = 0; i < 5; i++) {
			token.append(characters.charAt(random.nextInt(characters.length())));
		}
		System.out.println("[AdminHomePage] Invite token generated: " + token.toString());
		return token.toString();
	}

	/**
	 * List to store alert messages
	 */
	private List<String> alertMessages = new ArrayList<>();

	/**
	 * showAlert()
	 * 
	 * @param title
	 * @param message
	 * @param message2
	 */
	private void showAlert(String title, String message, String message2) {
		alertMessages.add(message);
		alertMessages.add(message2);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message + "\n" + message2);
		alert.showAndWait();
	}

	/**
	 * Method to handle updating a user's permissions
	 * 
	 * @param user
	 * @param isAdmin
	 * @param isStudent
	 * @param isInstructor
	 */
	private void updatePermissions(User user, Boolean isAdmin, Boolean isStudent, Boolean isInstructor) {
		// Check if we're removing admin from the last admin
	    if (!isAdmin && user.isAdmin() && isLastAdminUser(user.getEmail())) {
	        showAlert("Error", 
	                 "Cannot remove admin role", 
	                 "There must be at least one user with admin privileges in the system.");
	        return;
	    }
	    
		// Show a confirmation dialog before applying changes
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Update Permissions");
		confirmationAlert.setHeaderText("Modify permissions for " + user.getEmail());
		confirmationAlert.setContentText("Are you sure you want to update the permissions for this user?");

		// If admin confirms the change
		confirmationAlert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Get the updated permissions from checkboxes
				user.setAdmin(isAdmin);
				user.setStudent(isStudent);
				user.setInstructor(isInstructor);

				try {
					// Update the user permissions in the database
					db = Database.getInstance();
					db.updateUserPermissions(user.getEmail(), isAdmin, isStudent, isInstructor);

					// Show success message
					showAlert("Success", "Permissions updated for " + user.getEmail(), "");
					System.out.println("[AdminHomePage] New User Permissions For: " + user.getEmail() + " Permissions: " + isAdmin
							+ isStudent + isInstructor);
					loadUsers(); // Refresh the user list to show updated permissions

				} catch (SQLException e) {
					e.printStackTrace();
					showAlert("Error", "Failed to update permissions for " + user.getEmail(), "");
				}
			}
		});
	}

	/**
	 * deletes a user interacts with the database
	 * 
	 * @param user
	 */
	private void deleteUser(User user) {
		// Check if we're deleting the last admin
	    if (user.isAdmin() && isLastAdminUser(user.getEmail())) {
	        showAlert("Error", 
	                 "Cannot delete user", 
	                 "Cannot delete the last user with admin privileges from the system.");
	        return;
	    }
		
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Delete User");
		confirmationAlert.setHeaderText("Delete User: " + user.getUsername());
		confirmationAlert.setContentText("Are you sure you want to delete this user?");

		confirmationAlert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					db.deleteUser(user.getEmail());
					showAlert("Success", "User deleted successfully", "");
					System.out.println("[AdminHomePage] User deleted successfully");
					loadUsers(); // Refresh the user list
				} catch (SQLException e) {
					e.printStackTrace();
					showAlert("Error", "Failed to delete user: " + user.getEmail(), "");
				}
			}
		});
	}

	@Override
	public void initialize(Stage stage, Database db) {
		super.initialize(stage, db);
		initializeTable(); // Table is set up when the controller is initialized
		loadUsers(); // Load users into the TableView
	}
	
	/**
	 * Navigates to the backup and restore view
	 */
	@FXML
	private void navigateToBackupRestore() {
		System.out.println("[AdminHomePage] Navigating to Backup Restore");
	    navigateTo("/views/BackupRestoreView.fxml");
	}
	
	
	@FXML
	private void navigateToSpecialGroupSelection() {
		System.out.println("[AdminHomePage] Navigating to Special Group Selection");
	    navigateTo("/views/SelectSpecialGroupView.fxml");
	}
	
	
	@FXML
	private void navigateToMessageSystem() {
		System.out.println("[AdminHomePage] Navigating to Message System");
	    navigateTo("/views/MessagingSystemView.fxml");
	}
	
	
	private boolean isLastAdminUser(String email) {
	    try {
	        // Get all users
	        List<Map<String, Object>> allUsers = db.getAllUsers();
	        
	        // Count how many admin users we have
	        int adminCount = 0;
	        boolean isUserAdmin = false;
	        
	        // Count total admins and check if this user is an admin
	        for (Map<String, Object> user : allUsers) {
	            if ((Boolean) user.get("isAdmin")) {
	                adminCount++;
	                if (user.get("email").equals(email)) {
	                    isUserAdmin = true;
	                }
	            }
	        }
	        
	        // Return true only if this user is an admin AND there is exactly one admin total
	        // This means they are the last/only admin
	        return isUserAdmin && adminCount == 1;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}