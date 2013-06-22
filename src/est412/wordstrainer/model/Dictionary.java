package est412.wordstrainer.model;

import java.io.IOException;

public interface Dictionary {

	public abstract void open(String fileName) throws Exception;

	public abstract void save() throws Exception;

	public abstract void close() throws Exception;

	public abstract int getWordsNumber();

	public abstract String getWord(int lang, int count);

	public abstract boolean isToRepeat(int lang, int count);

	public abstract void setToRepeat(int lang, int count, boolean is);
	
	public abstract String getExample(int lang, int count);

}