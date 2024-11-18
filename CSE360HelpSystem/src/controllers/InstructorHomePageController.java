package controllers;

import database.Database;
import database.HelpArticleDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Article;
import models.UserSession;

import javafx.scene.control.TextField;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Title: InstructorHomePageController
 * </p>
 * 
 * <p>
 * Description: Home page for individuals with instructor permissions
 * </p>
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 */
public class InstructorHomePageController extends PageController {
	/**
	 * Instance of the help article database
	 */
	HelpArticleDatabase db;
	
	/**
     * FXML injected UI elements for the Instructor Homepage
     */
	@FXML
    private Button logoutButton;
    
    @FXML
    private Button createArticleButton;
    
    @FXML
    private Button backupButton;
    
    @FXML
    private Button restoreButton;
    
    @FXML
    private TableView<Article> articleTable;

    @FXML
    private TableColumn<Article, String> titleColumn;

    @FXML
    private TableColumn<Article, String> levelColumn;

    @FXML
    private TableColumn<Article, String> groupsColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> groupFilterComboBox;
    
    @FXML
    private ListView<String> groupFilterListView;
    
    @FXML
    private TableColumn<Article, Integer> idColumn;
    
    @FXML
    private Button backupRestoreButton;
    
    @FXML
    private ComboBox<String> levelFilterComboBox;
        
    @FXML
    private ListView<String> levelFilterListView;
    
    @FXML
    private TextField idSearchField;
    
    @FXML
    private TableColumn<Article, String> abstractColumn;

    @FXML
    private TableColumn<Article, String> authorsColumn;
    
    /**
     * Default constructor required for FXML loader
     */
	public InstructorHomePageController() {
		super();
	}
	
	/**
	 * Constructor with stage and database parameters
	 * 
	 * @param primaryStage the main application window
	 * @param db the database instance
	 */
	public InstructorHomePageController(Stage primaryStage, Database db) {
		super(primaryStage, db);
	}
	
	/**
	 * Handles user logout and navigation to login page
	 */
	@FXML
	public void logout() {
		System.out.println("Instructor logged out.");
		navigateTo("/views/LoginPageView.fxml");
	}
	
	/**
     * Initialize the controller and set up the table
     */
    @FXML
    public void initialize() {
        try {
            db = new HelpArticleDatabase();
            initializeTable();
            loadArticles();
            
            // Initialize group filter
            List<String> groups = db.fetchGroupingIdentifiers();
            groupFilterComboBox.setItems(FXCollections.observableArrayList(groups));
            
            // Initialize level filter
            List<String> levels = Arrays.asList("beginner", "intermediate", "advanced", "expert");
            levelFilterComboBox.setItems(FXCollections.observableArrayList(levels));
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Initialize table columns and their cell factories
     */
    private void initializeTable() {
    	// Set up the sequential ID column
        idColumn.setCellValueFactory(column -> 
            new javafx.beans.property.ReadOnlyObjectWrapper<>(
                articleTable.getItems().indexOf(column.getValue()) + 1
            )
        );
    	
        // Set up the other columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        
        // Set up groups column to display the list of groups
        groupsColumn.setCellFactory(column -> new TableCell<Article, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Article article = (Article) getTableRow().getItem();
                    List<String> groups = article.getGroupingIdentifiers();
                    setText(String.join(", ", groups));
                }
            }
        });

        // Add action buttons column
        TableColumn<Article, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
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
                    buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        articleTable.getColumns().add(actionsColumn);
    }
    
    /**
     * Navigate to create new article view
     */
    @FXML
    public void createNewArticle() {
        navigateTo("/views/CreateEditArticleView.fxml");
    }
    
    /**
     * Load all articles into the table
     */
    private void loadArticles() {
        try {
            List<Article> articles = db.getAllGeneralArticles();
            articleTable.getItems().setAll(articles);
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to load articles: " + e.getMessage());
        }
    }
    
    /**
     * View an article's details
     */
    private void viewArticle(Article article) {
        UserSession.getInstance().setSelectedArticle(article);
        navigateTo("/views/ViewArticle.fxml");
    }

    /**
     * Edit an existing article
     */
    private void editArticle(Article article) {
        UserSession.getInstance().setSelectedArticle(article);
        navigateTo("/views/CreateEditArticleView.fxml");
    }

    /**
     * Delete an article with confirmation
     */
    private void deleteArticle(Article article) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Article");
        confirm.setContentText("Are you sure you want to delete this article?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    db.deleteArticle(article.getId());
                    loadArticles();
                    showInfoAlert("Success", "Article deleted successfully");
                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to delete article: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Shows the specified message alert
     * 
     * @param title The title of the message being sent
     * @param message The body of the message being sent
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Method to search and filter articles based on the search field input.
     */
    @FXML
    public void filterArticles() {
        try {
            String searchQuery = searchField.getText().toLowerCase();
            List<String> selectedGroups = new ArrayList<>(groupFilterListView.getItems());
            List<String> selectedLevels = new ArrayList<>(levelFilterListView.getItems());
            
            System.out.println("Filtering with search query: '" + searchQuery + "'");
            System.out.println("Selected groups for filtering: " + selectedGroups);
            System.out.println("Selected levels for filtering: " + selectedLevels);
            
            // Get all general articles to apply search and filters too
            List<Article> articles = db.getAllGeneralArticles();  
            
            // Additional group filtering if groups are selected
            if (!selectedGroups.isEmpty()) {
                articles = articles.stream()
                    .filter(article -> {
                        // Using the updated grouping identifiers mechanism
                        List<String> articleGroups = article.getGroupingIdentifiers();
                        return articleGroups.stream()
                            .anyMatch(selectedGroups::contains);
                    })
                    .collect(Collectors.toList());
            }

            // Filter by levels if any are selected
            if (!selectedLevels.isEmpty()) {
                articles = articles.stream()
                    .filter(article -> selectedLevels.contains(article.getLevel()))
                    .collect(Collectors.toList());
            }

            // Apply search query filter
            if (!searchQuery.isEmpty()) {
                articles = articles.stream()
                    .filter(article -> 
                        article.getTitle().toLowerCase().contains(searchQuery) ||
                        article.getAbstractText().toLowerCase().contains(searchQuery) ||
                        article.getAuthors().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            }

            articleTable.getItems().setAll(articles);
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to filter articles: " + e.getMessage());
        }
    }
    
    /**
     * Adds a selected group to the filter list and updates the article display
     */
    @FXML
    public void addGroupToFilter() {
        String selectedGroup = groupFilterComboBox.getValue();
        if (selectedGroup != null && !selectedGroup.isEmpty() 
                && !groupFilterListView.getItems().contains(selectedGroup)) {
            groupFilterListView.getItems().add(selectedGroup);
            groupFilterComboBox.setValue(null);
            filterArticles(); // Use your existing filter method
        }
    }
    
    /**
     * Removes all group filters and updates the article display
     */
    @FXML
    public void clearGroupFilters() {
        groupFilterListView.getItems().clear();
        filterArticles(); // Reapply any keyword filters
    }
    
    /**
     * Navigates to the backup and restore view
     */
    @FXML
    public void goToBackupRestore() {
        navigateTo("/views/BackupRestoreView.fxml");
    }
    
    
    // PHASE 3 ADDITIONS
    @FXML
    public void goToSpecialGroups() {
        navigateTo("/views/SelectSpecialGroupView.fxml");
    }
    
    @FXML
    public void goToCreateSpecialGroup() {
        navigateTo("/views/CreateSpecialGroupView.fxml");
    }
    
    @FXML
    public void goToMessagingSystem() {
        navigateTo("/views/MessagingSystemView.fxml");
    }
    
    
    @FXML
    public void addLevelToFilter() {
        String selectedLevel = levelFilterComboBox.getValue();
        if (selectedLevel != null && !selectedLevel.isEmpty() 
                && !levelFilterListView.getItems().contains(selectedLevel)) {
            levelFilterListView.getItems().add(selectedLevel);
            levelFilterComboBox.setValue(null);
            filterArticles();
        }
    }

    @FXML
    public void clearLevelFilters() {
        levelFilterListView.getItems().clear();
        filterArticles();
    }
    
    public void goToManageStudents() {
    	navigateTo("/views/ManageStudentsView.fxml");
    }
    
    @FXML
    public void searchById() {
        try {
            String idText = idSearchField.getText().trim();
            if (idText.isEmpty()) {
                loadArticles(); // Reset to show all articles
                return;
            }
            
            long searchId = Long.parseLong(idText);
            List<Article> articles = db.getAllGeneralArticles().stream()
                .filter(article -> article.getId() == searchId)
                .collect(Collectors.toList());
                
            articleTable.getItems().setAll(articles);
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid numeric ID");
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to search by ID: " + e.getMessage());
        }
    }
}
