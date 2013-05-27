package est412.wordstrainer.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
	//private boolean rndLang = false;
	
	private List<ObservableList<Integer>> wordsIndex = new ArrayList<ObservableList<Integer>>();
	
	private int curWordPos;
	private BooleanProperty[] idxEmpty = new SimpleBooleanProperty[2];
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
		String str = dict.getWord(curLang, curWordPos);
		setCurWord(curLang, str);
	}
	
	public void translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		String str = dict.getWord(lang, curWordPos);
		setCurWord(lang, str);
		wordsIndex.get(curLang).remove(curWordPos);
	}
	
	// to delete? or convert to Property
	public boolean isLastWord() {
		if (activeLangs == -1) return (isLastWord(0) && isLastWord(1));
		else return isLastWord(curLang);
	}
	
	// to delete? or convert to Property
	public boolean isLastWord(int lang) {
		return wordsIndex.get(lang).isEmpty();
	}
	
	public boolean setActiveLangs(int langs) {
		activeLangs = langs;
		//System.out.println("DictionaryIterator.setActiveLangs() "+activeLangs);
		if (langs == -1) return (isLastWord(0) && isLastWord(1));
		else return isLastWord(langs);
	}
	
	public boolean setCurLang(int lang) {
		curLang = lang;
		return isLastWord();
	}
	
	public int getCurLang() {
		return curLang;
	}
	
	public boolean switchCurLang() {
		if (isLangRnd()) 
			do {
				curLang = (int) (Math.random() * 2); 
			} while (isLastWord(curLang));
		else {
			curLang = (curLang == 0 ? 1 : 0);
			if (isLastWord(curLang)) curLang = (curLang == 0 ? 1 : 0);
		}
		return isLastWord();
	}
	
	private void calcIdxWordsCounter() {
		int wordsNum = dict.getWordsNumber();
		idxWordsCounter[0].set(wordsNum - wordsIndex.get(0).size());
		idxWordsCounter[1].set(wordsNum - wordsIndex.get(1).size());
	}
	
	public void addToRepeat() {
		//wI[curWordsIdx == 0 ? 1 : 0][curLang][repeatWordCnt[curLang]] = curWordCnt[curLang];  
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
