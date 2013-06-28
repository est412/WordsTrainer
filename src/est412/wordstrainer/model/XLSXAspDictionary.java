package est412.wordstrainer.model;

import com.aspose.cells.Cell;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

public class XLSXAspDictionary implements Dictionary {
	protected int wordsNum; // количество слов в словаре = к-во строк в файле
	private Workbook wb; //таблица 
	private Worksheet ws; //вкладка словаря
	private Worksheet ws1; //вкладка с метками повторения
	String fileName; //имя файла
	
	//словарь существует только в свЯзи с открытым файлом
	public XLSXAspDictionary(String fileName) throws Exception {
		this.fileName = fileName;
		open(fileName);
	}
	
	//понятно
	@Override
	public void open(String fileName) throws Exception {
		LoadOptions loadOptions = new LoadOptions(FileFormatType.XLSX);
		wb = new Workbook(fileName, loadOptions);
		ws = wb.getWorksheets().get(0);
		ws1 = wb.getWorksheets().get(1);
		wordsNum = ws.getCells().getMaxRow() + 1;
		wb.getWorksheets().removeAt("Evaluation Warning");
	}
	
	//понятно
	@Override
	public void save() {
		wb.getWorksheets().removeAt("Evaluation Warning");
		try {
			wb.save(fileName);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	//понятно
	@Override
	public void close() {
		save();
	}
	
	//геттер
	@Override
	public int getWordsNumber() {
		return wordsNum;
	}
	
	//выдает нужное слово нужного языка
	@Override
	public String getWord(int lang, int count) {
		//if (ws == null || ws.getCells() == null) return null;
		return ws.getCells().get(count, lang).getValue().toString();
	}
	
	//проверяет наличие метки повтора
	@Override
	public boolean isToRepeat(int lang, int count) {
		//if (ws1 == null || ws1.getCells() == null) return false;
		Cell c = ws1.getCells().get(count, lang);
		if (c == null || c.getValue() == null) return false;
		return true;
	}
	
	//устанавливает/или очищает метку повтора и сохраняет файл
	@Override
	public void setToRepeat(int lang, int count, boolean is) {
		Cell c = ws1.getCells().get(count, lang);
		c.setValue(is ? 1 : null);
		save();
	}
	
	//парсит ячейку с примерами и выдает пример нужного языка
	@Override
	public String getExample(int lang, int count) {
		Cell c = ws.getCells().get(count, 2);
		if (c == null || c.getValue() == null) return "";
		String str = c.getValue().toString();
		String str1[] = str.split("\n"); // разделитель между примерами
		String str2[];
		str = "";
		for (int i = 0; i < str1.length; i++ ) {
			str2 = str1[i].split(" — "); // разделитель между языками
			str = str + (i+1) + ": " + str2[lang] + "\n";
		}
		return str;
	}
}
