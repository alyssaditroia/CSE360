package controllers;

import java.sql.SQLException;
import java.util.List;
import java.sql.Date;

import database.Database;
import database.HelpArticleDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Article;

/**
 * 
 * NOT CURRENTLY USING... JUST AN EXAMPLE
 * 
 * USING THE FOLLOWING INSTEAD FOR ORGANIZATION:
 * 
 * SearchArticleController
 * CreateEditArticleController
 * ViewArticleController
 * 
 * 
 * 
 */
public class HelpArticleController extends PageController {
    
    // Database handler instance
    private HelpArticleDatabase had;
    
	public HelpArticleController() {
		super();
	}

	public HelpArticleController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}

    // FXML fields corresponding to HelpArticleView.fxml file
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorsField;
    @FXML
    private TextArea abstractField;
    @FXML
    private TextArea keywordsField;
    @FXML
    private TextArea bodyField;
    @FXML
    private TextArea referencesField;
    @FXML
    private ComboBox<String> levelField;
    @FXML
    private TextField groupingSearchField;  // Search or add identifier
    @FXML
    private ListView<String> groupingListView;  // List of identifiers
    @FXML
    private Button addGroupingButton;
    @FXML
    private TextField permissionsField;
    @FXML
    private DatePicker dateAddedField;
    @FXML
    private TextField versionField;
    @FXML
    private Button createButton;
    @FXML
    private Button listButton;
    @FXML
    private Button viewButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backupButton;
    @FXML
    private Button restoreButton;
    @FXML
    private CheckBox adminCheckbox;
    @FXML
    private CheckBox instructorCheckbox;
    @FXML
    private CheckBox studentCheckbox;
    @FXML
    private TableView<Article> articleTable;
    @FXML
    private TableColumn<Article, Integer> idColumn;
    @FXML
    private TableColumn<Article, String> titleColumn;
    @FXML
    private TableColumn<Article, String> authorsColumn;

    /**
     *  Initialize method to set up TableView columns and load data
     */
    @FXML
    public void initialize() {
        try {
        	had = new HelpArticleDatabase();
            levelField.setItems(FXCollections.observableArrayList("beginner", "intermediate", "advanced", "expert"));
            groupingListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            // Set up TableView columns
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            // Load the articles into the table view
            listArticles();
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to initialize the HelpArticleController: " + e.getMessage());
        }
    }

    /**
     * Method to create a new article
     */
    @FXML
    public void createArticle() {
        char[] title = titleField.getText().toCharArray();
        char[] authors = authorsField.getText().toCharArray();
        char[] abstractText = abstractField.getText().toCharArray();
        char[] keywords = keywordsField.getText().toCharArray();
        char[] body = bodyField.getText().toCharArray();
        char[] references = referencesField.getText().toCharArray();
        String level = levelField.getValue();  // Get selected level from ComboBox

        // Get selected grouping identifiers from ListView
        List<String> selectedIdentifiers = groupingListView.getSelectionModel().getSelectedItems();
        String permissions = getSelectedPermissions();  // Get selected permissions

        java.sql.Date dateAdded = java.sql.Date.valueOf(dateAddedField.getValue());
        String version = versionField.getText();

        // Create a new article
        Article newArticle = new Article(title, authors, abstractText, keywords, body, references, level, 
                                         selectedIdentifiers, permissions, dateAdded, version);

        // Insert into the database
        try {
            had.createArticle(title, authors, abstractText, keywords, body, references, level, 
                              selectedIdentifiers, permissions, dateAdded, version);
            listArticles();  // Refresh the table
            clearFields();   // Clear the form
            showInfoAlert("Success", "Article created successfully!");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to create article: " + e.getMessage());
        }
    }

    /**
     *  Method to list articles in the TableView
     */
    @FXML
    public void listArticles() {
        try {
            List<Article> articles = had.getAllDecryptedArticles();  // Fetch articles from database
            ObservableList<Article> articleList = FXCollections.observableArrayList(articles);
            articleTable.setItems(articleList);  // Set data in TableView
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to retrieve articles: " + e.getMessage());
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     *  Method to view a selected article's details
     */
    @FXML
    public void viewArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            titleField.setText(selectedArticle.getTitle());
            authorsField.setText(selectedArticle.getAuthors());
            abstractField.setText(selectedArticle.getAbstractText());
            keywordsField.setText(selectedArticle.getKeywords());
            bodyField.setText(selectedArticle.getBody());
            referencesField.setText(selectedArticle.getReferences());
            levelField.setValue(selectedArticle.getLevel());

            // Set permissions checkboxes
            String permissions = selectedArticle.getPermissions();
            adminCheckbox.setSelected(permissions.contains("Admin"));
            instructorCheckbox.setSelected(permissions.contains("Instructor"));
            studentCheckbox.setSelected(permissions.contains("Student"));

            // Set grouping identifiers (multi-select)
            List<String> identifiers = selectedArticle.getGroupingIdentifiers();
            groupingListView.getSelectionModel().clearSelection();
            for (String id : identifiers) {
                groupingListView.getSelectionModel().select(id);
            }

            if (selectedArticle.getDateAdded() != null) {
                dateAddedField.setValue(selectedArticle.getDateAdded().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate());            
                } else {
                dateAddedField.setValue(null);
            }            versionField.setText(selectedArticle.getVersion());
        } else {
            showInfoAlert("No selection", "Please select an article to view.");
        }
    }

    /**
     *  Method to delete a selected article
     */
    @FXML
    public void deleteArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            try {
                had.deleteArticle(selectedArticle.getId());  // Delete from the database
                listArticles();  // Refresh the list to show without deleted article
                clearFields();
                showInfoAlert("Success", "Article deleted successfully!");
            } catch (SQLException e) {
                showErrorAlert("Error", "Failed to delete the article: " + e.getMessage());
            }
        } else {
            showInfoAlert("No selection", "Please select an article to delete.");
        }
    }

    /**
     * Method to back up articles from file
     */
    @FXML
    public void backupArticles() {
        try {
        	// TODO Create Text Box for the backup file name to be inputted by the user
            had.backupArticles("backup_filename.txt");  // Calls backup function in HelpArticleDatabase
            showInfoAlert("Success", "Articles backed up successfully!");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to backup articles: " + e.getMessage());
        }
    }

    /**
     * Method to restore articles from backup
     */
    @FXML
    public void restoreArticles() {
        try {
        	// TODO create text box to enter the file name for which the files will be restored from 
            had.restoreArticles("backup_filename.txt");  // Call restore function in HelpArticleDatabase
            listArticles();  // Refresh the list after restoring
            showInfoAlert("Success", "Articles restored successfully!");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to restore articles: " + e.getMessage());
        }
    }
    @FXML
    public void addGroupingIdentifier() {
        String newIdentifier = groupingSearchField.getText();
        if (!newIdentifier.isEmpty()) {
            groupingListView.getItems().add(newIdentifier);
            groupingSearchField.clear();  // Clear search box after adding
        }
    }
    public String getSelectedPermissions() {
        StringBuilder permissions = new StringBuilder();
        if (adminCheckbox.isSelected()) {
            permissions.append("Admin,");
        }
        if (instructorCheckbox.isSelected()) {
            permissions.append("Instructor,");
        }
        if (studentCheckbox.isSelected()) {
            permissions.append("Student,");
        }
        // Remove trailing comma
        if (permissions.length() > 0) {
            permissions.setLength(permissions.length() - 1);
        }
        return permissions.toString();
    }

    // Helper method to clear input fields
    private void clearFields() {
        titleField.clear();
        authorsField.clear();
        abstractField.clear();
        keywordsField.clear();
        bodyField.clear();
        referencesField.clear();
    }

    // Helper method to show information alerts
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}




	

