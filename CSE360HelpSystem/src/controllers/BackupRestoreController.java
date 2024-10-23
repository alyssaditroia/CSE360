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

public class BackupRestoreController extends PageController {
    
    @FXML private TextField backupLocationField;
    @FXML private TextField restoreLocationField;
    @FXML private RadioButton mergeRadio;
    @FXML private RadioButton replaceRadio;
    @FXML private CheckBox selectGroupsForBackupCheck;
    @FXML private ListView<String> groupsForBackupList;
    @FXML private TextField groupBackupLocationField;
    
    
    private HelpArticleDatabase had;
    private File backupFile;
    private File restoreFile;
    private File groupBackupFile;

    // Default constructor for FXML loader
    public BackupRestoreController() {
        super();
    }
    
    /**
     * 
     * @param primaryStage
     * @param db
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
     * 
     * @throws SQLException
     */
    private void loadAvailableGroups() throws SQLException {
        if (had != null && groupsForBackupList != null) {
            List<String> groups = had.fetchGroupingIdentifiers();
            groupsForBackupList.setItems(FXCollections.observableArrayList(groups));
        }
    }
    
    /**
     * 
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
     * 
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
     * 
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
     * 
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
     * 
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
     * 
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