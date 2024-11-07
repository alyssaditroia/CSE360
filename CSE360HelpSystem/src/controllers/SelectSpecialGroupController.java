package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.Database;
import database.SpecialGroupsDatabase;
import models.SpecialGroup;
import models.UserSession;

/**
 * Controller for the special group selection page.
 * Allows users to select from their accessible special groups.
 */
public class SelectSpecialGroupController extends PageController {
    @FXML
    private ComboBox<String> groupDropdown;

    private SpecialGroupsDatabase specialGroupsDB;
    // Maps group names to their IDs for efficient lookup
    private Map<String, Integer> groupNameToId;
    
    /**
     * Default constructor
     */
    public SelectSpecialGroupController() {
        super();
        groupNameToId = new HashMap<>();
    }
    
    /**
     * Constructor with stage and database parameters
     * @param primaryStage The primary stage of the application
     * @param db The database instance being used
     */
    public SelectSpecialGroupController(Stage primaryStage, Database db) {
        super(primaryStage, db);
        groupNameToId = new HashMap<>();
    }

    /**
     * Initializes the controller.
     * Sets up database connection and loads available groups.
     */
    @FXML
    public void initialize() {
        try {
            specialGroupsDB = new SpecialGroupsDatabase();
            loadUserGroups();
        } catch (Exception e) {
            showError("Error loading special groups page");
            goHome();
        }
    }

    /**
     * Loads all special groups the current user has access to.
     * Populates the dropdown menu with group names and stores group IDs.
     */
    private void loadUserGroups() {
        try {
            // Get current user's groups from database
            String userId = String.valueOf(UserSession.getInstance().getCurrentUser().getId());
            List<String[]> userGroups = specialGroupsDB.getUserGroups(userId);
            
            // Store mapping of names to IDs and populate dropdown
            for (String[] group : userGroups) {
                groupNameToId.put(group[1], Integer.parseInt(group[0]));
            }
            
            groupDropdown.setItems(FXCollections.observableArrayList(groupNameToId.keySet()));
        } catch (Exception e) {
            showError("Error loading groups");
            goHome();
        }
    }

    /**
     * Handles user selection of a group.
     * Sets the user's access level for selected group and navigates to group view.
     */
    @FXML
    public void handleGroupSelection() {
        try {
            String selectedGroupName = groupDropdown.getValue();
            if (selectedGroupName != null) {
                // Get all required data first
                int groupId = groupNameToId.get(selectedGroupName);
                String userId = String.valueOf(UserSession.getInstance().getCurrentUser().getId());
                List<String[]> groupUsers = specialGroupsDB.getUserGroups(userId);
                List<String> articles = specialGroupsDB.getGroupArticles(groupId);
                
                // Convert to proper ArrayLists
                ArrayList<String> members = new ArrayList<>();
                ArrayList<String> admins = new ArrayList<>();
                ArrayList<String> groupArticles = new ArrayList<>(articles);
                
                // Create the group
                SpecialGroup selectedGroup = new SpecialGroup(groupId, selectedGroupName, 
                    members, admins, groupArticles);
                
                // Store access level and selected group
                int accessLevel = specialGroupsDB.getUserAccessLevel(userId, groupId);
                UserSession.getInstance().setAccessLevel(accessLevel);
                UserSession.getInstance().setSelectedSpecialGroup(selectedGroup);

                // Modified this part to properly handle the stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SpecialGroupView.fxml"));
                Parent newView = loader.load();
                SpecialGroupViewController controller = loader.getController();
                controller.initialize(this.stage, this.db);
                
                Scene newScene = new Scene(newView);
                this.stage.setScene(newScene);
                this.stage.show();
            }
        } catch (Exception e) {
            showError("Error selecting group: " + e.getMessage());
        }
    }


    /**
     * Initializes the controller with stage and database instances
     * @param stage The application window
     * @param db The database instance
     */
    @Override
    public void initialize(Stage stage, Database db) {
        super.initialize(stage, db);
        initialize();
    }
}