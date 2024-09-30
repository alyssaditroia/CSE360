package main;

import javafx.application.Application;
import javafx.stage.Stage;
import controllers.PageController;

public class Main extends Application {

    private PageController pageController;

    @Override
    public void start(Stage primaryStage) {
        this.pageController = new PageController(primaryStage); // Initialize with the main stage
        primaryStage.setTitle("CSE360 Help System");
        showLoginPage();
        primaryStage.show();
    }

    public void showLoginPage() {
        pageController.navigateTo("/views/LoginPageView.fxml"); // Reusing PageController's method
    }

    public static void main(String[] args) {
        launch(args);
    }
}

