package est412.wordstrainer;

import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import est412.wordstrainer.model.Dictionary;
import est412.wordstrainer.model.XLSXDictionary;
import est412.wordstrainer.model.DictionaryIterator;

public class WordsTrainerController {
	@FXML private Label labelLang0;
	@FXML private Label labelLang1;
	@FXML private Label labelFile;
	private Label[] labelLang = new Label[2];
	@FXML private CheckBox checkboxRepeat;
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
	
	static private Dictionary dict;
	private DictionaryIterator dictIterator;
	private boolean isTranslate;
	private File file;
	
	BooleanBinding isCheckBoxRndDisable;
		
	public WordsTrainerController() {
		//System.out.println("конструктор");
	}
	
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
        labelLang[0] = labelLang0;
        labelLang[1] = labelLang1;
        checkboxLang[0] = checkboxLang0;
        checkboxLang[1] = checkboxLang1;
        labelFile.setText(file.getAbsolutePath());
        isTranslate = false;
        dictIterator = new DictionaryIterator(dict);
        buttonRepeat.disableProperty().setValue(true);
        buttonNext.disableProperty().setValue(false);
        buttonRestart.disableProperty().setValue(true);
        hboxLang.setDisable(false);
        //labelWordsIdxTotal.setText(new Integer(dict.getWordsNumber()).toString());
        //labelWordsIdxCnt.setText("<Слово>");
        checkboxRepeat.disableProperty().setValue(true);
        //changeSystemState();
        setBindings();
        //System.out.println(checkboxLang0 + " " + checkboxLang1 + " " + dictIterator.isLastWord[0] + " " + dictIterator.isLastWord[1]);
		//checkboxLang0.disableProperty().bind(dictIterator.isLastWord[0]);
		//checkboxLang1.disableProperty().bind(dictIterator.isLastWord[1]);

	}
	
	@FXML protected void handleRestartButtonAction(ActionEvent event) {
		isTranslate = false;
		dictIterator.initIndex();
		//buttonNext.disableProperty().setValue(false); ???
		dictIterator.clearCurWord();
		//labelLang0.setText("<Иностранный>");
		//labelLang1.setText("<Русский>");
		//labelWordsIdxCnt.setText("<Слово>");
		buttonRepeat.disableProperty().setValue(true);
	}
		
	@FXML protected void handleNextButtonAction(ActionEvent event) {
		//System.out.println("handleNextButtonAction "+isTranslate);
		if (!isTranslate) {
			dictIterator.nextWord();
			//dictIterator.word[dictIterator.getCurLang() == 0 ? 1 : 0].set("");
			//labelLang[dictIterator.getCurLang()].setText(str);
			hboxLang.setDisable(true);
			isTranslate = true;
		}
		else {
			dictIterator.translateCurWord();
			//labelLang[dictIterator.getCurLang() == 0 ? 1 : 0].setText(str);
			//buttonNext.disableProperty().setValue(dictIterator.isLastWord());
			hboxLang.setDisable(false);
			isTranslate = false;
		}
		buttonRestart.disableProperty().setValue(false);
		//labelWordsIdxCnt.setText(new Integer(dictIterator.calcWordsIdxCnt()).toString());
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
	
	@FXML protected void handleRndLangCheckBoxAction(ActionEvent event) {
		//dictIterator.setRndLang(checkboxRndLang.isSelected());
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
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(-1));
			//checkboxRndLang.setDisable(false);
		}
		if (!checkboxLang0.isSelected()) {
			dictIterator.setActiveLangs(1);
			buttonNext.disableProperty().bind(dictIterator.idxEmptyProperty(1));
		}
		if (!checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(0);
			buttonNext.disableProperty().bind(dictIterator.idxEmptyProperty(0));
		}
		//labelWordsIdxTotal.setText(new Integer(dictIterator.getWordsIdxTotal()).toString());
		//labelWordsIdxCnt.setText(new Integer(dictIterator.calcWordsIdxCnt()).toString());
	}
	
	private void setBindings() {
		//System.out.println("set bind");
		checkboxLang0.disableProperty().bind(dictIterator.idxEmptyProperty(0));
		checkboxLang1.disableProperty().bind(dictIterator.idxEmptyProperty(1));
		
		isCheckBoxRndDisable = Bindings.or(checkboxLang0.selectedProperty().not(), checkboxLang1.selectedProperty().not());
		checkboxRndLang.disableProperty().bind(isCheckBoxRndDisable);
		
		dictIterator.langRndProperty().bind(checkboxRndLang.selectedProperty());
		
		labelLang0.textProperty().bind(dictIterator.curWordProperty(0));
		labelLang1.textProperty().bind(dictIterator.curWordProperty(1));
		
		labelIdxWordsCntLang0.textProperty().bind(dictIterator.idxWordsCounterProperty(0).asString());
		labelIdxWordsCntLang1.textProperty().bind(dictIterator.idxWordsCounterProperty(1).asString());
		labelIdxWordsNumber.textProperty().bind(dictIterator.idxWordsNumberProperty().asString());
	}
}
