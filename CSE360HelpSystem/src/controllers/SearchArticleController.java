package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private ComboBox<String> groupFilterComboBox;
        
    @FXML
    private ListView<String> groupFilterListView;

    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();

            // Set up the table columns with corresponding properties from the Article model
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));  // Use correct property
            authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            
            // Load groups into the ComboBox
            List<String> groups = had.fetchGroupingIdentifiers();
            groupFilterComboBox.setItems(FXCollections.observableArrayList(groups));

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
            String searchQuery = searchField.getText().toLowerCase();
            List<String> selectedGroups = new ArrayList<>(groupFilterListView.getItems());
            
            System.out.println("Filtering with search query: '" + searchQuery + "'");
            System.out.println("Selected groups for filtering: " + selectedGroups);
            
            List<Article> articles = had.searchArticles(searchQuery);  
            
            // Additional group filtering if groups are selected
            if (!selectedGroups.isEmpty()) {
                articles = articles.stream()
                    .filter(article -> {
                        List<String> articleGroups = article.getGroupingIdentifiers();
                        return articleGroups.stream()
                            .anyMatch(selectedGroups::contains);
                    })
                    .collect(Collectors.toList());
            }

            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to filter articles: " + e.getMessage());
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
    
    @FXML
    public void addGroupToFilter() {
        String selectedGroup = groupFilterComboBox.getValue();
        if (selectedGroup != null && !selectedGroup.isEmpty() 
                && !groupFilterListView.getItems().contains(selectedGroup)) {
            groupFilterListView.getItems().add(selectedGroup);
            groupFilterComboBox.setValue(null);
            loadArticles(); // Reapply filters
        }
    }

    @FXML
    public void clearGroupFilters() {
        groupFilterListView.getItems().clear();
        loadArticles(); // Reapply any keyword filters
    }

}

