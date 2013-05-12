package est412.wordstrainer.model;

import java.io.IOException;

import org.apache.poi.openxml4j.opc.OPCPackage;
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
		return sheet.getRow(count).getCell(lang).getStringCellValue();
	}
}
