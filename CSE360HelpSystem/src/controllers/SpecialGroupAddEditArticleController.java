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
/**
 * The {@code SpecialGroupAddEditArticleController} is for creating and editing help articles within a special group.
 * allows users to input and update article details, configure permissions, and save or cancel article edits. 
 */
public class SpecialGroupAddEditArticleController extends PageController {
    // Instances
    private HelpArticleDatabase had;
    private SpecialGroupsDatabase specialGroupsDB;
    private Article articleToEdit;
    private SpecialGroup currentGroup;
    
    // FXML COMPONENTS
    @FXML private TextField titleField;
    @FXML private TextField authorsField;
    @FXML private TextArea abstractField;
    @FXML private TextArea keywordsField;
    @FXML private TextArea bodyField;
    @FXML private TextArea referencesField;
    @FXML private ComboBox<String> levelField;
    @FXML private CheckBox adminCheckbox, instructorCheckbox, studentCheckbox;
    @FXML private DatePicker dateAddedField;
    @FXML private TextField versionField;
    @FXML private Button saveButton;
    @FXML private Button backToList;
    @FXML private Button cancel;
    @FXML private Label groupNameLabel;
    
    // CONSTRUCTORS
    public SpecialGroupAddEditArticleController() {
        super();
    }
    
    public SpecialGroupAddEditArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }
    // Initialization 
    @FXML
    public void initialize() {
        try {
            had = new HelpArticleDatabase();
            specialGroupsDB = new SpecialGroupsDatabase();
            currentGroup = UserSession.getInstance().getSelectedSpecialGroup();

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
    /**
     * Fills in the article data in the respective fields
     * @param article
     */
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
    }

    
    /**
     * Handles saving the article
     */
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

            String permissions = getSelectedPermissions();
            java.sql.Date dateAdded = java.sql.Date.valueOf(dateAddedField.getValue());
            String version = versionField.getText();

            if (articleToEdit == null) {
                Article newArticle = new Article(title, authors, abstractText, keywords, body, references, level,
                		List.of(), permissions, dateAdded, version);
                
                // Set special group flag
                newArticle.setAsPartOfSpecialGroup();
                
                // Set article in user session
                UserSession.getInstance().setSelectedArticle(newArticle);
                
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
                articleToEdit.setGroupingIdentifiers(List.of());
                articleToEdit.setPermissions(permissions);
                articleToEdit.setDateAdded(dateAdded);
                articleToEdit.setVersion(version);
                articleToEdit.setAsPartOfSpecialGroup();
                
                had.updateArticle(articleToEdit);
                
                showInfoAlert("Success", "Article updated successfully!");
            }
            
            
            goBackToList();
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to save article: " + e.getMessage());
        }
    }
    /**
     * Navigates back to the list of articles in the special group
     */
    @FXML
    public void goBackToList() {
    	UserSession.getInstance().setSelectedArticle(null);
        navigateTo("/views/SpecialGroupView.fxml");
    }

    /**
     * Navigates back to the list of articles in the special group
     */
    @FXML
    public void cancel() {
        goBackToList();
    }
    /**
     * Returns a comma-separated string of selected permissions based on the checkboxes
     * @return
     */
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
    /**
     * Resets all input fields and checkboxes, preparing the form for a new article.
     */
    private void clearFields() {
        titleField.clear();
        authorsField.clear();
        abstractField.clear();
        keywordsField.clear();
        bodyField.clear();
        referencesField.clear();
        levelField.setValue(null);
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