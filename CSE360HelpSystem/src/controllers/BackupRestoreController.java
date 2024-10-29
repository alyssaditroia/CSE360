package controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import database.Database;
import database.HelpArticleDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.UserSession;
/*******
 * <p>
 * Title: BackupRestoreController class
 * </p>
 * 
 * <p>
 * Description: a JavaFX controller that manages the functionality of the 
 * page where a user can backup and restore the help system. This class extends 
 * the PageController base class, allowing it to inherit common page-related behavior.
 * </p>
 * 
 * @author Justin Faris
 * 
 */
public class BackupRestoreController extends PageController {
	/**
	 * FXML injected UI elements for the Backup Restore Page
	 */
    @FXML private TextField backupLocationField;
    @FXML private TextField restoreLocationField;
    @FXML private RadioButton mergeRadio;
    @FXML private RadioButton replaceRadio;
    @FXML private CheckBox selectGroupsForBackupCheck;
    @FXML private ListView<String> groupsForBackupList;
    @FXML private TextField groupBackupLocationField;
    
    /**
     * Help article database instance
     */
    private HelpArticleDatabase had;
    
    /**
     * File used to backup the system information
     */
    private File backupFile;
    
    /**
     * Backup file used to restore the system information
     */
    private File restoreFile;
    
    /**
     * Backup file for a group of files to be backed up to
     */
    private File groupBackupFile;

    /**
     * Default constructor for FXML loader
     */
    public BackupRestoreController() {
        super();
    }
    
    /**
     * Initializes the BackupRestoreController with the specified primary stage and database.
     * 
     * @param primaryStage The main stage of the application
     * @param db The database instance to be used
     */
    public BackupRestoreController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }
    
    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();
            
            // Initialize groups list
            loadAvailableGroups();
            
            // Set up groups list visibility binding
            if (selectGroupsForBackupCheck != null && groupsForBackupList != null) {
                groupsForBackupList.managedProperty().bind(selectGroupsForBackupCheck.selectedProperty());
                groupsForBackupList.visibleProperty().bind(selectGroupsForBackupCheck.selectedProperty());
            }
            
            // Enable multiple selection for groups
            if (groupsForBackupList != null) {
                groupsForBackupList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
            
            // Set up toggle group for radio buttons
            if (mergeRadio != null && replaceRadio != null) {
                ToggleGroup toggleGroup = new ToggleGroup();
                mergeRadio.setToggleGroup(toggleGroup);
                replaceRadio.setToggleGroup(toggleGroup);
                mergeRadio.setSelected(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize backup/restore controller: " + e.getMessage());
        }
    }
    
    /**
     * Loads all available groups from the database into the groups list view.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    private void loadAvailableGroups() throws SQLException {
        if (had != null && groupsForBackupList != null) {
            List<String> groups = had.fetchGroupingIdentifiers();
            groupsForBackupList.setItems(FXCollections.observableArrayList(groups));
        }
    }
    
    /**
     * Opens a file chooser dialog to select a location for saving the backup file.
     * Updates the backup location text field with the selected path.
     */
    @FXML
    void chooseBackupLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Backup File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Backup Files", "*.bak")
        );
        
        backupFile = fileChooser.showSaveDialog(stage); // Using inherited stage
        if (backupFile != null) {
            backupLocationField.setText(backupFile.getAbsolutePath());
        }
    }
    
    /**
     * Opens a file chooser dialog to select a backup file for restoration.
     * Updates the restore location text field with the selected path.
     */
    @FXML
    void chooseRestoreFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup File to Restore");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Backup Files", "*.bak")
        );
        
        restoreFile = fileChooser.showOpenDialog(stage); // Using inherited stage
        if (restoreFile != null) {
            restoreLocationField.setText(restoreFile.getAbsolutePath());
        }
    }
    
    /**
     * Creates a backup of all articles at the specified backup location.
     * Shows a success message if backup is created successfully, or an error message if it fails.
     */
    @FXML
    void createBackup() {
        if (backupFile == null) {
            showErrorAlert("Error", "Please select a backup location first.");
            return;
        }

        try {
            had.backupArticles(backupFile.getAbsolutePath());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Backup created successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Backup Error", "Failed to create backup: " + e.getMessage());
        }
    }
    
    /**
     * Restores articles from the selected backup file.
     * Uses the selected restore mode (merge or replace) and shows appropriate success/error messages.
     */
    @FXML
    void restoreArticles() {
        if (restoreFile == null) {
            showErrorAlert("Error", "Please select a backup file to restore from.");
            return;
        }

        try {
            had.restoreArticlesWithMerge(restoreFile.getAbsolutePath(), mergeRadio.isSelected());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Articles restored successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Restore Error", "Failed to restore articles: " + e.getMessage());
        }
    }
    
    /**
     * Opens a file chooser dialog to select a location for saving the group backup file.
     * Updates the group backup location text field with the selected path.
     */
    @FXML
    void chooseGroupBackupLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Group Backup File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Backup Files", "*.bak")
        );
        
        groupBackupFile = fileChooser.showSaveDialog(stage);
        if (groupBackupFile != null) {
            groupBackupLocationField.setText(groupBackupFile.getAbsolutePath());
        }
    }
    
    /**
     * Creates a backup of selected groups at the specified backup location.
     * Shows a success message if backup is created successfully, or an error message if it fails.
     * Requires at least one group to be selected before backup can be created.
     */
    @FXML
    void createGroupBackup() {
        if (groupBackupFile == null) {
            showErrorAlert("Error", "Please select a backup location first.");
            return;
        }

        List<String> selectedGroups = groupsForBackupList.getSelectionModel().getSelectedItems();
        if (selectedGroups.isEmpty()) {
            showErrorAlert("Error", "Please select at least one group to backup.");
            return;
        }

        try {
            had.backupGroupArticles(groupBackupFile.getAbsolutePath(), selectedGroups);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Group backup created successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Backup Error", "Failed to create group backup: " + e.getMessage());
        }
    }
}