package com.xiaohe.tryansjseg.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : 小何
 * @Description : 加载jdk文档，将其分词
 * @date : 2023-01-31 18:20
 */
public class Parser {
    // 需要转换的文件名
    private static final String FILENAME = "C:\\Users\\23825\\Downloads\\jdk-8u361-docs-all\\docs\\api";
    private static final String PATH = "https://docs/oracle.com/javase/8/docs/api/";

    public void run() {
        // 加载api文件夹中的所有html文件
        ArrayList<File> files = new ArrayList<>();
        enumFile(FILENAME, files);

        // 解析上述html文件
        for (File file : files) {
            System.out.println("开始解析: " + file.getAbsolutePath());
            parseHTML(file);
        }
        // 分词

        // 存储
    }
    // 解析html文件
    private void parseHTML(File file) {
        // 解析html的标题
        String title = parseTitle(file);

        // 解析html的URL


        // 解析HTML的正文
    }
//    a.html  2, 6

    // 解析html的标题
    private String parseTitle(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - 5);
    }

    private String paresURL(File file) {
        String part2 = file.getAbsolutePath().substring(FILENAME.length());
        return PATH + part2;

    }
    private String parseContent(File file) {
        return null;
    }

    // 将所有html文件放入list
    public void enumFile(String fileName, ArrayList<File> fileList) {
        File rootFile = new File(fileName);
        // 得到第一层所有的文件/文件夹
        File[] files = rootFile.listFiles();

        // 递归所有文件夹，取出所有文件
        for (File file : files) {

            
            
            // 如果是文件夹.递归调用
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath(), fileList);
            } else {
                // 如果是普通文件并且文件后缀为html，加入list
                if (file.getAbsolutePath().endsWith(".html")){
                    fileList.add(file);
                }
            }
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.run();
    }


}
