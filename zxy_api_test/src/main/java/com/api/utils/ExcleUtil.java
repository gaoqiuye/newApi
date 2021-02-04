package com.api.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcleUtil {
	private static Workbook workbook = null;
	private static Sheet sheet = null;
	private static BufferedInputStream bis = null;
	private static Logger log = Logger.getLogger(ExcleUtil.class);
	private static String dictName = "";
	private static int executeNum = 1;
	public static String workPath = System.getProperty("user.dir").replace("\\", "/");

	public static Object[][] getExclesData(String totalExcles, String totalSheets) {
		// 获取所有的excle文件的name和sheetName
		List<String[]> exclesAndSheets = getExcleData(totalExcles, totalSheets);
		List<String[]> allTestDataList = new ArrayList<>();
		for (int i = 0; i < exclesAndSheets.size(); i++) {
			if (!exclesAndSheets.get(i)[1].equals("空")) {
				dictName = exclesAndSheets.get(i)[1];
			}
			String excleName = dictName + "/" + exclesAndSheets.get(i)[2];
			String sheetsName = exclesAndSheets.get(i)[3];
			String excleFlag = exclesAndSheets.get(i)[4];
			String executeNumStr = exclesAndSheets.get(i)[5];
			if (!executeNumStr.equals("空")) {
				try {
					executeNum = Integer.parseInt(executeNumStr);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			for (int j = 0; j < executeNum; j++) {
				if (excleFlag.toLowerCase().equals("y")) {
					String[] sheetsArr = sheetsName.split(";");
					for (int p = 0; p < sheetsArr.length; p++) {
						List<String[]> sheetDataList = getExcleData(excleName, sheetsArr[p]);
						// 在每个sheet表中的测试数据后面追加对应的excle和sheet的name
						for (int k = 0; k < sheetDataList.size(); k++) {
							String[] tempArr = new String[sheetDataList.get(k).length + 2];
							for (int m = 0; m < tempArr.length - 2; m++) {
								tempArr[m] = sheetDataList.get(k)[m];
							}
							tempArr[tempArr.length - 2] = excleName;
							tempArr[tempArr.length - 1] = sheetsArr[p];
							sheetDataList.set(k, tempArr);
						}
						// 将重组后的数据添加到全部测试数据的集合中
						allTestDataList.addAll(sheetDataList);
					}
				}
			}
		}
		Object[][] ob = new Object[allTestDataList.size()][];
		for (int i = 0; i < ob.length; i++) {
			ob[i] = allTestDataList.get(i);
		}
		return ob;
	}

	/**
	 * 读取一个excle表格数据: excleName是文件名； sheetName是表名
	 */
	public static List<String[]> getExcleData(String excleName, String sheetName) {
		workbook = getWorkBook(excleName);
		sheet = workbook.getSheet(sheetName);
		List<String[]> list = new ArrayList<String[]>();
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		int sequenceNum = 2;
		// firstRowNum + 1去掉首行数据
		for (int i = firstRowNum + 1; i < lastRowNum + 1; i++) {
			Row row = sheet.getRow(i);
			int lastCellNum = row.getLastCellNum();
			// lastCellNum-2，不取最后2个单元格的数据
			String[] cellRrr = new String[lastCellNum + 1];
			cellRrr[0] = sequenceNum + "";
			for (int j = 0; j < lastCellNum; j++) {
				Cell cell = row.getCell(j);
				if (row.getCell(j) != null) {
					cell.setCellType(CellType.STRING);
					String cellStr = cell.getStringCellValue().trim();
					if (cellStr.length() > 0) {
						cellRrr[j + 1] = cellStr;
					} else {
						cellRrr[j + 1] = "空";
					}
				} else {
					cellRrr[j + 1] = "空";
				}
			}
			list.add(cellRrr);
			sequenceNum += 1;
		}
		try {
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return list;
	}

	/**
	 * 往指定的excle文件中的sheet表格指定的单元格写入数据，result为所写入的内容
	 */
	public static void excleWriter(String excleName, String sheetName, String rownum, String cellnum, String result) {
		File excleFile = getFile(excleName);
		workbook = getWorkBook(excleName);
		sheet = workbook.getSheet(sheetName);
		Row row = sheet.getRow(Integer.parseInt(rownum));
		Cell cell = row.getCell(Integer.parseInt(cellnum) - 1);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(result);
		try {
			FileOutputStream fos = new FileOutputStream(excleFile);
			workbook.write(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取workbook,并判断excle文件的类型
	 */
	private static Workbook getWorkBook(String excleName) {
		File excleFile = getFile(excleName);
		try {
			bis = new BufferedInputStream(new FileInputStream(excleFile));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}
		if ((excleName.substring(excleName.indexOf(".")).equals(".xls"))) {
			try {
				workbook = new HSSFWorkbook(bis);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		} else if ((excleName.substring(excleName.indexOf(".")).equals(".xlsx"))) {
			try {
				workbook = new XSSFWorkbook(bis);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		} else {
			log.error(excleName + "文件类型错误！");
		}
		return workbook;
	}

	/**
	 * 获取excle文件，并判断excle文件是否存在
	 */
	public static File getFile(String excleName) {
		String exclePath = workPath + "/testCases/" + excleName;
		File excleFile = new File(exclePath);
		if (excleFile.isFile()) {
			return excleFile;
		} else {
			log.error("请检查" + exclePath + "路径！");
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(getExcleData("apiTotalTestCase1.xlsx", "Sheet1").get(3)));
	}
}
