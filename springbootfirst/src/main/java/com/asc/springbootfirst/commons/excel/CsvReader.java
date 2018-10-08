package com.asc.springbootfirst.commons.excel;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * CSV文件读取
 * 
 * @author yjq
 * @version 版本信息 创建时间 2015-10-08
 */
public class CsvReader {
	private static final Logger log = LoggerFactory.getLogger(CsvReader.class);

	private static final String[] HEADER = {"日期 ","单据类型","客户名称","数量","单位", "批号","单价",
		"商品编码","商品名称","商品规格","生产厂家"};
	public static List<Map<String, String>> getFileContent(String fileName,
                                                           Integer limit, Integer start)
			throws Exception {
		List<Map<String, String>> list = null;
		String[] csvRowTem = null;
		FileInputStream in = null;
		try {
			if(start == null){
				start =0;
			}
			list = new ArrayList<Map<String, String>>();
			in = new FileInputStream(new File(fileName));
			String coding = "GBK";
			byte[] code = new byte[3];
			in.read(code);
			in.close();
			if (code[0]==-17 && code[1]==-69 && code[2]==-65) {
				 coding = "UTF-8";
			} else if (code[0]==-1 && code[1]==-2) {
				 coding = "Unicode";
			}
			in = new FileInputStream(new File(fileName));
			CSVReader csvReader = new CSVReader(
					new InputStreamReader(in, coding));
			int j = 0;
			//默认第一行为标题行，如果为空，则文件非法，导入失败
			for (int i = 0; i <=start; i++) {
				csvRowTem = csvReader.readNext();
			}
			log.info(csvRowTem.toString());
			
			char separator = '\t';
			boolean isSplit = false;
			if (csvRowTem.length==1 && csvRowTem[0]!=null) {
				if (csvRowTem[0].indexOf("\t")>1) {
					separator = '\t';
					isSplit = true;
				}
			}
			if (isSplit) {
				in.close();
				in = new FileInputStream(new File(fileName));
				csvReader = new CSVReader(
						new InputStreamReader(in, coding),separator);
				for (int i = 0; i <=start; i++) {
					csvRowTem = csvReader.readNext();
				}
			}
			log.info(csvRowTem.toString());
			
			for (int i = 0; i < csvRowTem.length; i++) {
				if (csvRowTem[i] == null || "".equals(csvRowTem[i])) {
					j++;
				}
			}
			if (j == csvRowTem.length) {
				log.info("文件标题为空 ，文件不合法");
				return list;
			}
			String[] csvRow = null;
			Map<String, String> line = null;
			while ((csvRow = csvReader.readNext()) != null) {
//				try {
//					log.info(csvRow);
//					if (csvRow.length > csvRowTem.length) {
//						log.error("文件记录字段数大于标题列的字段数！");
//						throw new Exception("文件不合法，请检查标题字段数与记录数是否相等");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
				int m = 0;
				for (int i = 0; i < csvRow.length; i++) {
					if (csvRow[i] == null || "".equals(csvRow[i])) {
						m++;
					}
				}
				if (m == csvRow.length) {
					continue;
				}
				line = new HashMap<String, String>();
				for (int i = 0; i < csvRowTem.length; i++) {
					String data =null;
					try {
						data = csvRow[i].trim();
					}catch(Exception e){
						data="";
					}
					String colName = null;
					if(csvRowTem[i]!=null && !csvRowTem[i].equals("")){
						colName = csvRowTem[i].replaceAll(" ","");
					}
					if(colName==null) continue;
					line.put(colName.toLowerCase(), data);
				}
				list.add(line);
				if (limit!=null && list.size()>=limit) {
					return list;
				}
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			try {
				if(in!=null) in.close();
			} catch (Exception e2) {
			}
		}
		return null;
	}

	/**
	 * 获取标题
	 * @param fileName
	 * @param start 开始行
	 * @return
	 */
	public static List<String> getColumnNames(String fileName, Integer start) {
		log.info("start get file title: " + fileName);
		List<String> colsName = new ArrayList<String>();
		String[] csvRow = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(new File(fileName));
			String coding = "GBK";
			byte[] code = new byte[3];
			in.read(code);
			in.close();
			if (code[0] == -17 && code[1] == -69 && code[2] == -65) {
				coding = "UTF-8";
			} else if (code[0] == -1 && code[1] == -2) {
				coding = "Unicode";
			}
			in = new FileInputStream(new File(fileName));
			CSVReader csvReader = new CSVReader(new InputStreamReader(in, coding));
			// 默认第一行为标题行，如果为空，则文件非法，导入失败
			for (int i = 0; i <= start; i++) {
				csvRow = csvReader.readNext();
			}
			char separator = '\t';
			boolean isSplit = false;
			if (csvRow.length == 1 && csvRow[0] != null) {
				if (csvRow[0].indexOf("\t") > 1) {
					separator = '\t';
					isSplit = true;
				}
			}
			if (isSplit) {
				in.close();
				in = new FileInputStream(new File(fileName));
				csvReader = new CSVReader(new InputStreamReader(in, coding), separator);
				for (int i = 0; i <= start; i++) {
					csvRow = csvReader.readNext();
				}
			}			
			int j = 0;
			for (int i = 0; i < csvRow.length; i++) {
				if (csvRow[i] == null || "".equals(csvRow[i])) {
					j++;
				}
			}
			if (j == csvRow.length) {
				log.info("文件标题为空 ");
				return colsName;
			}
			for (int i = 0; i < csvRow.length; i++) {
				colsName.add(csvRow[i].toString().replaceAll(" ","").toLowerCase());
			}
			return colsName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(in!=null) in.close();
			} catch (Exception e2) {
			}
		}
	}
	public static void main(String[] args) throws Exception {
		CsvReader r = new CsvReader();
		List<Map<String, String>> list = CsvReader.getFileContent("D:\\tmp\\rdc\\1487576227477\\1487576234321.xls",null, 2);
		List<Map<String, String>> salList = Lists.newArrayList();
		
		for (Map<String, String> map : list) {
			for (Entry<String, String> entry:map.entrySet()) {
				if("单据类型".equals(entry.getKey())&& 
						(entry.getValue().contains("销售")
						||entry.getValue().contains("调拨"))){
					salList.add(map);
				}
			}
		}
		
		
		if(!salList.isEmpty()){
			for (Map<String, String> map : salList) {
				List<String> dtable = new ArrayList<String>();
				String[] str = new String[HEADER.length];
				dtable.add(map.get("日期"));
				dtable.add(map.get("单据类型"));
				dtable.add(map.get("客户名称"));
				for (Entry<String, String> entry:map.entrySet()) {
					if("单据类型".equals(entry.getKey())){
						if("正常销售订单".equals(entry.getValue()) || "调拨单出".equals(entry.getValue())){
							dtable.add(map.get("出库数量"));
						}
						if("销售退货订单".equals(entry.getValue()) || "调拨退货单".equals(entry.getValue())){
							dtable.add("-"+map.get("入库数量"));
						}
					}
				}
				dtable.add(map.get("单位"));
				dtable.add(map.get("批号"));
				dtable.add(map.get("单价"));
				dtable.add(map.get("商品编码"));
				dtable.add(map.get("商品名称"));
				dtable.add(map.get("商品规格"));
				dtable.add(map.get("生产厂家"));
			}
		}
		
	}
	
}
