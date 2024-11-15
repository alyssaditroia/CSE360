package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import database.Database;
import database.HelpArticleDatabase;
import models.Article;
import models.UserSession;
/**
 * <p>
 * Title: SearchArticleController
 * </p>
 * 
 * <p>
 * Description: Manages the page that allows the user to search the system for specific help articles
 * </p>
 */
public class StudentGeneralViewController extends PageController {
	/**
     * Help article database instance
     */
    private HelpArticleDatabase had;

    /**
     * Default constructor required for FXML loader initialization.
     */
    public StudentGeneralViewController() {
        super();
    }


	/**
	 * Constructs a SearchArticleController with the specified stage and database.
	 *
	 * @param primaryStage The main application window
	 * @param db The database instance to be used
	 */
    public StudentGeneralViewController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }
    
    /**
     * FXML injected UI elements for the view of the page
     */
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
    private ComboBox<String> levelFilterComboBox;
    
    @FXML 
    private ListView<String> levelFilterListView;
    
    @FXML
    private Label activeGroupsLabel;
    
    @FXML
    private Label levelStatsLabel;
    
    @FXML
    private ListView<String> articleSummaryList;
    
    @FXML
    private TextField idSearchField;


	/**
	 * Initializes the controller and its UI components.
	 * Sets up table columns, loads groups into the ComboBox, and loads initial articles.
	 */
    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();

            // Set up the table columns with corresponding properties from the Article model
            idColumn.setCellValueFactory(column -> 
	            new javafx.beans.property.ReadOnlyObjectWrapper<>(
	                articleTable.getItems().indexOf(column.getValue()) + 1
	            )
	        );
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));  // Use correct property
            authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            
            // Load groups into the ComboBox
            List<String> groups = had.fetchGroupingIdentifiers();
            groupFilterComboBox.setItems(FXCollections.observableArrayList(groups));
            
            // Initialize level filter with predefined levels
            List<String> levels = Arrays.asList("beginner", "intermediate", "advanced", "expert");
            levelFilterComboBox.setItems(FXCollections.observableArrayList(levels));

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
            List<Article> articles = had.getAllGeneralArticles();  // Changed from getAllDecryptedArticles() to getAllGeneralArticles()
            ObservableList<Article> articleList = FXCollections.observableArrayList(articles);
            articleTable.setItems(articleList);
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
        	// Get the student's search field and if isn't empty add their search to the user session 
        	String originalSearchText = searchField.getText().trim();
        	if (!originalSearchText.isEmpty()) {
        	    UserSession.getInstance().addUserSearch(originalSearchText);
        	    System.out.println("[INFO in UserSession] Added search: " + originalSearchText);
        	}
        	
        	// Get search and filters selected
            String searchQuery = searchField.getText().toLowerCase();
            List<String> selectedGroups = new ArrayList<>(groupFilterListView.getItems());
            List<String> selectedLevels = new ArrayList<>(levelFilterListView.getItems());
            
            // Get general articles and filter them
            List<Article> articles = had.getAllGeneralArticles().stream()
                .filter(article -> 
                    searchQuery.isEmpty() || // If no search query, include all
                    article.getTitle().toLowerCase().contains(searchQuery) ||
                    article.getAbstractText().toLowerCase().contains(searchQuery) ||
                    article.getAuthors().toLowerCase().contains(searchQuery))
                .filter(article -> 
                    selectedGroups.isEmpty() || // If no groups selected, include all
                    article.getGroupingIdentifiers().stream().anyMatch(selectedGroups::contains))
                .filter(article -> 
                    selectedLevels.isEmpty() || // If no levels selected, include all
                    (article.getLevel() != null && selectedLevels.contains(article.getLevel())))
                .collect(Collectors.toList());
            
            // Count articles per level, handling null levels
            Map<String, Long> levelCounts = articles.stream()
                .collect(Collectors.groupingBy(
                    article -> article.getLevel() != null ? article.getLevel() : "unspecified",
                    Collectors.counting()
                ));
            
            // Update the label with safe default values of 0
            levelStatsLabel.setText(
                "Beginner(" + levelCounts.getOrDefault("beginner", 0L) + 
                ") Intermediate(" + levelCounts.getOrDefault("intermediate", 0L) + 
                ") Advanced(" + levelCounts.getOrDefault("advanced", 0L) + 
                ") Expert(" + levelCounts.getOrDefault("expert", 0L) + 
                ") Unspecified(" + levelCounts.getOrDefault("unspecified", 0L) + ")"
            );

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
    
    /**
     * Displays an information alert dialog with the specified title and message.
     *
     * @param title The title of the alert dialog
     * @param message The message to display in the alert dialog
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Adds the selected group from the ComboBox to the filter ListView.
     * Reapplies filters after adding the group.
     */
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

    /**
     * Clears all group filters from the ListView.
     * Reapplies any existing keyword filters.
     */
    @FXML
    public void clearGroupFilters() {
        groupFilterListView.getItems().clear();
        loadArticles(); // Reapply any keyword filters
    }
    
    @FXML
    public void addLevelToFilter() {
        String selectedLevel = levelFilterComboBox.getValue();
        if (selectedLevel != null && !selectedLevel.isEmpty() 
                && !levelFilterListView.getItems().contains(selectedLevel)) {
            levelFilterListView.getItems().add(selectedLevel);
            levelFilterComboBox.setValue(null);
            loadArticles();
        }
    }

    @FXML
    public void clearLevelFilters() {
        levelFilterListView.getItems().clear();
        loadArticles();
    }
    
    /**
     * 
     */
    @FXML
    private void returnHome() {
		navigateTo("/views/StudentHomePageView.fxml");
	}
    
    @FXML
    public void searchById() {
        try {
            String idText = idSearchField.getText().trim();
            if (idText.isEmpty()) {
                loadAllArticles();
                return;
            }
            
            long searchId = Long.parseLong(idText);
            List<Article> articles = had.getAllGeneralArticles().stream()
                .filter(article -> article.getId() == searchId)
                .collect(Collectors.toList());
                
            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid numeric ID");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to search by ID: " + e.getMessage());
        }
    }

}

