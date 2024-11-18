package controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import database.Database;
import database.HelpArticleDatabase;
import database.SpecialGroupsDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Article;
import models.SpecialGroup;
import models.UserSession;

public class SpecialGroupBackupRestoreController extends PageController {
    @FXML private TextField backupLocationField;
    @FXML private TextField restoreLocationField;
    
    private HelpArticleDatabase had;
    private SpecialGroupsDatabase sgd;
    private int currentGroupId;
    private File backupFile;
    private File restoreFile;
    
    public SpecialGroupBackupRestoreController() {
        super();
    }
    
    public SpecialGroupBackupRestoreController(Stage primaryStage, Database db, int groupId) {
        super(primaryStage, db);
        this.currentGroupId = groupId;
    }
    
    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();
            sgd = new SpecialGroupsDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize backup/restore controller: " + e.getMessage());
        }
    }
    
    @FXML
    void chooseBackupLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Special Group Backup File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Backup Files", "*.bak")
        );
        
        backupFile = fileChooser.showSaveDialog(stage);
        if (backupFile != null) {
            backupLocationField.setText(backupFile.getAbsolutePath());
        }
    }
    
    @FXML
    void chooseRestoreFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Special Group Backup File to Restore");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Backup Files", "*.bak")
        );
        
        restoreFile = fileChooser.showOpenDialog(stage);
        if (restoreFile != null) {
            restoreLocationField.setText(restoreFile.getAbsolutePath());
        }
    }
    
    @FXML
    void createBackup() {
        if (backupFile == null) {
            showErrorAlert("Error", "Please select a backup location first.");
            return;
        }

        try {
            // Get current group from UserSession
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            if (currentGroup == null) {
                showErrorAlert("Error", "No special group selected.");
                return;
            }
            
            had.backupSpecialGroupArticles(backupFile.getAbsolutePath(), currentGroup.getGroupId());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Special group backup created successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Backup Error", "Failed to create backup: " + e.getMessage());
        }
    }
    
    @FXML
    void restoreArticles() {
        if (restoreFile == null) {
            showErrorAlert("Error", "Please select a backup file to restore from.");
            return;
        }

        try {
            // Get current group from UserSession
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            if (currentGroup == null) {
                showErrorAlert("Error", "No special group selected.");
                return;
            }
            
            // Use the special group restore method
            had.restoreSpecialGroupArticles(restoreFile.getAbsolutePath(), currentGroup.getGroupId());
                
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Special group articles restored successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Restore Error", "Failed to restore articles: " + e.getMessage());
        }
    }
    
    @FXML
    private void goBack() {
        navigateTo("/views/SpecialGroupView.fxml");
    }
}