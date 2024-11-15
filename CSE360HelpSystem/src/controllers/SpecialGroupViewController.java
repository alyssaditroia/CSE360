package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import database.Database;
import database.SpecialGroupsDatabase;
import database.HelpArticleDatabase;
import models.Article;
import models.SpecialGroup;
import models.UserSession;
/**
 * The {@code SpecialGroupViewController} is for managing articles and settings within a specific special group.
 * It allows users to view, create, edit, delete, filter, and search articles, as well as manage group settings such as users and backups.
 */
public class SpecialGroupViewController extends PageController {
    
	// FXML COMPONENTS
	@FXML private TableView<Article> articleTable;
    @FXML private TableColumn<Article, Integer> idColumn;
    @FXML private TableColumn<Article, String> titleColumn;
    @FXML private TableColumn<Article, String> abstractColumn;
    @FXML private TableColumn<Article, String> authorsColumn;
    @FXML private TextField searchField;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label groupNameLabel;
    @FXML private Button manageUsersButton;
    @FXML private Button deleteGroupButton;
    @FXML private TextField idSearchField; 
    @FXML private ComboBox<String> levelFilterComboBox; 
    @FXML private ListView<String> levelFilterListView; 
    @FXML private Label levelStatsLabel; 
    @FXML private Button backupRestoreButton;

    // Instances
    private SpecialGroupsDatabase specialGroupsDB;
    private HelpArticleDatabase helpArticleDB;

    // Constructors 
    public SpecialGroupViewController() {
        super();
    }
    
    // Constructor with stage and database
    public SpecialGroupViewController(Stage stage, Database db) {
        super(stage, db);
    }

    // Initialization 
    @FXML
    public void initialize() {
        try {
            // Only initialize databases if they haven't been created yet
            if (specialGroupsDB == null) {
                specialGroupsDB = new SpecialGroupsDatabase();
            }
            if (helpArticleDB == null) {
                helpArticleDB = new HelpArticleDatabase();
            }
            
            // Get the selected group
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            if (currentGroup != null) {
                // Clear and refresh group's articles from database
                List<String> freshArticles = specialGroupsDB.getGroupArticles(currentGroup.getGroupId());
                currentGroup.getGroupArticles().clear();
                for (String articleId : freshArticles) {
                    currentGroup.addArticle(articleId);
                }
                
                if (groupNameLabel != null) {
                    groupNameLabel.setText(currentGroup.getName());
                }
                
                // Initialize level filter
                List<String> levels = Arrays.asList("beginner", "intermediate", "advanced", "expert");
                levelFilterComboBox.setItems(FXCollections.observableArrayList(levels));
                
                // Load initial articles
                loadArticles();
            }
            
            if (articleTable != null) {
                setupTableColumns();
                setupAccessControls();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize special group view");
            goHome();
        }
    }
    // Sets up the columns for the table
    private void setupTableColumns() {
        // Existing column setup
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        
        // Add action buttons column
        TableColumn<Article, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                // Set up button actions
                viewBtn.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    viewArticle(article);
                });

                editBtn.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    editArticle(article);
                });

                deleteBtn.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    deleteArticle(article);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    int accessLevel = UserSession.getInstance().getAccessLevel();

                    // Level 1: View button only
                    if (accessLevel == 1) {
                        buttons.getChildren().add(viewBtn);
                    }
                    
                    // Level 2: Delete button only
                    else if (accessLevel == 2) {
                        buttons.getChildren().add(deleteBtn);
                    }
                    
                    // Level 3: All buttons
                    else if (accessLevel == 3) {
                        buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                    }

                    setGraphic(buttons);
                }
            }
        });
        
        articleTable.getColumns().add(actionsColumn);
    }
    /**
     * Adjusts the visibility of UI components based on access level
     */
    private void setupAccessControls() {
        int accessLevel = UserSession.getInstance().getAccessLevel();
        
        // Control button visibility based on access level
        if (manageUsersButton != null) manageUsersButton.setVisible(accessLevel >= 2);
        if (createButton != null) createButton.setVisible(accessLevel >= 2);
        if (deleteGroupButton != null) deleteGroupButton.setVisible(accessLevel == 3);
        if (backupRestoreButton != null) backupRestoreButton.setVisible(accessLevel >= 2);
    }

    /**
     * Loads articles by
     * Filters articles by search query, selected levels, and group membership
     * Updates levelStatsLabel with counts of articles at each difficulty level.
     * Populates article table
     */
    @FXML
    public void loadArticles() {
        try {
            String searchQuery = searchField.getText().toLowerCase();
            List<String> selectedLevels = new ArrayList<>(levelFilterListView.getItems());
            
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            
            List<Article> articles = helpArticleDB.getAllDecryptedArticles().stream()
                .filter(article -> currentGroup.getGroupArticles().contains(String.valueOf(article.getId())))
                .filter(article -> matchesSearch(article, searchQuery))
                .filter(article -> selectedLevels.isEmpty() || 
                                 (article.getLevel() != null && selectedLevels.contains(article.getLevel())))
                .collect(Collectors.toList());
                
            // Update level statistics
            Map<String, Long> levelCounts = articles.stream()
                .collect(Collectors.groupingBy(
                    article -> article.getLevel() != null ? article.getLevel() : "unspecified",
                    Collectors.counting()
                ));
            
            levelStatsLabel.setText(
                "Beginner(" + levelCounts.getOrDefault("beginner", 0L) + 
                ") Intermediate(" + levelCounts.getOrDefault("intermediate", 0L) + 
                ") Advanced(" + levelCounts.getOrDefault("advanced", 0L) + 
                ") Expert(" + levelCounts.getOrDefault("expert", 0L) + 
                ") Unspecified(" + levelCounts.getOrDefault("unspecified", 0L) + ")"
            );
                
            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (Exception e) {
            showErrorAlert("Loading Error", "Failed to load articles: " + e.getMessage());
        }
    }
    /**
     * Checks if an article matches a search query based on its title, authors, or abstract.
     * @param article
     * @param query
     * @return
     */
    private boolean matchesSearch(Article article, String query) {
        if (query.isEmpty()) return true;
        return article.getTitle().toLowerCase().contains(query) ||
               article.getAuthors().toLowerCase().contains(query) ||
               article.getAbstractText().toLowerCase().contains(query);
    }
    /**
     * Clears all level filters and reloads articles.
     * @param article
     * @param filters
     * @return
     */
    private boolean matchesGroupFilters(Article article, List<String> filters) {
        return filters.isEmpty() || article.getGroupingIdentifiers().stream()
            .anyMatch(filters::contains);
    }
    /**
     * Navigation methods
     */
    @FXML
    public void goToCreateArticle() {
        navigateTo("/views/SpecialGroupAddEditArticleView.fxml");
    }
    
    @FXML
    private void goToUserManagement() {
        navigateTo("/views/SpecialGroupManagementView.fxml");
    }
    
    @FXML
    private void goToBackupRestore() {
        navigateTo("/views/SpecialGroupBackupRestoreView.fxml");
    }
    
    
    private void viewArticle(Article article) {
        UserSession.getInstance().setSelectedArticle(article);
        navigateTo("/views/ViewArticle.fxml");  // Use your view article page
    }

    private void editArticle(Article article) {
        UserSession.getInstance().setSelectedArticle(article);
        navigateTo("/views/SpecialGroupAddEditArticleView.fxml");
    }

    /**
     * Deletes the current special group article
     * @param article
     */
    private void deleteArticle(Article article) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Article");
        confirm.setContentText("Are you sure you want to delete this article completely? This cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Get current group
                    SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
                    
                    // Remove article from special group
                    specialGroupsDB.removeArticleFromGroup(currentGroup.getGroupId(), 
                                                         String.valueOf(article.getId()));
                    
                    // Remove from current group's article list
                    currentGroup.removeArticle(String.valueOf(article.getId()));
                    
                    // Delete the article completely - Add this line
                    helpArticleDB.deleteArticle(article.getId());
                    
                    // Refresh the table
                    loadArticles();
                    
                    showInfoAlert("Success", "Article deleted successfully");
                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to delete article: " + e.getMessage());
                }
            }
        });
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Deletes special group entirely
     */
    @FXML
    private void deleteGroup() {
        SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
        if (currentGroup == null) {
            showErrorAlert("Error", "No group selected");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Special Group");
        confirm.setContentText("Are you sure you want to delete the group '" + 
                              currentGroup.getName() + "' and all associated articles? This cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    specialGroupsDB.deleteSpecialGroup(currentGroup.getGroupId());
                    showInfoAlert("Success", "Group deleted successfully");
                    goHome();  // Return to homepage after deletion
                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to delete group: " + e.getMessage());
                }
            }
        });
    }
    /**
     * Searches for an article by its numeric ID and displays it in the table
     */
    @FXML
    public void searchById() {
        try {
            String idText = idSearchField.getText().trim();
            if (idText.isEmpty()) {
                loadArticles(); // Reset to show all articles
                return;
            }
            
            long searchId = Long.parseLong(idText);
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            
            List<Article> articles = helpArticleDB.getAllDecryptedArticles().stream()
                .filter(article -> article.getId() == searchId)
                .filter(article -> currentGroup.getGroupArticles().contains(String.valueOf(article.getId())))
                .collect(Collectors.toList());
                
            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid numeric ID");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to search by ID: " + e.getMessage());
        }
    }

    /**
     * Adds the level specified to the view
     */
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

    /**
     * Clears level(s) specified from the view
     */
    @FXML
    public void clearLevelFilters() {
        levelFilterListView.getItems().clear();
        loadArticles();
    }
}