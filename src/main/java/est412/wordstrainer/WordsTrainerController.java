package est412.wordstrainer;

import java.io.File;
import java.io.IOException;

import est412.wordstrainer.model.XLSXPoiDictionary;
import est412.wordstrainer.utils.DefaultSettings;
import est412.wordstrainer.utils.PropertiesFileSettings;
import est412.wordstrainer.utils.Settings;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import est412.wordstrainer.model.Dictionary;
import est412.wordstrainer.model.DictionaryIterator;

public class WordsTrainerController {
	@FXML private TextArea textareaLang0;
	@FXML private TextArea textareaLang1;
	@FXML private Label labelFile;
	@FXML private CheckBox checkboxToRepeat;
	@FXML private CheckBox checkboxExample;
	@FXML private CheckBox checkboxLang0;
	@FXML private CheckBox checkboxLang1;
	@FXML private CheckBox checkboxRndLang;
	@FXML private Label labelIdxWordsCntLang0;
	@FXML private Label labelIdxWordsCntLang1;
	@FXML private Label labelIdxWordsNumber;
	@FXML private Button buttonNext;
	@FXML private Button buttonRestart;
	@FXML private Button fileButton;
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
	private TextInputControl contextMenuParentControl;
	private boolean isControlDown;

	private Settings settings;
	
	BooleanBinding isCheckBoxRndEnable;
	
	public WordsTrainerController() {
		dictIterator = new DictionaryIterator();
		try {
			settings = PropertiesFileSettings.getInstance();
		} catch (IOException e) {
			// TODO выдать предупреждение
			e.printStackTrace();
		}
	}
	
	@FXML
	protected void initialize() {
		choiceboxMode.setItems(FXCollections.observableArrayList("Все слова", "Повторение"));
		choiceboxMode.getSelectionModel().selectFirst();
	}
	
	public void initStage(Stage stage) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle("-fx-max-height: 300");
		for(String font : Font.getFamilies()) {
			contextMenu.getItems().add(new MenuItem(font));
		}
		contextMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MenuItem menuItem = (MenuItem) event.getTarget();
				contextMenuParentControl.setStyle("-fx-font-family: " + menuItem.getText()+";"+"-fx-font-size: "+ contextMenuParentControl.getFont().getSize());
				settings.setSetting("font_"+ contextMenuParentControl.getId(), menuItem.getText());
			}
		});
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
				settings.setSetting(Settings.WIDTH, ""+mainStage.getWidth());
				settings.setSetting(Settings.HEIGHT, ""+mainStage.getHeight());
			}
		});
		mainStage.setOnShown(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				TextInputControl textInputControl = (TextInputControl) mainStage.getScene().lookup("#lang0");
				textInputControl.setContextMenu(contextMenu);
				setFontStyle(textInputControl);
				textInputControl = (TextInputControl) mainStage.getScene().lookup("#lang1");
				textInputControl.setContextMenu(contextMenu);
				setFontStyle(textInputControl);
				textInputControl = (TextInputControl) mainStage.getScene().lookup("#lang0Example");
				textInputControl.setContextMenu(contextMenu);
				setFontStyle(textInputControl);
				textInputControl = (TextInputControl) mainStage.getScene().lookup("#lang1Example");
				textInputControl.setContextMenu(contextMenu);
				setFontStyle(textInputControl);
				fileButton.requestFocus();
			}
		});
		String setting = settings.getSetting(Settings.WIDTH);
		if (setting != null) {
			mainStage.setWidth(Double.parseDouble(setting));
		}
		setting = settings.getSetting(Settings.HEIGHT);
		if (setting != null) {
			mainStage.setHeight(Double.parseDouble(setting));
		}
	}

	private void setFontStyle(TextInputControl textInputControl) {
		String size = settings.getSetting("font_size_"+textInputControl.getId());
		String family = settings.getSetting("font_"+textInputControl.getId());
		String style = null;
		if (size != null) {
			style = "-fx-font-size: "+size+";";
		}
		if (family != null) {
			style += "-fx-font-family: "+family;
		}
		if (style != null) {
			textInputControl.setStyle(style);
		}
	}

	@FXML
	protected void handleContextMenu(ContextMenuEvent event) {
		contextMenuParentControl = (TextInputControl) event.getSource();
	}

	@FXML
	protected void handleFileButtonAction(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
		File initDir = new File(settings.getSetting(Settings.DICTIONARIES_DIRECTORY));
		if (!initDir.exists()) {
			initDir = new File(DefaultSettings.getSetting(Settings.DICTIONARIES_DIRECTORY));
		}
        fileChooser.setInitialDirectory(initDir);
		fileChooser.setInitialFileName(settings.getSetting(Settings.LAST_DICTIONARY));
       
        //Show open file dialog
        file = fileChooser.showOpenDialog(mainStage);
        if (file == null) return;

        try {
			if (dict != null) {
				dict.close();
			}
			dict = new XLSXPoiDictionary(file.getAbsolutePath());
        } catch (Exception e) {
        	e.printStackTrace();
        }

		settings.setSetting(Settings.DICTIONARIES_DIRECTORY, file.getParent());
		settings.setSetting(Settings.LAST_DICTIONARY, file.getName());

        labelFile.setText(file.getAbsolutePath());
        
        dictIterator.setDictionary(dict);

        buttonRestart.disableProperty().setValue(true);
   
        handleRestartButtonAction(null);

		choiceboxMode.getSelectionModel().selectFirst();
		buttonNext.requestFocus();

  	}
	
	@FXML
	protected void handleRestartButtonAction(ActionEvent event) {
		dictIterator.clearCurWord();
		toShow = 1;
		shown = 0;
        hboxLang.setDisable(false);
        checkboxExample.disableProperty().setValue(false);
		setBindings();
		checkboxToRepeat.setDisable(true);
		choiceboxMode.setDisable(false);
		dictIterator.mode.unbind();
		dictIterator.mode.bind(choiceboxMode.getSelectionModel().selectedIndexProperty());
		if (dictIterator.showNumber.get() == 0) {
			choiceboxMode.getSelectionModel().selectFirst();
		};
	}
		
	@FXML
	protected void handleNextButtonAction(ActionEvent event) {
		choiceboxMode.setDisable(true);
		choiceboxMode.opacityProperty().set(0.9);
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
				buttonNext.disableProperty().bind(dictIterator.showEmpty);
			}
			shown = 3;
		}
		else if (toShow == 4 && checkboxExample.isSelected()) {
			dictIterator.showTrExample();
			toShow = 1;
			shown = 4;
			buttonNext.disableProperty().bind(dictIterator.showEmpty);
		}
		buttonRestart.disableProperty().setValue(false);
		checkboxToRepeat.setDisable(false);
	}
	
	@FXML
	protected void handleExampleCheckBoxAction(ActionEvent event) {
		if (!checkboxExample.isSelected()) {
			dictIterator.hideExamples();
			return;
		}
		if (shown >= 1) dictIterator.showExample();
		if (shown >= 3) dictIterator.showTrExample();
	}
	
	@FXML
	protected void handleLang0CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected())
			checkboxLang1.setSelected(true);
		changeActiveLangs();
	}
	
	@FXML
	protected void handleLang1CheckBoxAction(ActionEvent event) {
		if (!checkboxLang0.isSelected() && !checkboxLang1.isSelected()) 
			checkboxLang0.setSelected(true);
		changeActiveLangs();
	}

	@FXML
	protected void handleScroll(ScrollEvent event) {
		if (!event.isControlDown()) return;
		TextInputControl textInputControl = (TextInputControl) event.getSource();
		int size = (int)textInputControl.getFont().getSize();
		if (event.getTextDeltaY() > 0) {
			textInputControl.setStyle("-fx-font-size: "+(++size));
		} else if (event.getTextDeltaY() < 0) {
			textInputControl.setStyle("-fx-font-size: "+(--size));
		}
		settings.setSetting("font_size_"+textInputControl.getId(), ""+size);
	}

	@FXML
	protected void handleKeyPressed(KeyEvent event) {
		if (!buttonNext.isDisabled()) {
			if ("SPACE".equals(event.getCode().toString()) || "ENTER".equals(event.getCode().toString())) {
				if (!buttonNext.isFocused()) {
					buttonNext.requestFocus();
					buttonNext.fire();
				}
			}
		}
		if (!checkboxToRepeat.isDisabled()) {
			if ("CONTROL".equals(event.getCode().toString())) {
				checkboxToRepeat.selectedProperty().set(!checkboxToRepeat.selectedProperty().get());
			}
		}
	}

	private void changeActiveLangs() {
		if (checkboxLang0.isSelected() && checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(2);
		}
		if (!checkboxLang0.isSelected()) {
			dictIterator.setActiveLangs(1);
		}
		if (!checkboxLang1.isSelected()) {
			dictIterator.setActiveLangs(0);
		}
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
