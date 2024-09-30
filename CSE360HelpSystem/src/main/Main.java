package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

	    @Override
	    public void start(Stage primaryStage) throws Exception {
	        primaryStage.setTitle("Your Application Title");
	        showLoginPage(primaryStage);
	        primaryStage.show();
	    }

	    public void showLoginPage(Stage stage) throws Exception {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginPageView.fxml"));
	        Scene scene = new Scene(loader.load());
	        stage.setScene(scene);
	    }

	    public void showSetupAccountPage(Stage stage) throws Exception {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SetupAccountPageView.fxml"));
	        Scene scene = new Scene(loader.load());
	        stage.setScene(scene);
	    }

	    public static void main(String[] args) {
	        launch(args);
	    }
	}

