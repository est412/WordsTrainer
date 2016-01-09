package est412.wordstrainer.model;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class XLSXPoiDictionary implements Dictionary {
	protected int wordsNum; // количество слов в словаре = к-во строк в файле
	private XSSFWorkbook wb; //таблица
	private XSSFSheet ws; //вкладка словаря
	private XSSFSheet ws1; //вкладка с метками повторения
	String fileName; //имя файла
	FileInputStream fileInputStream; // необходимо для возможности нормального сохранения измененного файла

	//словарь существует только в свЯзи с открытым файлом
	public XLSXPoiDictionary(String fileName) throws Exception {
		this.fileName = fileName;
		open(fileName);
	}

	//понятно
	@Override
	public void open(String fileName) throws Exception {
		fileInputStream = new FileInputStream(new File(fileName));
		wb = new XSSFWorkbook(fileInputStream);
		//TODO добавить проверку наличия 0го воркшита
		ws = wb.getSheetAt(0);
		wordsNum = ws.getLastRowNum() + 1;
		ws1 = wb.getSheetAt(1);
		if (ws1 == null) {
			// создаём вторую вкладку
			ws1 = wb.createSheet();
		}
	}

	//понятно
	@Override
	public void save() {
		FileOutputStream fos = null;
		try {
			// обязательная последовательность для нормального сохранения файла
			// http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
			fileInputStream.close();
			fos = new FileOutputStream(fileName);
			wb.write(fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//понятно
	@Override
	public void close() {
		save();
		try (XSSFWorkbook workbook = wb) {
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//геттер
	@Override
	public int getWordsNumber() {
		return wordsNum;
	}

	//выдает нужное слово нужного языка
	@Override
	public String getWord(int lang, int count) {
		XSSFRow row = ws.getRow(count);
		if (row == null) return "";
		XSSFCell cell = row.getCell(lang);
		if (cell == null) return "";
		return cell.toString();
	}

	//проверяет наличие метки повтора
	@Override
	public boolean isToRepeat(int lang, int count) {
		XSSFRow row = ws1.getRow(count);
		if (row == null) return false;
		XSSFCell cell = row.getCell(lang);
		if (cell == null) return false;
		if ("".equals(cell.toString().trim())) return false;
		return true;
	}

	//устанавливает/или очищает метку повтора и сохраняет файл
	@Override
	public void setToRepeat(int lang, int count, boolean is) {
		XSSFRow row = ws1.getRow(count);
		if (row == null) {
			row = ws1.createRow(count);
		}
		// некрасиво, но уж как есть
		XSSFCell cell = row.getCell(lang);
		if (cell == null) {
			cell = row.createCell(lang);
		}
		cell.setCellValue(is ? "1" : "");
	}

	//парсит ячейку с примерами и выдает пример нужного языка
	@Override
	public String getExample(int lang, int count) {
		XSSFRow row = ws.getRow(count);
		if (row == null) return "";
		XSSFCell cell = row.getCell(2);
		if (cell == null) return "";
		String str = cell.toString();
		if ("".equals(str)) return "";
		String[] str1 = str.split("\n"); // разделитель между примерами
		String[] str2;
		str = "";
		for (int i = 0; i < str1.length; i++ ) {
			str2 = str1[i].split(" — "); // разделитель между языками
			str = str + (i+1) + ": " + str2[lang] + "\n";
		}
		return str;
	}
}
