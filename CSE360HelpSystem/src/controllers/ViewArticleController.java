/**
 * 
 */
package controllers;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import database.Database;
import models.Article;
import models.UserSession;

public class ViewArticleController extends PageController {
    private Article articleToView;

    // Constructors
    public ViewArticleController() {
        super();
    }

    public ViewArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    // UI Elements
    @FXML
    private TextField titleField;  

    @FXML
    private TextField authorsField;  

    @FXML
    private TextArea abstractField;  // TextArea for multi-line fields

    @FXML
    private TextArea keywordsField;

    @FXML
    private TextArea bodyField;

    @FXML
    private TextArea referencesField;

    @FXML
    private Button backToList;

    @FXML
    private Button editArticle;
    
    @FXML
    private Button logout;

    @FXML
    public void initialize() {
        Article selectedArticle = UserSession.getInstance().getSelectedArticle();
        if (selectedArticle != null) {
            // Load the article details into the UI components
            titleField.setText(selectedArticle.getTitle());
            authorsField.setText(selectedArticle.getAuthors());
            abstractField.setText(selectedArticle.getAbstractText());
            keywordsField.setText(selectedArticle.getKeywords());
            bodyField.setText(selectedArticle.getBody());  // Ensure body content is loaded here
            referencesField.setText(selectedArticle.getReferences());
        } else {
            showErrorAlert("Error", "No article selected.");
        }
    }

    // Navigation method to go back to the search list
    @FXML
    public void goBackToList() {
        String currentRole = UserSession.getInstance().getCurrentRole();
        if ("admin".equals(currentRole)) {
            // Admins go to the article management view
            navigateTo("/views/SearchArticleView.fxml");
        } else {
        	// Instructors and students go back to their homepage
            goHome();
        }
    }

    // Navigate to the edit article view
    @FXML
    public void goToEditArticle() {
        navigateTo("/views/CreateEditArticleView.fxml"); // Navigate to the article edit page
    }
}