import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/ressources/Views/LoginMenu.fxml"));
		
		Image icon = new Image(getClass().getResourceAsStream("/ressources/Images/Logo.png"));
		Scene frame = new Scene(root);
		primaryStage.isResizable();
		primaryStage.setTitle("StoreWise");
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(frame);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}