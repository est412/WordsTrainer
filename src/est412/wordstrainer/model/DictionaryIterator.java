package est412.wordstrainer.model;

public class DictionaryIterator {
	private Dictionary dict;
	private int[][] wordsIndex;
	private int[][][] wI;
	private int wordsNum;
	private int[] curWordCnt = new int[2];
	private int[] repeatWordCnt = new int[2]; 
	private int curLang; // 0 = foreing, 1 = russian
	private int activeLangs; // -1 = both; 0 = foreing, 1 = russian
	private boolean rndLang = false;
	private int curWordsIdx;
	
	public DictionaryIterator(Dictionary dict) {
		this.dict = dict;
		wordsNum = dict.getWordsNum();
		wI = new int[2][2][wordsNum];
		curWordsIdx = 0;
		wordsIndex = wI[curWordsIdx];
		randomize();
	}
	
	public int getCurWordCnt() {
		return curWordCnt[curLang];
	}
	
	public String getCurWord() {
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
		return isLastWord(++curWordCnt[lang]);
	}
	
	public boolean isLastWord() {
		if (activeLangs == -1) return (isLastWord(0) && isLastWord(1));
		else return isLastWord(curLang);
	}
	
	public boolean isLastWord(int lang) {
		if (curWordCnt[lang] == wordsNum-1 || wordsIndex[lang][curWordCnt[lang]+1] == -1 ) return true;
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
		if (rndLang) 
			do {
				curLang = (int) (Math.random() * 2); 
			} while (isLastWord(curLang));
		else {
			//System.out.println("до"+curLang+" : "+curWordCnt[curLang]);
			curLang = (curLang == 0 ? 1 : 0);
			if (isLastWord(curLang)) curLang = (curLang == 0 ? 1 : 0);
			//System.out.println("после"+curLang+" : "+curWordCnt[curLang]);
		}
		return isLastWord();
	}
	
	public void switchWordsIndex() {
		for (int i = 0; i < wordsNum; i++) 
			wordsIndex[0][i] = wordsIndex[1][i] = -1;
		wordsIndex = wI[(curWordsIdx == 0 ? 1 : 0)];
	}
	
	public void setRndLang(boolean rndLang) {
		this.rndLang = rndLang;
	}
	
	public int getWordsIdxTotal() {
		if (activeLangs == -1) return wordsNum*2; 
		else return wordsNum;
	}
	
	public int getWordsIdxCnt() {
		if (activeLangs == -1) return (curWordCnt[0]+1) + (curWordCnt[1]+1); 
		else return curWordCnt[curLang]+1;
	}
	
	public void addToRepeat() {
		wI[curWordsIdx == 0 ? 1 : 0][curLang][repeatWordCnt[curLang]] = curWordCnt[curLang];  
	};
	
	public int getRepeatWordsTotal() {
		if (activeLangs == -1) return (repeatWordCnt[0]+1) + (repeatWordCnt[1]+1); 
		else return repeatWordCnt[curLang]+1;
	}
	
	public void randomize() {
		int rnd;
		curWordCnt[0] = curWordCnt[1] = -1;
		repeatWordCnt[0] = repeatWordCnt[1] = -1;
		for (int i = 0; i < wordsNum; i++) 
			wI[0][0][i] = wI[0][1][i] = wI[1][0][i] = wI[1][1][i] = -1;
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
