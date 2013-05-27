package est412.wordstrainer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WordsTrainer extends Application {
	
	static Stage mainStage;

	@Override
	public void start(Stage stage) throws Exception {
		//System.out.println(111);
		mainStage = stage;
		Parent root = FXMLLoader.load(getClass().getResource("WordsTrainer.fxml"));
        stage.setTitle("Words Trainer");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				WordsTrainerController.handleStageCloseRequest(we);
			}
		});
        stage.show();
}

	public static void main(String[] args) {
		launch(args);
	}
}
