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
/**
 * <p>
 * Title: InstructorHomePageController
 * </p>
 * 
 * <p>
 * Description: Manages the page that displays a selected help article's information
 * </p>
 */
public class ViewArticleController extends PageController {
	
	/**
	 * The help article that is currently being viewed on the page
	 */
    private Article articleToView;

    /**
     * Default constructor required for FXML loader initialization.
     */
    public ViewArticleController() {
        super();
    }

    /**
     * Constructs a ViewArticleController with the specified stage and database.
     *
     * @param primaryStage The main application window
     * @param db The database instance to be used
     */
    public ViewArticleController(Stage primaryStage, Database db) {
        super(primaryStage, db);
    }

    /**
     * FXML injected UI elements for the view of the page
     */
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

    /**
     * Initializes the controller and populates the UI fields with the selected article's data.
     * Displays an error alert if no article is selected.
     */
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
            
         // Hide edit button if user is a student
            String currentRole = UserSession.getInstance().getCurrentRole();
            editArticle.setVisible(!"student".equals(currentRole));
        } else {
            showErrorAlert("Error", "No article selected.");
        }
    }

    /**
     * Handles navigation back to the previous view based on user role.
     * Admins are directed to the article management view, while instructors and students
     * return to their respective homepages.
     */
    @FXML
    public void goBackToList() {
        // Clear the selected article when leaving the view page
        UserSession.getInstance().setSelectedArticle(null);
        
        String currentRole = UserSession.getInstance().getCurrentRole();
        if ("admin".equals(currentRole)) {
            // Admins go to the article management view
            navigateTo("/views/SearchArticleView.fxml");
        } else if("student".equals(currentRole))  {
        	// Students go to their general article view
        	navigateTo("/views/StudentHomepageView.fxml");
        } else {
            // Instructors go back to their homepage
            goHome();
        }
    }

    /**
     * Navigates to the edit article view for the currently selected article.
     */
    @FXML
    public void goToEditArticle() {
    	if(UserSession.getInstance().getSelectedArticle().checkSpecialGroupArticle() == true) {
    		navigateTo("/views/SpecialGroupAddEditArticleView.fxml");
    	} else {
    		navigateTo("/views/CreateEditArticleView.fxml"); 
    	}    
    }
}