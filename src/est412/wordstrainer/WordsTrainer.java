package est412.wordstrainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WordsTrainer extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("WordsTrainer.fxml"));
		final Parent root = (Parent) loader.load();
        stage.setTitle("Words Trainer");
        stage.setScene(new Scene(root));
        final WordsTrainerController controller = loader.getController();
        controller.initStage(stage);
        stage.show();
        //System.out.println(1);
}

	public static void main(String[] args) {
		launch(args);
	}
}
