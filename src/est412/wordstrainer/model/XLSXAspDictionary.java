package est412.wordstrainer.model;

import com.aspose.cells.Cell;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

public class XLSXAspDictionary implements Dictionary {
	protected int wordsNum;
	private Workbook wb;
	private Worksheet ws;
	String fileName; 
	
	public XLSXAspDictionary(String fileName) throws Exception {
		this.fileName = fileName;
		open(fileName);
	}
	
	@Override
	public void open(String fileName) throws Exception {
		LoadOptions loadOptions = new LoadOptions(FileFormatType.XLSX);
		wb = new Workbook(fileName, loadOptions);
		ws = wb.getWorksheets().get(0);
		wordsNum = ws.getCells().getMaxRow() + 1;
		wb.getWorksheets().removeAt("Evaluation Warning");
	}
	
	@Override
	public void save() throws Exception {
		wb.save(fileName);
		
	}
	
	@Override
	public void close() throws Exception {
		save();
	}

	@Override
	public int getWordsNumber() {
		return wordsNum;
	}
	
	@Override
	public String getWord(int lang, int count) {
		return ws.getCells().get(count, lang).getValue().toString();
	}
	
	@Override
	public boolean isToRepeat(int lang, int count) {
		//XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		//Cell cell = ws.getRow(count).getCell(lang+4);
		//if (cell == null) return false;
		return true;//(cell.getNumericCellValue() == 1);
	}
	
	@Override
	public void setToRepeat(int lang, int count, boolean is) {
		//XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		System.out.println("");
		//Cell cell = ws.getRow(count).getCell(lang+4);
		//if (cell == null) {
		//	cell = ws.getRow(count).createCell(lang+4);
		//	cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		//}
		//System.out.println("set "+count+" "+is);
		//cell.setCellValue(is ? 1 : 0);
		//pkg.flush();
		//		wb.write(outStream);
		//System.out.println("flushed");
	}
	
	public String getExample(int lang, int count) {
		Cell c = ws.getCells().get(count, 2);
		//System.out.println(c);
		if (c == null || c.getValue() == null) return "";
		String str = c.getValue().toString();
		String str1[] = str.split("\n");
		String str2[];
		str = "";
		for (int i = 0; i < str1.length; i++ ) {
			str2 = str1[i].split(" — ");
			str = str + (i+1) + ": " + str2[lang] + "\n";
		}
		return str;
	}
}
