package est412.wordstrainer.model;

public class DictionaryIterator {
	private Dictionary dict;
	private int[][] wordsIndex;
	private int wordsNum;
	private int[] curWordCnt = new int[2];
	private int curLang; // 0 = foreing, 1 = russian
	private int activeLangs; // -1 = both; 0 = foreing, 1 = russian
	private boolean rndLang = false;
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		wordsNum = dict.getWordsNum();
		wordsIndex = new int[2][wordsNum];
		randomize();
	}
	
	public int getCurWordCnt() {
		return curWordCnt[curLang];
	}
	
	public String getCurWord() {
		System.out.println("curLang="+curLang);
		System.out.println("curWordCnt[curLang]="+curWordCnt[curLang]);
		System.out.println("wordsIndex[curLang][curWordCnt[curLang]]="+wordsIndex[curLang][curWordCnt[curLang]]);
		return dict.getWord(wordsIndex[curLang][curWordCnt[curLang]], curLang);
	}
	
	public String translateCurWord() {
		int lang = (curLang == 0 ? 1 : 0);
		return dict.getWord(wordsIndex[curLang][curWordCnt[curLang]], lang);
	}
	
	public boolean nextWord() {
		if (activeLangs == -1) switchCurLang();
		else setCurLang(activeLangs);
		curWordCnt[curLang]++;
		return isLastWord();
	}
	
	public boolean nextWord(int lang) {
		return (isLastWord(++curWordCnt[lang]));
	}
	
	public boolean isLastWord() {
		if (activeLangs == -1) return (isLastWord(0) && isLastWord(1));
		else return isLastWord(curLang);
	}
	
	public boolean isLastWord(int lang) {
		if (curWordCnt[lang] == wordsNum-1) return true;
		else return false;
	}
	
	public boolean setActiveLangs(int langs) {
		activeLangs = langs;
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
		if (rndLang) curLang = (int) (Math.random() * 2); 
		else curLang = (curLang == 0 ? 1 : 0);
		return isLastWord();
	}
	
	public void setRndLang(boolean rndLang) {
		this.rndLang = rndLang;
	}
	
	public void randomize() {
		int rnd;
		curWordCnt[0] = curWordCnt[1] = -1;
		for (int i = 0; i < wordsNum; i++) 
			wordsIndex[0][i] = wordsIndex[1][i] = -1;
		for (int i = 0; i < wordsNum; i++) {
			do {
				rnd = (int) (Math.random() * wordsNum);
				if (wordsIndex[0][rnd] != -1) continue;
				wordsIndex[0][rnd] = i;
				break;
			} while (true);
		}
		for (int i = 0; i < wordsNum; i++) {
			do {
				rnd = (int) (Math.random() * wordsNum);
				if (wordsIndex[1][rnd] != -1) continue;
				wordsIndex[1][rnd] = i;
				break;
			} while (true);
		}
	}
}
