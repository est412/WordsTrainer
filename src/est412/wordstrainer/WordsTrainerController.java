package est412.wordstrainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import est412.wordstrainer.model.Dictionary;
import est412.wordstrainer.model.XLSXAspDictionary;
import est412.wordstrainer.model.DictionaryIterator;

public class WordsTrainerController {
	@FXML private TextArea textareaLang0;
	@FXML private TextArea textareaLang1;
	@FXML private Label labelFile;
	private TextArea[] textareaLang = new TextArea[2];
	@FXML private CheckBox checkboxToRepeat;
	@FXML private CheckBox checkboxExample;
	@FXML private CheckBox checkboxLang0;
	@FXML private CheckBox checkboxLang1;
	@FXML private CheckBox checkboxRndLang;
	private CheckBox[] checkboxLang = new CheckBox[2]; 
	@FXML private Label labelIdxWordsCntLang0;
	@FXML private Label labelIdxWordsCntLang1;
	@FXML private Label labelIdxWordsNumber;
	@FXML private Button buttonNext;
	@FXML private Button buttonRestart;
	@FXML private HBox hboxLang;
	@FXML private TextArea textareaLang0Example;
	@FXML private TextArea textareaLang1Example;
	@FXML private ChoiceBox<String> choiceboxMode;
	@FXML private AnchorPane anchorpaneMain;
		
	private Dictionary dict;
	private DictionaryIterator dictIterator;
	private File file;
	private int toShow;
	private int shown;
	private Stage mainStage;
	
	private List<String> modes = new ArrayList<String>();
		
	BooleanBinding isCheckBoxRndEnable;
	
	public WordsTrainerController() {
		modes.add("Все слова");
		modes.add("Повторение");
	}
	
	@FXML
	protected void initialize() {
		choiceboxMode.setItems(FXCollections.observableArrayList("Все слова", "Повторение"));
		choiceboxMode.getSelectionModel().selectFirst();
	}
	
	public void initStage(Stage stage) {
		mainStage = stage;
		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				try { 
					if (dict != null) {
						dict.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@FXML protected void handleFileButtonAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File initDir = new File(System.getProperty("user.dir"));
		 
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(initDir);
       
        //Show open file dialog
        file = fileChooser.showOpenDialog(mainStage);
        if (file == null) return;
        
        try {
			if (dict != null) {
				dict.close();
			}
        	dict = new XLSXAspDictionary(file.getAbsolutePath());
        } catch (Exception e) {
        	e.printStackTrace();
        }
        textareaLang[0] = textareaLang0;
        textareaLang[1] = textareaLang1;
        checkboxLang[0] = checkboxLang0;
        checkboxLang[1] = checkboxLang1;
        labelFile.setText(file.getAbsolutePath());
        dictIterator = new DictionaryIterator(dict);
        buttonNext.disableProperty().unbind(); //?
        buttonNext.disableProperty().setValue(false); //?
        buttonRestart.disableProperty().setValue(true);
        hboxLang.setDisable(false);
        checkboxExample.disableProperty().setValue(false);
        changeSystemState();
        setBindings();
        toShow = 1;
        shown = -1;
        checkboxToRepeat.setDisable(true);
        choiceboxMode.setDisable(false);
        dictIterator.mode.unbind();
        dictIterator.mode.bind(choiceboxMode.getSelectionModel().selectedIndexProperty());
  	}
	
	@FXML protected void handleRestartButtonAction(ActionEvent event) {
        dictIterator = new DictionaryIterator(dict);
		//dictIterator.initIndex();
		dictIterator.clearCurWord();
		toShow = 1;
		shown = -1;
		changeSystemState();
		setBindings();
		checkboxToRepeat.setDisable(true);
		choiceboxMode.setDisable(false);
		dictIterator.mode.unbind();
		dictIterator.mode.bind(choiceboxMode.getSelectionModel().selectedIndexProperty());
	}
		
	@FXML protected void handleNextButtonAction(ActionEvent event) {
		choiceboxMode.setDisable(true);
		if (toShow == 1) {
			buttonNext.disableProperty().unbind();
			dictIterator.nextWord();
			hboxLang.setDisable(true);
			shown = 1;
			if (checkboxExample.isSelected()) toShow = 2;
			else toShow = 3;
		}
		else if (toShow == 2 && checkboxExample.isSelected()) {
			dictIterator.showExample();
			toShow = 3;
			shown = 2;
		}
		else if (toShow == 3) {
			dictIterator.translateCurWord();
			hboxLang.setDisable(false);
			if (checkboxExample.isSelected()) toShow = 4;
			else { 
				toShow = 1;
				changeSystemState();
			}
			shown = 3;
		}
		else if (toShow == 4 && checkboxExample.isSelected()) {
			dictIterator.showTrExample();
			toShow = 1;
			shown = 4;
			changeSystemState();
		}
		buttonRestart.disableProperty().setValue(false);
		checkboxToRepeat.setDisable(false);
	}
	
	@FXML protected void handleExampleCheckBoxAction(ActionEvent event) {
		if (!checkboxExample.isSelected()) {
			dictIterator.hideExamples();
			return;
		}
		if (shown >= 1) dictIterator.showExample();
		if (shown >= 3) dictIterator.showTrExample();
	}
	
	@FXML protected void handleLang0CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected())
			checkboxLang1.setSelected(true);
		changeSystemState();
	}
	
	@FXML protected void handleLang1CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected()) 
			checkboxLang0.setSelected(true);
		changeSystemState();
	}
	
	protected void handleStageCloseRequest(WindowEvent we) {
	}
	
	@FXML protected void handleRepeatCheckBoxAction(ActionEvent event) {
		
	}
	
	private void changeSystemState() {
		if (checkboxLang0.isSelected() && checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(2);
		}
		if (!checkboxLang0.isSelected()) {
			dictIterator.setActiveLangs(1);
		}
		if (!checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(0);
		}
		buttonNext.disableProperty().bind(dictIterator.showEmpty);
	}
	
	private void setBindings() {
		checkboxLang0.disableProperty().bind(dictIterator.idxEmptyProperty(0));
		checkboxLang1.disableProperty().bind(dictIterator.idxEmptyProperty(1));
		
		isCheckBoxRndEnable = Bindings.and(
			checkboxLang0.selectedProperty().and(checkboxLang0.disabledProperty().not()),
			checkboxLang1.selectedProperty().and(checkboxLang1.disabledProperty().not()));
		checkboxRndLang.disableProperty().bind(isCheckBoxRndEnable.not());
		
		dictIterator.langRndProperty().bind(checkboxRndLang.selectedProperty());
		
		textareaLang0.textProperty().bind(dictIterator.curWordProperty(0));
		textareaLang1.textProperty().bind(dictIterator.curWordProperty(1));
		
		textareaLang0Example.textProperty().bind(dictIterator.curExampleProperty(0));
		textareaLang1Example.textProperty().bind(dictIterator.curExampleProperty(1));
		
		labelIdxWordsCntLang1.textProperty().bind(dictIterator.showCounter.asString());
		labelIdxWordsNumber.textProperty().bind(dictIterator.showNumber.asString());
		
		checkboxToRepeat.selectedProperty().bindBidirectional(dictIterator.toRepeat);
		
		buttonNext.disableProperty().bind(dictIterator.showEmpty);
		
		dictIterator.showExampleProperty().bind(checkboxExample.selectedProperty());
	}
}
