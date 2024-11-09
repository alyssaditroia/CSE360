package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.SpecialGroup;
import models.User;
import models.UserSession;

import java.sql.PreparedStatement;

import database.Database;
import database.SpecialGroupsDatabase;

public class SpecialGroupManagementController extends PageController {
	 // Table views
    @FXML private TableView<User> adminUsersTable;
    @FXML private TableView<User> manageUsersTable;
    @FXML private TableView<User> viewUsersTable;
    @FXML private TableView<User> availableUsersTable;
    
    // Admin table columns
    @FXML private TableColumn<User, Integer> adminIdCol;
    @FXML private TableColumn<User, String> adminNameCol;
    @FXML private TableColumn<User, String> adminEmailCol;
    @FXML private TableColumn<User, Void> adminActionsCol;
    
    // Manage table columns
    @FXML private TableColumn<User, Integer> manageIdCol;
    @FXML private TableColumn<User, String> manageNameCol;
    @FXML private TableColumn<User, String> manageEmailCol;
    @FXML private TableColumn<User, Void> manageActionsCol;
    
    // View table columns
    @FXML private TableColumn<User, Integer> viewIdCol;
    @FXML private TableColumn<User, String> viewNameCol;
    @FXML private TableColumn<User, String> viewEmailCol;
    @FXML private TableColumn<User, Void> viewActionsCol;
    
    // Available table columns
    @FXML private TableColumn<User, Integer> availableIdCol;
    @FXML private TableColumn<User, String> availableNameCol;
    @FXML private TableColumn<User, String> availableEmailCol;
    @FXML private TableColumn<User, Void> availableActionsCol;
    
    private int groupId; // ID of the special group being managed
    private SpecialGroupsDatabase specialGroupsDb;

    @FXML
    public void initialize() {
        try {
            specialGroupsDb = new SpecialGroupsDatabase();
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            if (currentGroup != null) {
                // Get group ID from the current group
                this.groupId = currentGroup.getGroupId();
                System.out.println("Initializing management for group ID: " + this.groupId);
                
                // Initialize the tables
                initializeTables();
                
                // Print debug info
                specialGroupsDb.printGroupMembers(this.groupId);
                
                // Load users with correct group ID
                loadUsers();
            } else {
                System.out.println("No group selected in UserSession");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTables() {
        // Initialize common columns for all tables
        initializeTable(adminUsersTable, 3);    // Admin access
        initializeTable(manageUsersTable, 2);   // Manage access
        initializeTable(viewUsersTable, 1);     // View access
        initializeAvailableUsersTable();        // Available users
    }

    private void initializeTable(TableView<User> table, int currentAccessLevel) {
        // Set up basic columns
        setupBasicColumns(table);
        
        // Set up action column
        TableColumn<User, Void> actionsCol = (TableColumn<User, Void>) table.getColumns().get(3);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button removeBtn = new Button("Remove");
            private final ComboBox<String> accessLevel = new ComboBox<>();
            
            {
                // Setup remove button
                removeBtn.setOnAction(e -> {
                    User user = getTableRow().getItem();
                    removeUserFromGroup(user);
                });
                
                // Setup access level combo box
                accessLevel.getItems().addAll("View", "Manage", "Admin");
                accessLevel.setOnAction(e -> {
                    User user = getTableRow().getItem();
                    updateUserAccess(user, accessLevel.getSelectionModel().getSelectedIndex() + 1);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(5, accessLevel, removeBtn);
                    setGraphic(actions);
                }
            }
        });
    }

    private void initializeAvailableUsersTable() {
        // Set up basic columns
        setupBasicColumns(availableUsersTable);
        
        // Set up action column with add buttons
        TableColumn<User, Void> actionsCol = (TableColumn<User, Void>) availableUsersTable.getColumns().get(3);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button addViewBtn = new Button("Add View");
            private final Button addManageBtn = new Button("Add Manage");
            private final Button addAdminBtn = new Button("Add Admin");
            
            {
                addViewBtn.setOnAction(e -> addUserToGroup(getTableRow().getItem(), 1));
                addManageBtn.setOnAction(e -> addUserToGroup(getTableRow().getItem(), 2));
                addAdminBtn.setOnAction(e -> addUserToGroup(getTableRow().getItem(), 3));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(5, addViewBtn, addManageBtn, addAdminBtn);
                    setGraphic(actions);
                }
            }
        });
    }

    private void setupBasicColumns(TableView<User> table) {
        ((TableColumn<User, String>) table.getColumns().get(0)).setCellValueFactory(
                new PropertyValueFactory<>("id"));
        ((TableColumn<User, String>) table.getColumns().get(1)).setCellValueFactory(
                new PropertyValueFactory<>("username"));
        ((TableColumn<User, String>) table.getColumns().get(2)).setCellValueFactory(
                new PropertyValueFactory<>("email"));
    }

    private void loadUsers() {
        try {
            System.out.println("Loading users for group ID: " + this.groupId);
            
            // Load each access level into appropriate table
            adminUsersTable.getItems().setAll(specialGroupsDb.getGroupUsersByAccessLevel(this.groupId, 3));
            manageUsersTable.getItems().setAll(specialGroupsDb.getGroupUsersByAccessLevel(this.groupId, 2));
            viewUsersTable.getItems().setAll(specialGroupsDb.getGroupUsersByAccessLevel(this.groupId, 1));
            availableUsersTable.getItems().setAll(specialGroupsDb.getAvailableUsers(this.groupId));
            
            System.out.println("Users loaded into tables");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void addUserToGroup(User user, int accessLevel) {
        try {
            specialGroupsDb.addUserToGroup(groupId, String.valueOf(user.getId()), accessLevel);
            loadUsers(); // Refresh all tables
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeUserFromGroup(User user) {
        try {
            specialGroupsDb.removeUserFromGroup(groupId, String.valueOf(user.getId()));
            loadUsers(); // Refresh all tables
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserAccess(User user, int newAccessLevel) {
        try {
            String sql = "UPDATE special_group_members SET access_level = ? " +
                        "WHERE group_id = ? AND user_id = ?";
            
            try (PreparedStatement pstmt = specialGroupsDb.getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, newAccessLevel);
                pstmt.setInt(2, groupId);
                pstmt.setString(3, String.valueOf(user.getId()));
                pstmt.executeUpdate();
            }
            loadUsers(); // Refresh all tables
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to set the group ID when navigating to this page
    public void setGroupId(int groupId) {
        this.groupId = groupId;
        loadUsers(); // Load users for this group
    }
    
    @FXML
    public void goBack() {
    	navigateTo("/views/SpecialGroupView.fxml");
    }
}