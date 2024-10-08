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
 * <p> Title: AdminHomePageController class </p>
 * 
 * <p> Description: a JavaFX controller that manages the functionality of the admin home page in this application. 
 *     This class extends the PageController base class, allowing it to inherit common page-related behavior
 *     while introducing admin-specific actions. </p>
 * 
 * @author Alyssa DiTroia
 * 
 */
public class AdminHomePageController extends PageController {

    public AdminHomePageController() {
        super();
    }

    public AdminHomePageController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    @FXML
    private Label adminHomeTitle;

    @FXML
    private CheckBox admin;

    @FXML
    private CheckBox student;

    @FXML
    private CheckBox instructor;

    @FXML
    private TextField emailField;

    @FXML
    private Button sendInviteButton;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> usernameColumn;
    
    @FXML
    private TableColumn<User, String> preferrednameColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, Boolean> adminColumn;

    @FXML
    private TableColumn<User, Boolean> studentColumn;

    @FXML
    private TableColumn<User, Boolean> instructorColumn;

    @FXML
    private TableColumn<User, Void> updatePermissionsColumn;

    @FXML
    private TableColumn<User, Void> resetPasswordColumn;
    
    @FXML
    private TableColumn<User, Void> deleteColumn;

    @FXML
    private Button logoutButton;

    // Handles invite button click
    @FXML
    public void handleInvite() {
        String email = emailField.getText();
        String validationResult = TextValidation.validateEmail(email);

        if (!validationResult.isEmpty()) {
            showAlert("Invalid Email", validationResult, "");
            return;
        }

        db = Database.getInstance();
        if (db.getEmail(email).isEmpty()) {  // Check if email exists in the database

            if (!admin.isSelected() && !student.isSelected() && !instructor.isSelected()) {
                showAlert("No permissions selected!", "Please select at least one permission.", "");
                return;
            }

            // Generate a random invite token
            String inviteToken = generateInviteToken();
            boolean isAdmin = admin.isSelected();
            boolean isStudent = student.isSelected();
            boolean isInstructor = instructor.isSelected();

            User newUser = new User();  // Create new User instance
            setPermissions(newUser, inviteToken);

            try {
                String generatedInviteCode = db.inviteUser(inviteToken, email, isAdmin, isStudent, isInstructor);
                showAlert("User Invited!", "User invited with invite code: " + generatedInviteCode, "Invite sent to: " + email);
                System.out.println("User invited with invite code: " + generatedInviteCode);
                loadUsers();  // Refresh the table with updated users

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while accessing the database.", "");
            }
        } else {
            showAlert("User Already Exists!", "User already exists with the email", email);
        }
    }

    // Logout functionality
    @FXML
    public void logout() {
        System.out.println("Admin logged out.");
        navigateTo("/views/LoginPageView.fxml");
    }

    // Set permissions for a new user
    public void setPermissions(User user, String inviteToken) {
        user.setAdmin(admin.isSelected());
        user.setStudent(student.isSelected());
        user.setInstructor(instructor.isSelected());
        user.setInviteToken(inviteToken);
    }

    // Initialize table columns and action buttons
    private void initializeTable() {
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
    
    //
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
                    otp.generateAndSaveOTP(user.getEmail());  // Assuming you want to use email for OTP

                    // Show a success message
                    showAlert("Success", "One-Time Password sent to " + user.getEmail(), "");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to generate OTP for " + user.getEmail(), "");
                }
            }
        });
    }


 // Load users into the TableView
    public void loadUsers() {
        db = Database.getInstance();
        List<User> users = new ArrayList<>();
        try {
            List<Map<String, Object>> rawUsers = db.getAllUsers(); // Fetch raw user data
            for (Map<String, Object> row : rawUsers) {
                User user = new User();
                // Use the correct keys to retrieve values from the map
                user.setUsername((String) row.get("username"));
                user.setPreferredName((String) row.get("preferredName"));
                user.setEmail((String) row.get("email"));
                user.setAdmin((Boolean) row.get("isAdmin")); // Changed to match the key in the map
                user.setStudent((Boolean) row.get("isStudent")); // Changed to match the key in the map
                user.setInstructor((Boolean) row.get("isInstructor")); // Changed to match the key in the map
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

    // Generate random invite token
    private String generateInviteToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(characters.charAt(random.nextInt(characters.length())));
        }
        return token.toString();
    }

    // Show alert messages
    private List<String> alertMessages = new ArrayList<>();

    //
    private void showAlert(String title, String message, String message2) {
        alertMessages.add(message);
        alertMessages.add(message2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + message2);
        alert.showAndWait();
    }

    // Method to handle updating a user's permissions
    private void updatePermissions(User user, Boolean isAdmin, Boolean isStudent, Boolean isInstructor) {

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
                    System.out.println("New User Permissions For: " + user.getEmail() + " Permissions: " + isAdmin + isStudent + isInstructor);
                    loadUsers();  // Refresh the user list to show updated permissions

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to update permissions for " + user.getEmail(), "");
                }
            }
        });
    }
    
    //
    private void deleteUser(User user) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete User");
        confirmationAlert.setHeaderText("Delete User: " + user.getUsername());
        confirmationAlert.setContentText("Are you sure you want to delete this user?");
        
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    db.deleteUser(user.getEmail());
                    showAlert("Success", "User deleted successfully", "");
                    loadUsers();  // Refresh the user list
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
        initializeTable();  // Ensure table is set up when the controller is initialized
        loadUsers();  // Load users into the TableView
    }
}

