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
import java.util.List;
import java.util.stream.Collectors;

import database.Database;
import database.SpecialGroupsDatabase;
import database.HelpArticleDatabase;
import models.Article;
import models.SpecialGroup;
import models.UserSession;

public class SpecialGroupViewController extends PageController {
    @FXML private TableView<Article> articleTable;
    @FXML private TableColumn<Article, Integer> idColumn;
    @FXML private TableColumn<Article, String> titleColumn;
    @FXML private TableColumn<Article, String> abstractColumn;
    @FXML private TableColumn<Article, String> authorsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> groupFilterComboBox;
    @FXML private ListView<String> groupFilterListView;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label groupNameLabel;
    @FXML private Button manageUsersButton;

    private SpecialGroupsDatabase specialGroupsDB;
    private HelpArticleDatabase helpArticleDB;

    public SpecialGroupViewController() {
        super();
    }
    
    // Constructor with stage and database
    public SpecialGroupViewController(Stage stage, Database db) {
        super(stage, db);
    }

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
                currentGroup.getGroupArticles().clear();  // Clear existing articles
                for (String articleId : freshArticles) {  // Add fresh articles
                    currentGroup.addArticle(articleId);
                }
                
                // Set group name in title
                if (groupNameLabel != null) {
                    groupNameLabel.setText(currentGroup.getName());
                }
                
                // Load articles
                List<Article> allArticles = helpArticleDB.getAllDecryptedArticles();
                List<Article> groupArticles = allArticles.stream()
                    .filter(article -> currentGroup.getGroupArticles().contains(String.valueOf(article.getId())))
                    .collect(Collectors.toList());
                    
                if (articleTable != null) {
                    articleTable.setItems(FXCollections.observableArrayList(groupArticles));
                }
            }
            
            // Only setup if components are injected
            if (articleTable != null) {
                setupTableColumns();
                setupAccessControls();
                loadGroupFilters();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize special group view");
            goHome();
        }
    }
    
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
                    buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        articleTable.getColumns().add(actionsColumn);
    }

    private void setupAccessControls() {
        int accessLevel = UserSession.getInstance().getAccessLevel();
        if (createButton != null) {
            createButton.setVisible(accessLevel >= 2);
        }
        if (editButton != null) {
            editButton.setVisible(accessLevel >= 3);
        }
        if (deleteButton != null) {
            deleteButton.setVisible(accessLevel >= 2);
        }
    }

    @FXML
    public void loadArticles() {
        try {
            String searchQuery = searchField.getText().toLowerCase();
            List<String> selectedGroups = new ArrayList<>(groupFilterListView.getItems());
            
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            
            List<Article> articles = helpArticleDB.getAllDecryptedArticles().stream()
                .filter(article -> currentGroup.getGroupArticles().contains(String.valueOf(article.getId())))  // Add this line
                .filter(article -> matchesSearch(article, searchQuery))
                .filter(article -> matchesGroupFilters(article, selectedGroups))
                .collect(Collectors.toList());
                
            articleTable.setItems(FXCollections.observableArrayList(articles));
        } catch (Exception e) {
            showErrorAlert("Loading Error", "Failed to load articles: " + e.getMessage());
        }
    }

    private boolean matchesSearch(Article article, String query) {
        if (query.isEmpty()) return true;
        return article.getTitle().toLowerCase().contains(query) ||
               article.getAuthors().toLowerCase().contains(query) ||
               article.getAbstractText().toLowerCase().contains(query);
    }

    private boolean matchesGroupFilters(Article article, List<String> filters) {
        return filters.isEmpty() || article.getGroupingIdentifiers().stream()
            .anyMatch(filters::contains);
    }

    @FXML
    public void addGroupToFilter() {
        String selectedGroup = groupFilterComboBox.getValue();
        if (selectedGroup != null && !groupFilterListView.getItems().contains(selectedGroup)) {
            groupFilterListView.getItems().add(selectedGroup);
            groupFilterComboBox.setValue(null);
            loadArticles();
        }
    }

    @FXML
    public void clearGroupFilters() {
        groupFilterListView.getItems().clear();
        loadArticles();
    }

    private void loadGroupFilters() {
        try {
            List<String> groups = helpArticleDB.fetchGroupingIdentifiers();
            groupFilterComboBox.setItems(FXCollections.observableArrayList(groups));
        } catch (Exception e) {
            showErrorAlert("Filter Error", "Failed to load group filters");
        }
    }
    
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

    private void deleteArticle(Article article) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Article");
        confirm.setContentText("Are you sure you want to delete this article from the special group?");

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
                    
                    // Refresh the table
                    loadArticles();
                    
                    showInfoAlert("Success", "Article removed from special group successfully");
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
}