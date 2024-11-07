package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

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
            
            // Get the selected group's articles
            SpecialGroup currentGroup = UserSession.getInstance().getSelectedSpecialGroup();
            if (currentGroup != null) {
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractText"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
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
            
            List<Article> articles = helpArticleDB.getAllDecryptedArticles().stream()
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
    
    
}