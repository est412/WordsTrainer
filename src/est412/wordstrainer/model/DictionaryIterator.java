package est412.wordstrainer.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DictionaryIterator {
	private Dictionary dict;
	private int wordsNum;
	private int curLang; // 0 = foreing, 1 = russian
	private int activeLangs; // -1 = both; 0 = foreing, 1 = russian
	private boolean rndLang = false;
	
	private List<ObservableList<Integer>> wordsIndex = new ArrayList<ObservableList<Integer>>();
	
	private int curWord;
	public BooleanProperty[] isEmptyIndex = new SimpleBooleanProperty[2];
	public BooleanProperty isLast;
	
	public StringProperty[] word = new SimpleStringProperty[2];
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		wordsNum = dict.getWordsNumber();
		isEmptyIndex[0] = new SimpleBooleanProperty();
		isEmptyIndex[1] = new SimpleBooleanProperty();
		word[0] = new SimpleStringProperty();
		word[1] = new SimpleStringProperty();
		initIndex();
	}
	
	// to delete?
	public int getCurWordCnt() {
		return curWord;
	}
	
	public String getCurWord() {
		curWord = (int) (Math.random() * wordsIndex.get(0).size());
		String str = dict.getWord(curLang, curWord);
		word[curLang].setValue(str);
		return str;
	}
	
	public String translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		wordsIndex.get(curLang).remove(curWord);
		String str = dict.getWord(lang, curWord);
		word[lang].setValue(str);
		return str;
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
		System.out.println("DictionaryIterator.setActiveLangs() "+activeLangs);
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
		if (rndLang) 
			do {
				curLang = (int) (Math.random() * 2); 
			} while (isLastWord(curLang));
		else {
			curLang = (curLang == 0 ? 1 : 0);
			if (isLastWord(curLang)) curLang = (curLang == 0 ? 1 : 0);
		}
		return isLastWord();
	}
	
	public void setRndLang(boolean rndLang) {
		this.rndLang = rndLang;
	}
	
	public int getWordsIdxTotal() {
		if (activeLangs == -1) return wordsNum*2; 
		else return wordsNum;
	}
	
	public int getWordsIdxCnt() {
		if (activeLangs == -1) 
			return getWordsIdxTotal() - wordsIndex.get(0).size() - wordsIndex.get(1).size();
		else return getWordsIdxTotal() - wordsIndex.get(curLang).size();
	}
	
	public void addToRepeat() {
		//wI[curWordsIdx == 0 ? 1 : 0][curLang][repeatWordCnt[curLang]] = curWordCnt[curLang];  
	};
	
	
	public void initIndex() {
		//System.out.println("initIndex");
		ObservableList<Integer> tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		tmp = FXCollections.observableArrayList();
		wordsIndex.add(tmp);
		for (int i = 0; i < dict.getWordsNumber(); i++) {
			wordsIndex.get(0).add(new Integer(i));
			wordsIndex.get(1).add(new Integer(i));
		} // for
	    //System.out.println("1" + isEmptyIndex[0] + " " + isEmptyIndex[1]);
		wordsIndex.get(0).addListener(new ListChangeListener() {
	    	@Override
	    	public void onChanged(ListChangeListener.Change change) {
	    		isEmptyIndex[0].set(wordsIndex.get(0).isEmpty());
	    	} // onChanged()
	    }); // addListener(
	    wordsIndex.get(1).addListener(new ListChangeListener() {
	    	@Override
	    	public void onChanged(ListChangeListener.Change change) {
	    		isEmptyIndex[1].set(wordsIndex.get(1).isEmpty());
	    	} // onChanged()
	    }); // addListener(
	} // initIndex()
} // class
