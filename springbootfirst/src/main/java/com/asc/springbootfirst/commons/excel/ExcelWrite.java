package com.asc.springbootfirst.commons.excel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;


public class ExcelWrite implements IWriteFile{
	private final static Logger log = Logger.getLogger(ExcelWrite.class);
	private int rownum=0;
	
	SXSSFWorkbook wb =  null;
	Sheet sh = null;
	
	private boolean isCreate=false;

	private boolean isEnd=false;

	public boolean isCreate() {
		return isCreate;
	}

	public SXSSFWorkbook getWb() {
		return wb;
	}

	//.asc

	public ExcelWrite(){
		wb = new SXSSFWorkbook(100);
		sh = wb.createSheet("Sheet1");
	}

	public ExcelWrite(SXSSFWorkbook wb, Sheet sh) {
		this.wb = wb;
		this.sh = sh;
	}

	public ExcelWrite(SXSSFWorkbook wb, Sheet sh, boolean isEnd) {
		this.wb = wb;
		this.sh = sh;
		this.isEnd = isEnd;
	}

	public void write(String[] data){
		Row r = sh.createRow(rownum++);
		for (int cellnum = 0; cellnum < data.length; cellnum++) {
			Cell c = r.createCell(cellnum);
			c.setCellValue(data[cellnum]);
		}
	}
	
	public void write(String[] data, int rownum){
		Row r = sh.createRow(rownum);
		for (int cellnum = 0; cellnum < data.length; cellnum++) {
			Cell c = r.createCell(cellnum);
			c.setCellValue(data[cellnum]);
		}
		
		
	}
	
	public boolean ExcelEnd(OutputStream out){
		try {
			if (rownum<=0) {
				return false;
			}
			wb.write(out);
			isCreate=true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} finally{
			wb.dispose();
			try {
				out.close();
			} catch (IOException e) {
				log.error(e);
			}
			out = null;
			sh = null;
			wb = null;
		}
		
		return true;
	}

	public boolean ExcelEnd(OutputStream out,Sheet... shs){
		try {
			wb.write(out);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} finally{
			wb.dispose();
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
			for (Sheet sh:shs){
				sh = null;
			}
			wb = null;
		}

		return true;
	}
	
	public void dispose(){
		if(wb!=null){
			wb.dispose();
			sh=null;
			wb=null;
		}
	}
	public int getRownum() {
		return rownum;
	}

}
