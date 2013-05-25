package est412.wordstrainer.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
	//private boolean rndLang = false;
	
	private List<ObservableList<Integer>> wordsIndex = new ArrayList<ObservableList<Integer>>();
	
	private int curWord;
	public BooleanProperty[] isEmptyIndex = new SimpleBooleanProperty[2];
	public BooleanProperty isRndLang = new SimpleBooleanProperty(false);
	public StringProperty[] word = new SimpleStringProperty[2];
	public IntegerProperty wordsIdxTotal = new SimpleIntegerProperty();
	public IntegerProperty wordsIdxCnt = new SimpleIntegerProperty();
	
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		wordsNum = dict.getWordsNumber();
		isEmptyIndex[0] = new SimpleBooleanProperty();
		isEmptyIndex[1] = new SimpleBooleanProperty();
		word[0] = new SimpleStringProperty();
		word[1] = new SimpleStringProperty();
		initIndex();
	}
	
	public void clearWord() {
		word[0].set("");
		word[1].set("");
	}
	
	private void nextLang() {
		if (activeLangs == -1) switchCurLang();
		else setCurLang(activeLangs);
	}
	
	public void nextWord() {
		clearWord();
		nextLang();
		curWord = (int) (Math.random() * wordsIndex.get(curLang).size());
		String str = dict.getWord(curLang, curWord);
		word[curLang].setValue(str);
	}
	
	public void translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		String str = dict.getWord(lang, curWord);
		word[lang].setValue(str);
		wordsIndex.get(curLang).remove(curWord);
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
		if (isRndLang.get()) 
			do {
				curLang = (int) (Math.random() * 2); 
			} while (isLastWord(curLang));
		else {
			curLang = (curLang == 0 ? 1 : 0);
			if (isLastWord(curLang)) curLang = (curLang == 0 ? 1 : 0);
		}
		return isLastWord();
	}
	
	private void calcWordsIdxTotal() {
		if (activeLangs == -1) wordsIdxTotal.setValue(wordsNum*2); 
		else wordsIdxTotal.setValue(wordsNum);
	}
	
	private void calcWordsIdxCnt() {
		int cnt;
		if (activeLangs == -1) cnt = wordsNum*2 - wordsIndex.get(0).size() - wordsIndex.get(1).size();  
		else cnt = wordsNum - wordsIndex.get(curLang).size();
		wordsIdxCnt.setValue(cnt);
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
		wordsIndex.get(0).addListener(new ListChangeListener<Integer>() {
	    	@Override
	    	public void onChanged(ListChangeListener.Change change) {
	    		isEmptyIndex[0].set(wordsIndex.get(0).isEmpty());
	    		calcWordsIdxTotal();
	    		calcWordsIdxCnt();
	    	} // onChanged()
	    }); // addListener(
	    wordsIndex.get(1).addListener(new ListChangeListener<Integer>() {
	    	@Override
	    	public void onChanged(ListChangeListener.Change change) {
	    		isEmptyIndex[1].set(wordsIndex.get(1).isEmpty());
	    		calcWordsIdxTotal();
	    		calcWordsIdxCnt();
	    	} // onChanged()
	    }); // addListener(
		for (int i = 0; i < dict.getWordsNumber(); i++) {
			wordsIndex.get(0).add(new Integer(i));
			wordsIndex.get(1).add(new Integer(i));
		} // for
	} // initIndex()
} // class
