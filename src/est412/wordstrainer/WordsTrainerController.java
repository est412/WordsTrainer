package est412.wordstrainer;

import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import est412.wordstrainer.model.Dictionary;
import est412.wordstrainer.model.XLSXDictionary;
import est412.wordstrainer.model.DictionaryIterator;

public class WordsTrainerController {
	@FXML private TextArea textareaLang0;
	@FXML private TextArea textareaLang1;
	@FXML private Label labelFile;
	private TextArea[] textareaLang = new TextArea[2];
	@FXML private CheckBox checkboxRepeat;
	@FXML private CheckBox checkboxExample;
	@FXML private CheckBox checkboxLang0;
	@FXML private CheckBox checkboxLang1;
	@FXML private CheckBox checkboxRndLang;
	private CheckBox[] checkboxLang = new CheckBox[2]; 
	@FXML private Label labelIdxWordsCntLang0;
	@FXML private Label labelIdxWordsCntLang1;
	@FXML private Label labelIdxWordsNumber;
	@FXML private Button buttonRepeat;
	@FXML private Button buttonNext;
	@FXML private Button buttonRestart;
	@FXML private HBox hboxLang;
	@FXML private TextArea textareaLang0Example;
	@FXML private TextArea textareaLang1Example;
	
	static private Dictionary dict;
	private DictionaryIterator dictIterator;
	private boolean isTranslate;
	private File file;
	
	BooleanBinding isCheckBoxRndEnable;
		
	@FXML protected void handleRepeatButtonAction(ActionEvent event) {
		dictIterator.addToRepeat();
	}
	
	@FXML protected void handleFileButtonAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File initDir = new File(System.getProperty("user.dir"));
		 
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(initDir);
        
       
        //Show open file dialog
        file = fileChooser.showOpenDialog(WordsTrainer.mainStage);
        if (file == null) return;
        
        try {
        	dict = new XLSXDictionary(file.getAbsolutePath());
        } catch (Exception e) {
        	e.printStackTrace();
        }
        textareaLang[0] = textareaLang0;
        textareaLang[1] = textareaLang1;
        checkboxLang[0] = checkboxLang0;
        checkboxLang[1] = checkboxLang1;
        labelFile.setText(file.getAbsolutePath());
        isTranslate = false;
        dictIterator = new DictionaryIterator(dict);
        buttonRepeat.disableProperty().setValue(true);
        buttonNext.disableProperty().setValue(false);
        buttonRestart.disableProperty().setValue(true);
        hboxLang.setDisable(false);
        //checkboxRepeat.disableProperty().setValue(true);
        changeSystemState();
        setBindings();
	}
	
	@FXML protected void handleRestartButtonAction(ActionEvent event) {
		isTranslate = false;
		dictIterator.initIndex();
		dictIterator.clearCurWord();
		buttonRepeat.disableProperty().setValue(true);
	}
		
	@FXML protected void handleNextButtonAction(ActionEvent event) {
		if (!isTranslate) {
			dictIterator.nextWord();
			hboxLang.setDisable(true);
			isTranslate = true;
			//System.out.print(dictIterator.curWordProperty(0).get()+" - ");
		}
		else {
			dictIterator.translateCurWord();
			hboxLang.setDisable(false);
			isTranslate = false;
			//System.out.println(dictIterator.curWordProperty(1).get());
		}
		buttonRestart.disableProperty().setValue(false);
		//System.out.println(isCheckBoxRndDisable.get());
	}
	
	@FXML protected void handleLang0CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected())
			checkboxLang1.setSelected(true);
		changeSystemState();
	}
	
	@FXML protected void handleRepeatCheckBoxAction(ActionEvent event) {
		//dict.setToRepeat(dictIterator.getCurLang(), dictIterator.getCurWordCnt(), checkboxRepeat.isSelected());
		
	}
		
	@FXML protected void handleLang1CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected()) 
			checkboxLang0.setSelected(true);
		changeSystemState();
	}
	
	static protected void handleStageCloseRequest(WindowEvent we) {
		try { 
			if (dict != null) {
				dict.save();
				dict.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeSystemState() {
		//System.out.println("здесь");
		buttonNext.disableProperty().unbind();
		if (checkboxLang0.isSelected() && checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(-1);
			buttonNext.disableProperty().bind(dictIterator.idxEmptyTotalProperty());
		}
		if (!checkboxLang0.isSelected()) {
			dictIterator.setActiveLangs(1);
			buttonNext.disableProperty().bind(dictIterator.idxEmptyProperty(1));
		}
		if (!checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(0);
			buttonNext.disableProperty().bind(dictIterator.idxEmptyProperty(0));
		}
	}
	
	private void setBindings() {
		//System.out.println("set bind");
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
		
		labelIdxWordsCntLang0.textProperty().bind(dictIterator.idxWordsCounterProperty(0).asString());
		labelIdxWordsCntLang1.textProperty().bind(dictIterator.idxWordsCounterProperty(1).asString());
		labelIdxWordsNumber.textProperty().bind(dictIterator.idxWordsNumberProperty().asString());
		
		//System.out.println("111");
		checkboxRepeat.selectedProperty().bindBidirectional(dictIterator.toRepeat);
		
		dictIterator.showExampleProperty().bind(checkboxExample.selectedProperty());
	}
}
