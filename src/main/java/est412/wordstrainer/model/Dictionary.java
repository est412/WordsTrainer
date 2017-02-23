package est412.wordstrainer.model;

public interface Dictionary {
	void open(String fileName) throws Exception;
	void save() throws Exception;
	void close() throws Exception;
	int getWordsNumber();
	String getWord(int lang, int count);
	boolean isToRepeat(int lang, int count);
	void setToRepeat(int lang, int count, boolean is);
	String getExample(int lang, int count);
}