package com.asc.springbootfirst;

import com.google.common.base.Strings;

import java.io.*;

/**
 * @author:dijian
 * @date:2018/9/29
 */
public class ReaderTest {
    private static final String OUT_FILE = "E:\\test.txt";

    public static void main(String[] args) {
        String path = "E:\\IdeaProjects2018\\saleslook_work\\saleslook\\src\\main\\java\\com\\asc\\cloud\\report\\control";

        File file = new File(OUT_FILE);
        if(file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        read(path);
    }

    private static void read(String path) {
        File file = new File(path);
        if(file.isFile()) {
            readFile(file);
        }

        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tmpFile : files) {
                if(tmpFile.isFile()) {
                    readFile(tmpFile);
                }

                if(tmpFile.isDirectory()) {
                    read(tmpFile.getPath());
                }
            }
        }
    }

    private static void readFile(File file) {
        BufferedReader bufferedReader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(OUT_FILE, true));
            while ((line = bufferedReader.readLine()) != null) {
                if (Strings.isNullOrEmpty(line)) {
                    continue;
                }

                if(line.trim().startsWith("/")) {
                    continue;
                }

                if(line.trim().startsWith("*")) {
                    continue;
                }


                if(line.trim().startsWith("\t")) {
                    continue;
                }


                writer.write(line+"\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
