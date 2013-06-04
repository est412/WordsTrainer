package est412.wordstrainer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXDictionary implements Dictionary {
	private int wordsNum;
	
	private OPCPackage pkg;
	//private XSSFWorkbook wb;
	private Workbook wb;
	//private XSSFSheet sheet;
	private Sheet sheet;
	private InputStream inStream;
	private OutputStream outStream;
	File fil; 
	
	public XLSXDictionary(String fileName) throws IOException {
		open(fileName);
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#open(java.lang.String)
	 */
	@Override
	public void open(String fileName) throws IOException {
		try {
			pkg = OPCPackage.open(fileName);
			fil = new File(fileName+"1");
			//inStream = new FileInputStream(fileName);
			//outStream = new FileOutputStream(fileName);
			wb = new XSSFWorkbook(pkg);
			//wb = new XSSFWorkbook(inStream);
			//wb = WorkbookFactory.create(inStream);
			sheet = wb.getSheetAt(0);
			wordsNum = sheet.getPhysicalNumberOfRows();
		} catch (Exception e) {
			close();
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#save()
	 */
	@Override
	public void save() throws IOException {
		try {
			pkg.save(fil);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#close()
	 */
	@Override
	public void close() throws IOException {
		if (pkg != null) pkg.close();
		//if (inStream != null) inStream.close();
	}

	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#getWordsNumber()
	 */
	@Override
	public int getWordsNumber() {
		return wordsNum;
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#getWord(int, int)
	 */
	@Override
	public String getWord(int lang, int count) {
		return sheet.getRow(count).getCell(lang).toString();
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#isToRepeat(int, int)
	 */
	@Override
	public boolean isToRepeat(int lang, int count) {
		//XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		Cell cell = sheet.getRow(count).getCell(lang+4);
		if (cell == null) return false;
		return (cell.getNumericCellValue() == 1);
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#setToRepeat(boolean, int, int)
	 */
	@Override
	public void setToRepeat(int lang, int count, boolean is) {
		//XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		System.out.println("");
		Cell cell = sheet.getRow(count).getCell(lang+4);
		if (cell == null) {
			cell = sheet.getRow(count).createCell(lang+4);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		}
		System.out.println("set "+count+" "+is);
		cell.setCellValue(is ? 1 : 0);
		pkg.flush();
		//		wb.write(outStream);
		//System.out.println("flushed");
	}
	
	public String getExample(int lang, int count) {
		Cell cell = sheet.getRow(count).getCell(2);
		if (cell == null) return "";
		String str = cell.toString();
		String str1[] = str.split("\n");
		String str2[];
		str = "";
		for (int i = 0; i < str1.length; i++ ) {
			str2 = str1[i].split(" — ");
			str = str + str2[lang] + "\n";
			//System.out.println(str);
		}
		return str;
	}
}
