package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import database.HelpArticleDatabase;
import models.Article;
import models.UserSession;

public class SearchArticleController extends PageController {
    private HelpArticleDatabase had;

    // Constructor with default behavior
    public SearchArticleController() {
        super();
    }

    // Constructor with Stage and Database
    public SearchArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    // Search field for article queries
    @FXML
    private TextField searchField;

    // TableView and columns for displaying articles
    @FXML
    private TableView<Article> articleTable;

    @FXML
    private TableColumn<Article, Integer> idColumn;

    @FXML
    private TableColumn<Article, String> titleColumn;

    @FXML
    private TableColumn<Article, String> abstractColumn;

    @FXML
    private TableColumn<Article, String> authorsColumn;
    
    @FXML
    private Button returnHome;
    
    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();

            // Set up the table columns with corresponding properties from the Article model
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));  // Use correct property
            authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));

            // Initially load all articles into the table
            loadAllArticles();  // Load all articles at first
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to initialize the SearchArticleController: " + e.getMessage());
        }
    }

    /**
     * Method to load all articles when the page is initialized.
     */
    public void loadAllArticles() {
        try {
            List<Article> articles = had.getAllDecryptedArticles();  // Fetch all articles from the database
            ObservableList<Article> articleList = FXCollections.observableArrayList(articles);
            articleTable.setItems(articleList);  // Set data in TableView
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to load articles: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to search and filter articles based on the search field input.
     */
    @FXML
    public void loadArticles() {
        try {
            String searchQuery = searchField.getText().toLowerCase();  // Get search input
            List<Article> articles = had.searchArticles(searchQuery);  // Fetch filtered articles from the database

            ObservableList<Article> articleList = FXCollections.observableArrayList(articles);
            articleTable.setItems(articleList);  // Set filtered data in TableView

        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to search articles: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method to delete the selected article.
     */
    @FXML
    public void deleteSelectedArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        
        if (selectedArticle != null) {
            // Confirm deletion with the user
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Article");
            confirmationAlert.setHeaderText("Are you sure you want to delete this article?");
            confirmationAlert.setContentText("This action cannot be undone.");
            
            // If user confirms, proceed with deletion
            if (confirmationAlert.showAndWait().get() == ButtonType.OK) {
                try {
                    // Delete the article from the database
                    had.deleteArticle(selectedArticle.getId());
                    showInfoAlert("Success", "Article deleted successfully.");

                    // Reload articles to reflect changes
                    loadAllArticles();

                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to delete the article: " + e.getMessage());
                }
            }
        } else {
            showErrorAlert("No selection", "Please select an article to delete.");
        }
    }

 

    /**
     * Navigate to the create article page.
     */
    public void goToCreateArticle() {
    	Article selectedArticle = null;
    	UserSession.getInstance().setSelectedArticle(selectedArticle); 
        navigateTo("/views/CreateEditArticleView.fxml");
    }

    /**
     * Navigate to view the selected article.
     */
    public void goToViewArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            // Pass the selected article to the view article page use UserSession to hold the article
            UserSession.getInstance().setSelectedArticle(selectedArticle);  
            navigateTo("/views/ViewArticle.fxml");
        } else {
            showErrorAlert("No selection", "Please select an article to view.");
        }
    }

    /**
     * Navigate to edit the selected article.
     */
    public void goToEditArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            // Pass the selected article to the edit article page  use UserSession to hold the article
            UserSession.getInstance().setSelectedArticle(selectedArticle);  
            navigateTo("/views/CreateEditArticleView.fxml");
        } else {
            showErrorAlert("No selection", "Please select an article to edit.");
        }
    }
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

