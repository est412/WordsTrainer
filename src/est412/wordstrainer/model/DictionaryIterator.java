package est412.wordstrainer.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DictionaryIterator {
	private Dictionary dict;
	private int curLang; // 0 = foreing, 1 = russian
	private int activeLangs; // 0 = foreing, 1 = russian; 2 = both
	
	//индекс для хранения двух списков - по языкам
	private List<ObservableList<Integer>> wordsIndex = new ArrayList<ObservableList<Integer>>();
		
	private int curWordIdx;
	private int curWordPos;
	private BooleanProperty[] idxEmpty = new SimpleBooleanProperty[3];
	private BooleanProperty langRnd = new SimpleBooleanProperty(false);
	private StringProperty[] curWord = new SimpleStringProperty[2];
	private StringProperty[] curExample = new SimpleStringProperty[2];
	private IntegerProperty[] idxWordsNumber = new SimpleIntegerProperty[3];
	private IntegerProperty[] idxWordsCounter = new SimpleIntegerProperty[3];
	private BooleanProperty showExample = new SimpleBooleanProperty();
	public BooleanProperty toRepeat = new SimpleBooleanProperty();
	private String[] buffer = new String[4];
	private boolean[] repBuffer = new boolean[2];
	public BooleanProperty repeat = new SimpleBooleanProperty();
	public IntegerProperty mode = new SimpleIntegerProperty();
	public IntegerProperty showNumber = new SimpleIntegerProperty();
	public IntegerProperty showCounter = new SimpleIntegerProperty();
	public BooleanProperty showEmpty = new SimpleBooleanProperty();
	
	//выставляет значения пропертей, т.к. сами свойства списка пропертями не являются
	private InvalidationListener invalListener0 = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
   		idxEmpty[0].set(wordsIndex.get(0).isEmpty());
    		idxWordsCounter[0].set(idxWordsNumber[0].get() - wordsIndex.get(0).size());
		} // invalidated 
	};
	
	//выставляет значения пропертей, т.к. сами свойства списка пропертями не являются
	private InvalidationListener invalListener1 = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
    		idxEmpty[1].set(wordsIndex.get(1).isEmpty());
    		idxWordsCounter[1].set(idxWordsNumber[1].get() - wordsIndex.get(1).size());
 		} // invalidated 
	};
	
	//сохраняет в словарь изменившееся свойство toRepeat
	private InvalidationListener invalListenerToRepeat = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
    		dict.setToRepeat(curLang, curWordPos, toRepeat.get());
 		} // invalidated 
	};
	
	private InvalidationListener invalListenerMode = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
    		initIndex();
 		} // invalidated 
	};
	
	public boolean isIdxEmpty(int lang) {
		return idxEmpty[lang].get();
	}
	
	public BooleanProperty idxEmptyProperty(int lang) {
		return idxEmpty[lang];
	}
	
	public boolean isLangRnd() {
		return langRnd.get();
	}
	
	public BooleanProperty langRndProperty() {
		return langRnd;
	}
	
	public String getCurWord(int lang) {
		return curWord[lang].get();
	}
	
	public StringProperty curWordProperty(int lang) {
		return curWord[lang];
	}
	
	public String getCurExample(int lang) {
		return curExample[lang].get();
	}
	
	public StringProperty curExampleProperty(int lang) {
		return curExample[lang];
	}
	
	public int getIdxWordsNumber(int lang) {
		return idxWordsNumber[lang].get();
	}
	
	public IntegerProperty idxWordsNumberProperty(int lang) {
		return idxWordsNumber[lang];
	}
		
	public int getIdxWordsCounder(int lang) {
		return idxWordsCounter[lang].get();
	}
	
	public IntegerProperty idxWordsCounterProperty(int lang) {
		return idxWordsCounter[lang];
	}
	
	public BooleanProperty showExampleProperty() {
		return showExample;
	}
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		idxEmpty[0] = new SimpleBooleanProperty();
		idxEmpty[1] = new SimpleBooleanProperty();
		idxEmpty[2] = new SimpleBooleanProperty();
		curWord[0] = new SimpleStringProperty();
		curWord[1] = new SimpleStringProperty();
		curExample[0] = new SimpleStringProperty();
		curExample[1] = new SimpleStringProperty();
		idxWordsCounter[0] = new SimpleIntegerProperty();
		idxWordsCounter[1] = new SimpleIntegerProperty();
		idxWordsCounter[2] = new SimpleIntegerProperty();
		idxWordsNumber[0] = new SimpleIntegerProperty();
		idxWordsNumber[1] = new SimpleIntegerProperty();
		idxWordsNumber[2] = new SimpleIntegerProperty();
		idxWordsNumber[0].set(dict.getWordsNumber());
		initIndex();
		idxEmpty[2].bind(Bindings.and(idxEmpty[0], idxEmpty[1]));
		idxWordsCounter[2].bind(Bindings.add(idxWordsCounter[0], idxWordsCounter[1]));
		idxWordsNumber[2].bind(Bindings.add(idxWordsNumber[0], idxWordsNumber[1]));
		mode.addListener(invalListenerMode);
	}
	
	public void initIndex() {
		clearShowBindings();
		wordsIndex.clear();
		ObservableList<Integer> tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		
		wordsIndex.get(0).addListener(invalListener0);
		wordsIndex.get(1).addListener(invalListener1);
		toRepeat.addListener(invalListenerToRepeat);
		
		System.out.println("initIndex " + mode.get());
		if (mode.get() == 0) {
			for (int i = 0; i < dict.getWordsNumber(); i++) {
				wordsIndex.get(0).add(new Integer(i));
				wordsIndex.get(1).add(new Integer(i));
			}
		} else {
			for (int i = 0; i < dict.getWordsNumber(); i++) {
				if (dict.isToRepeat(0, i)) wordsIndex.get(0).add(new Integer(i));
				if (dict.isToRepeat(1, i)) wordsIndex.get(1).add(new Integer(i));
			}
		}
		idxWordsNumber[0].set(wordsIndex.get(0).size());
		idxWordsNumber[1].set(wordsIndex.get(1).size());
		idxWordsCounter[0].set(0);
		idxWordsCounter[1].set(0);
		setShowBindings(curLang);
	} // initIndex()

	
	public void clearCurWord() {
		curWord[0].set("");
		curWord[1].set("");
		hideExamples();
	}
	
	public void hideExamples() {
		curExample[0].set("");
		curExample[1].set("");
	}
	
	private void nextLang() {
		if (activeLangs == 2) switchCurLang();
		else setCurLang(activeLangs);
	}
	
	public void nextWord() {
		clearCurWord();
		nextLang();
		curWordIdx = (int) (Math.random() * wordsIndex.get(curLang).size());
		curWordPos = wordsIndex.get(curLang).get(curWordIdx);
		buffer[0] = dict.getWord(0, curWordPos);
		buffer[1] = dict.getWord(1, curWordPos);
		buffer[2] = dict.getExample(0, curWordPos);
		buffer[3] = dict.getExample(1, curWordPos);
		repBuffer[0] = dict.isToRepeat(0, curWordPos);
		repBuffer[1] = dict.isToRepeat(1, curWordPos);
		curWord[curLang].set(buffer[curLang]);
		toRepeat.set(repBuffer[curLang]);
		
		wordsIndex.get(curLang).remove(curWordIdx);
	}
	
	public void translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		curWord[lang].set(buffer[lang]);
	}
	
	public void showExample() {
		if (buffer[curLang+2].equals("")) buffer[curLang+2] = "---";
		curExample[curLang].set(buffer[curLang+2]);
	}
	
	public void showTrExample() {
		int lang = (curLang == 0 ? 1 : 0);
		if (buffer[lang+2].equals("")) buffer[lang+2] = "---";
		curExample[lang].set(buffer[lang+2]);
	}
	
	public void setShowBindings(int lang) {
		showCounter.bind(idxWordsCounter[lang]);
		showNumber.bind(idxWordsNumber[lang]);
		showEmpty.bind(idxEmpty[lang]);
	}
	
	public void clearShowBindings() {
		showCounter.unbind();
		showNumber.unbind();
		showEmpty.unbind();
	}
	
	public void setActiveLangs(int langs) {
		activeLangs = langs;
		setShowBindings(langs);
	}
	
	public void setCurLang(int lang) {
		curLang = lang;
		setShowBindings(lang);
	}
	
	public void switchCurLang() {
		if (isLangRnd()) 
			do {
				curLang = (int) (Math.random() * 2); 
			} while (isIdxEmpty(curLang));
		else {
			curLang = (curLang == 0 ? 1 : 0);
			if (isIdxEmpty(curLang)) curLang = (curLang == 0 ? 1 : 0);
		}
	}
} // class
