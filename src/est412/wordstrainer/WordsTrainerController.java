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
	@FXML private Label labelWordsIdxCnt;
	@FXML private Label labelWordsIdxTotal;
	@FXML private Button buttonRepeat;
	@FXML private Button buttonNext;
	@FXML private Button buttonRestart;
	@FXML private HBox hboxLang;
	
	static private Dictionary dict;
	private DictionaryIterator dictIterator;
	private boolean isTranslate;
	private File file;
	
	public WordsTrainerController() {
		//System.out.println("�����������");
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
        labelWordsIdxTotal.setText(new Integer(dict.getWordsNumber()).toString());
        labelWordsIdxCnt.setText("<�����>");
        checkboxRepeat.disableProperty().setValue(true);
        //changeSystemState();
        setBindings();
        //System.out.println(checkboxLang0 + " " + checkboxLang1 + " " + dictIterator.isLastWord[0] + " " + dictIterator.isLastWord[1]);
		//checkboxLang0.disableProperty().bind(dictIterator.isLastWord[0]);
		//checkboxLang1.disableProperty().bind(dictIterator.isLastWord[1]);

	}
	
	@FXML protected void handleRestartButtonAction(ActionEvent event) {
		isTranslate = false;
		//dictIterator.randomize();
		dictIterator.initIndex();
		buttonNext.disableProperty().setValue(false);
		labelLang0.setText("<�����������>");
		labelLang1.setText("<�������>");
		labelWordsIdxCnt.setText("<�����>");
		buttonRepeat.disableProperty().setValue(true);
	}
		
	@FXML protected void handleNextButtonAction(ActionEvent event) {
		String str;
		System.out.println("handleNextButtonAction "+isTranslate);
		if (!isTranslate) {
			//dictIterator.nextWord();
			str = dictIterator.getCurWord();
			labelLang[dictIterator.getCurLang() == 0 ? 1 : 0].setText("");
			//labelLang[dictIterator.getCurLang()].setText(str);
			hboxLang.setDisable(true);
			buttonRepeat.disableProperty().setValue(true);
			checkboxRepeat.disableProperty().setValue(false);
			checkboxRepeat.selectedProperty()
				.setValue(dict.isToRepeat(dictIterator.getCurLang(), dictIterator.getCurWordCnt()));
			isTranslate = true;
		}
		else {
			str = dictIterator.translateCurWord();
			//labelLang[dictIterator.getCurLang() == 0 ? 1 : 0].setText(str);
			buttonNext.disableProperty().setValue(dictIterator.isLastWord());
			hboxLang.setDisable(false);
			buttonRepeat.disableProperty().setValue(false);
			isTranslate = false;
		}
		buttonRestart.disableProperty().setValue(false);
		labelWordsIdxCnt.setText(new Integer(dictIterator.getWordsIdxCnt()).toString());
	}
	
	@FXML protected void handleLang0CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected())
			checkboxLang1.setSelected(true);
		changeSystemState();
	}
	
	@FXML protected void handleRepeatCheckBoxAction(ActionEvent event) {
		dict.setToRepeat(dictIterator.getCurLang(), dictIterator.getCurWordCnt(), checkboxRepeat.isSelected());
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
				dict.save();
				dict.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeSystemState() {
		if (checkboxLang0.isSelected() && checkboxLang1.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(-1));
			checkboxRndLang.setDisable(false);
		}
		if (!checkboxLang0.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(1));
		}
		if (!checkboxLang1.isSelected()) {
			buttonNext.disableProperty().setValue(dictIterator.setActiveLangs(0));
		}
		labelWordsIdxTotal.setText(new Integer(dictIterator.getWordsIdxTotal()).toString());
		labelWordsIdxCnt.setText(new Integer(dictIterator.getWordsIdxCnt()).toString());
	}
	
	private void setBindings() {
		//System.out.println("set bind");
		checkboxLang0.disableProperty().bind(dictIterator.isEmptyIndex[0]);
		checkboxLang1.disableProperty().bind(dictIterator.isEmptyIndex[1]);
		labelLang0.textProperty().bindBidirectional(dictIterator.word[0]);
		labelLang1.textProperty().bindBidirectional(dictIterator.word[1]);
		
	}
}
