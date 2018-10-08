package com.asc.springbootfirst;

import com.asc.springbootfirst.commons.excel.CsvReader;
import com.asc.springbootfirst.commons.excel.ExcelWrite;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author:dijian
 * @date:2018/9/29
 */
public class CsvSplitter {
    private static final String OUT_PATH = "E:\\内网通\\程俊杰\\0929\\out";

    private static List<String> columnNames = Lists.newArrayList();

    public static void main(String[] args)  {
        Date start = new Date();
        System.out.println("开始时间：" + start);
        String path  = "E:\\内网通\\程俊杰\\0929";
        read(path);
        Date end = new Date();
        System.out.println("结束时间：" + end);
        System.out.println("共用时：" + (end.getTime() - start.getTime())/1000 + "秒");
        System.out.println("拆分成功");

    }

    private static void read(String path) {
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".csv")) {
                    return true;
                }
                return false;

            }
        });

        for (File file1 : files) {
           Map<String, ExcelWrite> writerMap = Maps.newHashMap();
           writeFile(file1,writerMap);
        }
    }

    private static void writeFile(File file, Map<String, ExcelWrite> writerMap) {

        columnNames = CsvReader.getColumnNames(file.getPath(), 0);
        columnNames.remove("");

        getFileContent(file, writerMap);

        writeEnd(file, writerMap);

    }

    private static void writeEnd(File file, Map<String, ExcelWrite> writerMap) {
        String fileType = file.getName().substring(0, file.getName().indexOf("."));
        OutputStream outputStream = null;
        try {
            for (Map.Entry<String, ExcelWrite> writeEntry : writerMap.entrySet()) {
                String fileName = OUT_PATH + File.separator + writeEntry.getKey() + File.separator + "SJYY_" + fileType.toUpperCase()  +  "_MON_2018-09-29.xlsx";
                if("inv".equalsIgnoreCase(fileType)) {
                    fileName = OUT_PATH + File.separator + writeEntry.getKey() + File.separator + "SJYY_" + fileType.toUpperCase()  +  "_CUR_2018-09-29.xlsx";
                }
                File outFile = new File(fileName);
                if(!outFile.getParentFile().exists()){
                    outFile.getParentFile().mkdir();
                }
                outputStream = new FileOutputStream(outFile);

                writeEntry.getValue().ExcelEnd(outputStream);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    static String lastLine = "";
    private static void write(String line, Map<String, ExcelWrite> writerMap) {

        if(Strings.isNotBlank(lastLine)) {
            line = lastLine + line;
        }
        line = line.replaceAll("\",\"", "\"@@@@@\"");
        line = line.replaceAll(","," ");
        List<String> dataList1 = Splitter.on("@@@@@").splitToList(line);
        List<String> dataList = Lists.newArrayList();

        for (int i = 1; i < dataList1.size(); i++) {
            String s = dataList1.get(i);
            if(Strings.isNotBlank(s)) {
                s = s.replaceAll("\"", "");
            }
            dataList.add(s);
        }

        String area = dataList.get(dataList.size() - 1);

        if(!area.endsWith("区")) {
            lastLine = line;
            return;
        } else {
            lastLine = "";
        }

        area = area.replaceAll("\"", "");
        ExcelWrite writer = null;
        if(writerMap.containsKey(area)) {
            writer = writerMap.get(area);
        } else  {
            writer = new ExcelWrite();
            writer.write(columnNames.toArray(new String[columnNames.size()]));
            writerMap.put(area, writer);
        }

        writer.write(dataList.toArray(new String[dataList.size()]));
    }


    public static void getFileContent(File file, Map<String, ExcelWrite> writerMap) {
        try {
            LineIterator lineIterator = FileUtils.lineIterator(file, "GBK");
            int rowNum = 0;
            while(lineIterator.hasNext())
            {

                String next = lineIterator.next();
                if(rowNum++ == 0) {
                    continue;
                }

                write(next, writerMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
