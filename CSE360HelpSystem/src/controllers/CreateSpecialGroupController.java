package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

import database.Database;
import database.SpecialGroupsDatabase;
import models.UserSession;

public class CreateSpecialGroupController extends PageController {
    @FXML
    private TextField groupNameField;
    
    private SpecialGroupsDatabase specialGroupsDB;

    public CreateSpecialGroupController() {
        super();
    }

    public CreateSpecialGroupController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    @FXML
    public void initialize() {
        try {
            specialGroupsDB = new SpecialGroupsDatabase();
        } catch (Exception e) {
            showErrorAlert("Initialization Error", "Failed to initialize special groups page");
            goHome();
        }
    }

    @FXML
    public void handleCreateGroup() {
        String groupName = groupNameField.getText().trim();
        if (groupName.isEmpty()) {
            showErrorAlert("Invalid Input", "Group name cannot be empty");
            return;
        }
        
        try {
            // Print before creation
            System.out.println("\n=== Creating New Special Group ===");
            System.out.println("Group Name: " + groupName);
            System.out.println("Current User ID: " + UserSession.getInstance().getCurrentUser().getId());
            
            int groupId = specialGroupsDB.createSpecialGroup(groupName);
            System.out.println("Created Group ID: " + groupId);
            
            String userId = String.valueOf(UserSession.getInstance().getCurrentUser().getId());
            specialGroupsDB.addUserToGroup(groupId, userId, 3);
            System.out.println("Added user " + userId + " to group with access level 3");
            System.out.println("=====================================\n");
            
            goHome();
        } catch (SQLException e) {
            if (e.getMessage().contains("CONSTRAINT_INDEX")) {
                showErrorAlert("Creation Error", "A group with this name already exists");
            } else {
                showErrorAlert("Creation Error", "Failed to create group: " + e.getMessage());
            }
        } catch (Exception e) {
            showErrorAlert("Creation Error", "Failed to create group: " + e.getMessage());
        }
    }

    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);
        initialize();
    }
}