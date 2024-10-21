package controllers;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import database.HelpArticleDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Article;

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
        char [] title = titleField.getText().toCharArray();
		char [] authors = authorsField.getText().toCharArray();
		char [] abstractText = abstractField.getText().toCharArray();
		char [] keywords = keywordsField.getText().toCharArray();
		char [] body = bodyField.getText().toCharArray();
		char [] references = referencesField.getText().toCharArray();

		// Create a new article instance for runtime memory
		Article newArticle = new Article(title, authors, abstractText, keywords, body, references);

		// Insert into the database
		try {
			had.createArticle(title, authors, abstractText, keywords, body, references);
		} catch (Exception e) {
		    showErrorAlert("Error", "Failed to create article: " + e.getMessage());
		}

		// Refresh the article list in the TableView
		listArticles();

		// Clear input fields
		clearFields();

		showInfoAlert("Success", "Article created successfully!");
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
            titleField.setText(selectedArticle.getTitle().toString());
            authorsField.setText(selectedArticle.getAuthors().toString());
            abstractField.setText(selectedArticle.getAbstractText().toString());
            keywordsField.setText(selectedArticle.getKeywords().toString());
            bodyField.setText(selectedArticle.getBody().toString());
            referencesField.setText(selectedArticle.getReferences().toString());
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




	

