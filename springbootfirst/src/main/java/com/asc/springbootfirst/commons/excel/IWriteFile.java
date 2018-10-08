package com.asc.springbootfirst.commons.excel;

import java.io.OutputStream;

public interface IWriteFile {
	public void write(String[] data);
	public int getRownum();
	public boolean ExcelEnd(OutputStream out);
	public boolean isCreate();
}
