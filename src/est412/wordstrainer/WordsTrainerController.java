package est412.wordstrainer;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import org.apache.poi.openxml4j.opc.OPCPackage;

import est412.wordstrainer.model.Dictionary;
import est412.wordstrainer.model.DictionaryIterator;

public class WordsTrainerController {
	@FXML private Label labelLang0;
	@FXML private Label labelLang1;
	@FXML private Label labelFile;
	private Label[] labelLang = new Label[2]; 
	@FXML private CheckBox checkboxLang0;
	@FXML private CheckBox checkboxLang1;
	@FXML private CheckBox checkboxRndLang;
	private CheckBox[] checkboxLang = new CheckBox[2]; 
	@FXML private Label labelCurWordCount;
	@FXML private Label labelDictWordsNum;
	@FXML private Button buttonNext;
	@FXML private Button buttonRestart;
	@FXML private HBox hboxLang;
	
	static private Dictionary dict;
	private DictionaryIterator dictIterator;
	private boolean isTranslate;
	private File file;
	
	@FXML protected void handleStartButtonAction(ActionEvent event) {

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
        	dict = new Dictionary(file.getAbsolutePath());
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
        buttonNext.disableProperty().setValue(false);
        buttonRestart.disableProperty().setValue(true);
        hboxLang.setDisable(false);
        labelDictWordsNum.setText(new Integer(dict.getWordsNum()).toString());
        labelCurWordCount.setText("<Слово>");
        changeSystemState();		
	}
	
	@FXML protected void handleRestartButtonAction(ActionEvent event) {
		isTranslate = false;
		dictIterator.randomize();
		buttonNext.disableProperty().setValue(false);
		labelLang0.setText("<Иностранный>");
		labelLang1.setText("<Русский>");
		labelCurWordCount.setText("<Слово>");
	}
		
	@FXML protected void handleNextButtonAction(ActionEvent event) {
		String str;
		if (!isTranslate) {
			dictIterator.nextWord();
			str = dictIterator.getCurWord();
			labelLang[dictIterator.getCurLang() == 0 ? 1 : 0].setText("");
			labelLang[dictIterator.getCurLang()].setText(str);
			hboxLang.setDisable(true);
		}
		else {
			str = dictIterator.translateCurWord();
			labelLang[dictIterator.getCurLang() == 0 ? 1 : 0].setText(str);
			buttonNext.disableProperty().setValue(dictIterator.isLastWord());
			hboxLang.setDisable(false);
		}
		buttonRestart.disableProperty().setValue(false);
		labelCurWordCount.setText(new Integer(dictIterator.getCurWordCnt()+1).toString());
		isTranslate = !isTranslate;
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
	
	@FXML protected void handleRndLangCheckBoxAction(ActionEvent event) {
		dictIterator.setRndLang(checkboxRndLang.isSelected());
	}
	
	static protected void handleStageCloseRequest(WindowEvent we) {
		try { 
			if (dict != null) {
				OPCPackage pkg = dict.getPkg();
				if (pkg != null) pkg.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void changeSystemState() {
		if (checkboxLang0.isSelected() && checkboxLang1.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(-1));
			checkboxRndLang.setDisable(false);
		}
		if (!checkboxLang0.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(1));
			checkboxRndLang.setDisable(true);
		}
		if (!checkboxLang1.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(0));
			checkboxRndLang.setDisable(true);
		}
	}
}
