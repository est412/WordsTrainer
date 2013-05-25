package est412.wordstrainer.model;

import java.io.IOException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXDictionary implements Dictionary {
	private int wordsNum;
	
	private OPCPackage pkg;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
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
			wb = new XSSFWorkbook(pkg);
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
		
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#close()
	 */
	@Override
	public void close() throws IOException {
		if (pkg != null) pkg.close();
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
		XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		if (cell == null) return false;
		return (cell.getNumericCellValue() == 1);
	}
	
	/* (non-Javadoc)
	 * @see est412.wordstrainer.model.Dictionary#setToRepeat(boolean, int, int)
	 */
	@Override
	public void setToRepeat(int lang, int count, boolean is) {
		XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		if (cell == null) {
			cell = sheet.getRow(count).createCell(lang+2);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		}
		cell.setCellValue(is ? 1 : 0);
		pkg.flush();
		//System.out.println("flushed");
	}
}
