package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import database.Database;
import database.HelpArticleDatabase;
import database.SpecialGroupsDatabase;
import models.Article;
import models.UserSession;
import models.SpecialGroup;

public class SpecialGroupAddEditArticleController extends PageController {
    
    private HelpArticleDatabase had;
    private SpecialGroupsDatabase specialGroupsDB;
    private Article articleToEdit;
    private SpecialGroup currentGroup;
    
    @FXML private TextField titleField;
    @FXML private TextField authorsField;
    @FXML private TextArea abstractField;
    @FXML private TextArea keywordsField;
    @FXML private TextArea bodyField;
    @FXML private TextArea referencesField;
    @FXML private ComboBox<String> levelField;
    @FXML private ComboBox<String> groupingSearchComboBox;
    @FXML private ListView<String> articleGroupingListView;
    @FXML private Button addGroupingButton;
    @FXML private CheckBox adminCheckbox, instructorCheckbox, studentCheckbox;
    @FXML private DatePicker dateAddedField;
    @FXML private TextField versionField;
    @FXML private Button saveButton;
    @FXML private Button backToList;
    @FXML private Button cancel;
    @FXML private Label groupNameLabel;

    public SpecialGroupAddEditArticleController() {
        super();
    }
    
    public SpecialGroupAddEditArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }
    
    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();
            specialGroupsDB = new SpecialGroupsDatabase();
            currentGroup = UserSession.getInstance().getSelectedSpecialGroup();

            List<String> existingIdentifiers = had.fetchGroupingIdentifiers();
            groupingSearchComboBox.setItems(FXCollections.observableArrayList(existingIdentifiers));
            groupingSearchComboBox.setEditable(true);
            groupingSearchComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    filterGroupingSuggestions(newValue);
                }
            });

            addGroupingButton.setOnAction(event -> addGroupingIdentifier());
            addContextMenuToArticleGroupingList();
            levelField.setItems(FXCollections.observableArrayList("beginner", "intermediate", "advanced", "expert"));

            articleToEdit = UserSession.getInstance().getSelectedArticle();
            if (articleToEdit != null) {
                loadArticleData(articleToEdit);
            } else {
                clearFields();
            }
        } catch (Exception e) {
            System.out.println("[ERROR] (not actually error when creating new article): " + e.getMessage());
        }
    }

    public void loadArticleData(Article article) {
        titleField.setText(article.getTitle());
        authorsField.setText(article.getAuthors());
        abstractField.setText(article.getAbstractText());
        keywordsField.setText(article.getKeywords());
        bodyField.setText(article.getBody());
        referencesField.setText(article.getReferences());
        levelField.setValue(article.getLevel());

        String permissions = article.getPermissions();
        adminCheckbox.setSelected(permissions.contains("Admin"));
        instructorCheckbox.setSelected(permissions.contains("Instructor"));
        studentCheckbox.setSelected(permissions.contains("Student"));

        List<String> identifiers = article.getGroupingIdentifiers();
        articleGroupingListView.getItems().clear();
        articleGroupingListView.getItems().addAll(identifiers);
    }

    @FXML
    public void addGroupingIdentifier() {
        String newIdentifier = groupingSearchComboBox.getEditor().getText();

        if (!newIdentifier.isEmpty()) {
            if (!groupingSearchComboBox.getItems().contains(newIdentifier)) {
                try {
                    had.insertGroupingIdentifier(newIdentifier);
                    groupingSearchComboBox.getItems().add(newIdentifier);
                    showInfoAlert("Success", "New identifier added: " + newIdentifier);
                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to insert grouping identifier: " + e.getMessage());
                    return;
                }
            }

            if (!articleGroupingListView.getItems().contains(newIdentifier)) {
                articleGroupingListView.getItems().add(newIdentifier);
            }

            groupingSearchComboBox.getEditor().clear();
        }
    }

    private void filterGroupingSuggestions(String input) {
        List<String> filteredIdentifiers = FXCollections.observableArrayList();
        
        try {
            filteredIdentifiers = had.fetchGroupingIdentifiers().stream()
                .filter(identifier -> identifier.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to fetch grouping identifiers.");
        }

        groupingSearchComboBox.setItems(FXCollections.observableArrayList(filteredIdentifiers));
    }

    private void addContextMenuToArticleGroupingList() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(event -> {
            String selectedItem = articleGroupingListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                articleGroupingListView.getItems().remove(selectedItem);
            }
        });
        contextMenu.getItems().add(removeItem);
        articleGroupingListView.setContextMenu(contextMenu);
    }

    @FXML
    public void saveArticle() {
        try {
            char[] title = titleField.getText().toCharArray();
            char[] authors = authorsField.getText().toCharArray();
            char[] abstractText = abstractField.getText().toCharArray();
            char[] keywords = keywordsField.getText().toCharArray();
            char[] body = bodyField.getText().toCharArray();
            char[] references = referencesField.getText().toCharArray();
            String level = levelField.getValue();
            List<String> selectedIdentifiers = articleGroupingListView.getItems();
            String permissions = getSelectedPermissions();
            java.sql.Date dateAdded = java.sql.Date.valueOf(dateAddedField.getValue());
            String version = versionField.getText();

            if (articleToEdit == null) {
                Article newArticle = new Article(title, authors, abstractText, keywords, body, references, level,
                        selectedIdentifiers, permissions, dateAdded, version);
                
                // Set special group flag
                newArticle.setAsPartOfSpecialGroup();
                
                // Save to help article database
                had.createArticle(newArticle);
                
                // Get the most recently created article's ID
                List<Article> allArticles = had.getAllDecryptedArticles();
                Article savedArticle = allArticles.get(allArticles.size() - 1);
                
                // Save to special groups database using the retrieved ID
                specialGroupsDB.addArticleToGroup(
                    currentGroup.getGroupId(), 
                    String.valueOf(savedArticle.getId())
                );
                
                showInfoAlert("Success", "Article created successfully!");
            } else {
                articleToEdit.setTitle(title);
                articleToEdit.setAuthors(authors);
                articleToEdit.setAbstractText(abstractText);
                articleToEdit.setKeywords(keywords);
                articleToEdit.setBody(body);
                articleToEdit.setReferences(references);
                articleToEdit.setLevel(level);
                articleToEdit.setGroupingIdentifiers(selectedIdentifiers);
                articleToEdit.setPermissions(permissions);
                articleToEdit.setDateAdded(dateAdded);
                articleToEdit.setVersion(version);
                articleToEdit.setAsPartOfSpecialGroup();
                
                had.updateArticle(articleToEdit);
                
                // Make sure the article is still associated with the special group
                specialGroupsDB.addArticleToGroup(
                    currentGroup.getGroupId(),
                    String.valueOf(articleToEdit.getId())
                );
                
                showInfoAlert("Success", "Article updated successfully!");
            }
            
            
            goBackToList();
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to save article: " + e.getMessage());
        }
    }

    @FXML
    public void goBackToList() {
        navigateTo("/views/SpecialGroupView.fxml");
    }

    @FXML
    public void cancel() {
        goBackToList();
    }

    private String getSelectedPermissions() {
        StringBuilder permissions = new StringBuilder();
        if (adminCheckbox.isSelected()) permissions.append("Admin,");
        if (instructorCheckbox.isSelected()) permissions.append("Instructor,");
        if (studentCheckbox.isSelected()) permissions.append("Student,");
        if (permissions.length() > 0) {
            permissions.setLength(permissions.length() - 1);
        }
        return permissions.toString();
    }

    private void clearFields() {
        titleField.clear();
        authorsField.clear();
        abstractField.clear();
        keywordsField.clear();
        bodyField.clear();
        referencesField.clear();
        levelField.setValue(null);
        articleGroupingListView.getItems().clear();
        adminCheckbox.setSelected(false);
        instructorCheckbox.setSelected(false);
        studentCheckbox.setSelected(false);
        dateAddedField.setValue(null);
        versionField.clear();
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}