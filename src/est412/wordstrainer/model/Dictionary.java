package est412.wordstrainer.model;

import java.io.IOException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Dictionary {
	private int wordsNum;
	
	private OPCPackage pkg;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	
	public Dictionary(String fileName) throws IOException {
		try {
			pkg = OPCPackage.open(fileName);
			wb = new XSSFWorkbook(pkg);
			sheet = wb.getSheetAt(0);
			wordsNum = sheet.getPhysicalNumberOfRows();
		} catch (Exception e) {
			if (pkg != null) pkg.close();
			e.printStackTrace();
		}
	}

	public int getWordsNum() {
		return wordsNum;
	}
	
	public OPCPackage getPkg() {
		return pkg;
	}
	
	public String getWord(int count, int lang) {
		return sheet.getRow(count).getCell(lang).toString();
	}
	
	public boolean isToRepeat(int count, int lang) {
		XSSFCell cell = sheet.getRow(count).getCell(lang+2);
		if (cell == null) return false;
		return (cell.getNumericCellValue() == 1);
	}
	
	public void setToRepeat(boolean is, int count, int lang) {
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
