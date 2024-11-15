package controllers;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.TextValidation;
import models.User;

import java.sql.SQLException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageStudentsController extends PageController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Button sendInviteButton;
    
    @FXML
    private TableView<User> studentTable;
    
    @FXML
    private TableColumn<User, String> usernameColumn;
    
    @FXML
    private TableColumn<User, String> preferredNameColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, Void> deleteColumn;
    
    @FXML
    private Button homeButton;

    public ManageStudentsController() {
        super();
    }

    public ManageStudentsController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    @FXML
    private void initialize() {
        initializeTable();
        loadStudents();
    }

    private void initializeTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        preferredNameColumn.setCellValueFactory(new PropertyValueFactory<>("preferredName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configure delete button column
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    User student = getTableView().getItems().get(getIndex());
                    deleteStudent(student);
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

    @FXML
    public void handleInvite() {
        String email = emailField.getText();
        String validationResult = TextValidation.validateEmail(email);

        if (!validationResult.isEmpty()) {
            showAlert("Invalid Email", validationResult, "");
            return;
        }

        db = Database.getInstance();
        if (db.getEmail(email).isEmpty()) {
            // Generate a random invite token
            String inviteToken = generateInviteToken();
            
            try {
                // Automatically set as student only
                String generatedInviteCode = db.inviteUser(inviteToken, email, false, true, false);
                showAlert("Student Invited!", "Student invited with invite code: " + generatedInviteCode,
                        "Invite sent to: " + email);
                System.out.println("Student invited with invite code: " + generatedInviteCode);
                loadStudents();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while accessing the database.", "");
            }
        } else {
            showAlert("User Already Exists!", "User already exists with the email", email);
        }
    }

    private void loadStudents() {
        db = Database.getInstance();
        List<User> students = new ArrayList<>();
        try {
            List<Map<String, Object>> allUsers = db.getAllUsers();
            for (Map<String, Object> row : allUsers) {
                // Only add users who are students (i.e. also NOT admin and NOT instructor)
            	if ((Boolean) row.get("isStudent") && 
                        !(Boolean) row.get("isAdmin") && 
                        !(Boolean) row.get("isInstructor")) {
                            
                        User student = new User();
                        student.setUsername((String) row.get("username"));
                        student.setPreferredName((String) row.get("preferredName"));
                        student.setEmail((String) row.get("email"));
                        students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load students", "");
        }

        studentTable.getItems().setAll(students);
    }

    private void deleteStudent(User student) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Student");
        confirmationAlert.setHeaderText("Delete Student: " + student.getUsername());
        confirmationAlert.setContentText("Are you sure you want to delete this student?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    db.deleteUser(student.getEmail());
                    showAlert("Success", "Student deleted successfully", "");
                    loadStudents();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to delete student: " + student.getEmail(), "");
                }
            }
        });
    }

    private String generateInviteToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(characters.charAt(random.nextInt(characters.length())));
        }
        return token.toString();
    }

    private void showAlert(String title, String message, String message2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + message2);
        alert.showAndWait();
    }

    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);
        initializeTable();
        loadStudents();
    }
}
