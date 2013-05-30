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
	private int activeLangs; // -1 = both; 0 = foreing, 1 = russian
	
	private List<ObservableList<Integer>> wordsIndex = new ArrayList<ObservableList<Integer>>();
	
	private int curWordPos;
	private BooleanProperty[] idxEmpty = new SimpleBooleanProperty[2];
	private BooleanProperty idxEmptyTotal = new SimpleBooleanProperty();
	private BooleanProperty langRnd = new SimpleBooleanProperty(false);
	private StringProperty[] curWord = new SimpleStringProperty[2];
	private IntegerProperty idxWordsNumber = new SimpleIntegerProperty();
	private IntegerProperty idxWordsCounter[] = new SimpleIntegerProperty[2];
	
	public boolean isIdxEmpty(int lang) {
		return idxEmpty[lang].get();
	}
	
	protected void setIdxEmpty(int lang, boolean empty) {
		idxEmpty[lang].set(empty);
	}

	public BooleanProperty idxEmptyProperty(int lang) {
		return idxEmpty[lang];
	}
	
	public boolean isLangRnd() {
		return langRnd.get();
	}
	
	/*
	public boolean isIdxEmptyTotal() {
		return idxEmpty[0].get() && idxEmpty[1].get();
	}
	
	protected void setIdxEmptyTotal(boolean empty) {
		idxEmpty[0].set(empty);
		idxEmpty[1].set(empty);
	}*/

	public BooleanProperty idxEmptyTotalProperty() {
		return idxEmptyTotal;
	}
	
	
	protected void setLangRnd(boolean rnd) {
		langRnd.set(rnd);
	}
	
	public BooleanProperty langRndProperty() {
		return langRnd;
	}
	
	public String getCurWord(int lang) {
		return curWord[lang].get();
	}
	
	protected void setCurWord(int lang, String word) {
		curWord[lang].set(word);
	}
	
	public StringProperty curWordProperty(int lang) {
		return curWord[lang];
	}
	
	public int getIdxWordsNumber() {
		return idxWordsNumber.get();
	}
	
	protected void setIdxWordsNumber(int number) {
		idxWordsNumber.set(number);
	}
	
	public IntegerProperty idxWordsNumberProperty() {
		return idxWordsNumber;
	}
		
	public int getIdxWordsCounder(int lang) {
		return idxWordsCounter[lang].get();
	}
	
	protected void setIdxWordsCounter(int lang, int cnt) {
		idxWordsCounter[lang].set(cnt);
	}
	
	public IntegerProperty idxWordsCounterProperty(int lang) {
		return idxWordsCounter[lang];
	}
	
	
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		idxEmpty[0] = new SimpleBooleanProperty();
		idxEmpty[1] = new SimpleBooleanProperty();
		curWord[0] = new SimpleStringProperty();
		curWord[1] = new SimpleStringProperty();
		idxWordsCounter[0] = new SimpleIntegerProperty();
		idxWordsCounter[1] = new SimpleIntegerProperty();
		idxEmptyTotal.bind(Bindings.and(idxEmpty[0], idxEmpty[1]));
		idxWordsNumber.set(dict.getWordsNumber());
		initIndex();
	}
	
	public void clearCurWord() {
		setCurWord(0, "");
		setCurWord(1, "");
	}
	
	private void nextLang() {
		if (activeLangs == -1) switchCurLang();
		else setCurLang(activeLangs);
	}
	
	public void nextWord() {
		clearCurWord();
		nextLang();
		curWordPos = (int) (Math.random() * wordsIndex.get(curLang).size());
		String str = dict.getWord(curLang, wordsIndex.get(curLang).get(curWordPos));
		setCurWord(curLang, str);
	}
	
	public void translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		String str = dict.getWord(lang, wordsIndex.get(curLang).get(curWordPos));
		setCurWord(lang, str);
		wordsIndex.get(curLang).remove(curWordPos);
	}
	
	public void setActiveLangs(int langs) {
		activeLangs = langs;
	}
	
	public void setCurLang(int lang) {
		curLang = lang;
	}
	
	public int getCurLang() {
		return curLang;
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
	
	private void calcIdxWordsCounter() {
		int wordsNum = dict.getWordsNumber();
		idxWordsCounter[0].set(wordsNum - wordsIndex.get(0).size());
		idxWordsCounter[1].set(wordsNum - wordsIndex.get(1).size());
	}
	
	public void addToRepeat() {
	};
	
	
	public void initIndex() {
		wordsIndex.clear();
		ObservableList<Integer> tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		wordsIndex.get(0).addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
	    		setIdxEmpty(0, wordsIndex.get(0).isEmpty());
	    		calcIdxWordsCounter();
			} // invalidated
		}); // wordsIndex.get(0).addListener
		wordsIndex.get(1).addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				setIdxEmpty(1, wordsIndex.get(1).isEmpty());
	    		calcIdxWordsCounter();
			} // invalidated
		}); // wordsIndex.get(1).addListener
		for (int i = 0; i < dict.getWordsNumber(); i++) {
			wordsIndex.get(0).add(new Integer(i));
			wordsIndex.get(1).add(new Integer(i));
		} // for
	} // initIndex()
} // class
