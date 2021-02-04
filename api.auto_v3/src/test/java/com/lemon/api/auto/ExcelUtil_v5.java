package com.lemon.api.auto;


import java.io.File;
//import java.io.IOException;
//
//import org.apache.poi.EncryptedDocumentException;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.omg.CORBA.OBJ_ADAPTER;
//import org.testng.reporters.jq.Main;

/*
 * excelPath:用例文件路径
 * startRow:行号 而不是行索引
 * startCell：列号
 * 
 * rows:行号数组
 * cells:列号数组
 */

public class ExcelUtil_v5 {
	public  static Object[][] datas(String excelPath,String sheetName,int[]rows,int[]cells) { 
		//这个只适用于连续的行号，列号，如果取某行某列可以写成  int[]rows={3,4,5},int[]cells={4,5}
		//String excelPath="src/test/resources/case_v1.xls";
		Object[][] datas=null;
		//获取workbook对象
		try {
			Workbook workbook=WorkbookFactory.create(new File(excelPath));
			//获取sheet对象
			Sheet sheet=workbook.getSheet("sheetName");
			
			//定义保存数据的数组
			datas=new Object[rows.length][cells.length];
			for (int i=0;i<rows.length;i++) {
				//根据行索引取出一行
				Row row=sheet.getRow(rows[i]-1); //行号-1===索引
				//获取到每一行后 再获取该行的列，手机号和密码分别在第五列和第六列
				//Cell cell=row.getCell(5);  从第五列开始，取到第六列
				for(int j=0;j<=cells.length;j++) {
					Cell cell=row.getCell(cells[j]-1,MissingCellPolicy.CREATE_NULL_AS_BLANK); //通过每一行取出每一列,j后面加的这个后，如果参数值为空则返回空而不是null
					//将列设置为字符串类型
					cell.setCellType(CellType.STRING);
					//取出每列的值
					String value=cell.getStringCellValue();
					datas[i][j]=value;  //第一组数据是datas[0][0]
					
				}
				
			}
			
			//获取列
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return datas;
	}
	

	
}
















