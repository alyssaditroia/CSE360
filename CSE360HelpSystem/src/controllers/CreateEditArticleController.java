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
import models.Article;
import models.UserSession;
/**
 * The {@code CreateEditArticleController} handles the user input for creating and editing help articles
 */
public class CreateEditArticleController extends PageController {

    private HelpArticleDatabase had;
    private Article articleToEdit;  // Holds the article being edited

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
    private ComboBox<String> groupingSearchComboBox;  // Dropdown for all identifiers

    @FXML
    private ListView<String> articleGroupingListView;  // List of identifiers tied to the article

    @FXML
    private Button addGroupingButton;

    @FXML
    private CheckBox adminCheckbox, instructorCheckbox, studentCheckbox;

    @FXML
    private DatePicker dateAddedField;

    @FXML
    private TextField versionField;

    @FXML
    private Button saveButton;  // Save the article and navigate back to the list

    @FXML
    private Button backToList;  // Navigate back to the list without saving

    @FXML
    private Button cancel;  // Cancel editing and return to the list

    // Constructors
    public CreateEditArticleController() {
        super();
    }

    public CreateEditArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();

            // Load existing grouping identifiers from the database
            List<String> existingIdentifiers = had.fetchGroupingIdentifiers();
            groupingSearchComboBox.setItems(FXCollections.observableArrayList(existingIdentifiers));

            // Make the ComboBox editable, allowing users to type
            groupingSearchComboBox.setEditable(true);

            // Set a listener to update the dropdown based on input
            groupingSearchComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    filterGroupingSuggestions(newValue);
                }
            });

            // Set action to add the selected or new identifier to the article-specific list
            addGroupingButton.setOnAction(event -> addGroupingIdentifier());

            // Add context menu for removing identifiers from the article-specific list
            addContextMenuToArticleGroupingList();

            // Set up level dropdown options
            levelField.setItems(FXCollections.observableArrayList("beginner", "intermediate", "advanced", "expert"));

            // Load data into the fields if editing an existing article
            articleToEdit = UserSession.getInstance().getSelectedArticle();
            if (articleToEdit != null) {
                loadArticleData(articleToEdit);
            } else {
            	clearFields(); 
            }
        } 
        catch (Exception e) {
            //showErrorAlert("Error", "Failed to initialize the CreateEditArticleController: " + e.getMessage());
            System.out.println("[ERROR] (not actually error when creating new article): " + e.getMessage());
        }
    }

    // Load article data for editing
    public void loadArticleData(Article article) {
        titleField.setText(article.getTitle());
        authorsField.setText(article.getAuthors());
        abstractField.setText(article.getAbstractText());
        keywordsField.setText(article.getKeywords());
        bodyField.setText(article.getBody());
        referencesField.setText(article.getReferences());
        levelField.setValue(article.getLevel());

        // Set permissions checkboxes
        String permissions = article.getPermissions();
        adminCheckbox.setSelected(permissions.contains("Admin"));
        instructorCheckbox.setSelected(permissions.contains("Instructor"));
        studentCheckbox.setSelected(permissions.contains("Student"));

        // Set article-specific grouping identifiers (multi-select)
        List<String> identifiers = article.getGroupingIdentifiers();
        articleGroupingListView.getItems().clear();
        articleGroupingListView.getItems().addAll(identifiers);  // Populate the ListView with article-specific identifiers
    }

    /**
     * Adds a grouping identifier (either new or existing) to the article's list.
     */
    @FXML
    public void addGroupingIdentifier() {
        String newIdentifier = groupingSearchComboBox.getEditor().getText();

        if (!newIdentifier.isEmpty()) {
            // Check if the identifier already exists in the global list
            if (!groupingSearchComboBox.getItems().contains(newIdentifier)) {
                // Add the new identifier to the database and update the dropdown
                try {
                    had.insertGroupingIdentifier(newIdentifier);
                    groupingSearchComboBox.getItems().add(newIdentifier);  // Update ComboBox with new identifier
                    showInfoAlert("Success", "New identifier added: " + newIdentifier);
                } catch (SQLException e) {
                    showErrorAlert("Error", "Failed to insert grouping identifier: " + e.getMessage());
                    return;
                }
            }

            // Add the identifier to the article's specific grouping list if it's not already there
            if (!articleGroupingListView.getItems().contains(newIdentifier)) {
                articleGroupingListView.getItems().add(newIdentifier);
            }

            // Clear the search field
            groupingSearchComboBox.getEditor().clear();
        }
    }

    /**
     * Filters the grouping suggestions in the ComboBox based on the user's input.
     */
    private void filterGroupingSuggestions(String input) {
        List<String> filteredIdentifiers = FXCollections.observableArrayList();  // Initialize with an empty list
        
        try {
            filteredIdentifiers = had.fetchGroupingIdentifiers().stream()
                .filter(identifier -> identifier.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            // Log the exception or show an alert to the user
            e.printStackTrace();
            showErrorAlert("Error", "Failed to fetch grouping identifiers.");
        }

        groupingSearchComboBox.setItems(FXCollections.observableArrayList(filteredIdentifiers));
    }

    /**
     * Adds a context menu to the ListView to remove a specific grouping identifier from the article.
     */
    private void addContextMenuToArticleGroupingList() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(event -> {
            String selectedItem = articleGroupingListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                articleGroupingListView.getItems().remove(selectedItem); // Remove the selected item from the ListView
            }
        });
        contextMenu.getItems().add(removeItem);

        // Attach the context menu to the ListView
        articleGroupingListView.setContextMenu(contextMenu);
    }

    /**
     * Method to save the article (either creating a new article or updating an existing one).
     */
    @FXML
    public void saveArticle() {
        try {
            // Collect data from input fields
            char[] title = titleField.getText().toCharArray();
            char[] authors = authorsField.getText().toCharArray();
            char[] abstractText = abstractField.getText().toCharArray();
            char[] keywords = keywordsField.getText().toCharArray();
            char[] body = bodyField.getText().toCharArray();
            char[] references = referencesField.getText().toCharArray();
            String level = levelField.getValue();

            // Get the selected grouping identifiers from the article-specific ListView
            List<String> selectedIdentifiers = articleGroupingListView.getItems();  
            String permissions = getSelectedPermissions(); 

            java.sql.Date dateAdded = java.sql.Date.valueOf(dateAddedField.getValue());
            String version = versionField.getText();

            // Check if we are editing an existing article or creating a new one
            if (articleToEdit == null) {
                // Creating a new article
                Article newArticle = new Article(title, authors, abstractText, keywords, body, references, level,
                        selectedIdentifiers, permissions, dateAdded, version);
                // Insert into the database
                had.createArticle(newArticle);  
                showInfoAlert("Success", "Article created successfully!");
            } else {
                // Editing an existing article
                articleToEdit.setTitle(title);
                articleToEdit.setAuthors(authors);
                articleToEdit.setAbstractText(abstractText);
                articleToEdit.setKeywords(keywords);
                articleToEdit.setBody(body);
                articleToEdit.setReferences(references);
                articleToEdit.setLevel(level);
                articleToEdit.setGroupingIdentifiers(selectedIdentifiers);  // Update identifiers in the article
                articleToEdit.setPermissions(permissions);
                articleToEdit.setDateAdded(dateAdded);
                articleToEdit.setVersion(version);

                // Update the article in the database
                had.updateArticle(articleToEdit);
                showInfoAlert("Success", "Article updated successfully!");
            }

            goBackToList();

        } catch (Exception e) {
            showErrorAlert("Error", "Failed to save article: " + e.getMessage());
        }
    }

    /**
     * Method to handle the back to list action.
     */
    @FXML
    public void goBackToList() {
        String currentRole = UserSession.getInstance().getCurrentRole();
        if ("admin".equals(currentRole)) {
            navigateTo("/views/SearchArticleView.fxml");
        } else {
            goHome();
        }
    }

    /**
     * Cancel editing and go back to the list without saving.
     */
    @FXML
    public void cancel() {
        goBackToList();
    }

    /**
     * Helper method to get selected permissions from checkboxes.
     */
    private String getSelectedPermissions() {
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
        if (permissions.length() > 0) {
            permissions.setLength(permissions.length() - 1);
        }
        return permissions.toString();
    }

    /**
     * Clears all the fields after an article is saved or canceled.
     */
    private void clearFields() {
        titleField.clear();
        authorsField.clear();
        abstractField.clear();
        keywordsField.clear();
        bodyField.clear();
        referencesField.clear();
        levelField.setValue(null);  // Clear the level selection
        articleGroupingListView.getItems().clear();  // Clear any grouping identifiers
        adminCheckbox.setSelected(false);
        instructorCheckbox.setSelected(false);
        studentCheckbox.setSelected(false);
        dateAddedField.setValue(null);  // Clear the date
        versionField.clear();  // Clear the version field
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}





