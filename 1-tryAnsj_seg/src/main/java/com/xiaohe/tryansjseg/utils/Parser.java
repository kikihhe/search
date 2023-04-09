package com.xiaohe.tryansjseg.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;


/**
 * 加载jdk文档，将其分词
 */
@Component
public class Parser {
    // 需要转换的文件名
//    private static final String FILENAME = "D:\\tools\\jdk-20_doc-all\\docs\\api";
//    @Value("{spring.file.path}")
    private static final String FILE_PATH = "D:\\tools\\jdk-20_doc-all\\docs\\api";
    private static final String PATH = "https://docs.oracle.com/javase/8/docs/api";

    public void run() {
        // 加载api文件夹中的所有html文件
        ArrayList<File> files = new ArrayList<>();
        enumFile(FILE_PATH, files);

        // 解析上述html文件
        for (File file : files) {
            System.out.println("开始解析: " + file.getAbsolutePath());
            parseHTML(file);
        }
        // 分词

        // 存储
    }
    // 解析html文件
    private void parseHTML(File file)  {
        // 解析html的标题
        String title = parseTitle(file);

        // 转换html的URL, 加载出本地文件路径对应的在线文件路径
        String url = paresURL(file);

        // 解析HTML的正文
        String content = parseContent(file);

        // TODO 将这些title url content装入索引中。

    }

    // 解析html的标题
    private String parseTitle(File file) {
        String fileName = file.getName();
        // 假如该文件的名称为 ArrayList.html，需要展示的是ArrayList, 将.html去掉
        return fileName.substring(0, fileName.length() - 5);
    }

    // 将本地文件路径转为在线文档的路径
    private String paresURL(File file) {
        String part2 = file.getAbsolutePath().substring(FILE_PATH.length());
        return PATH + part2;

    }
    // 解析html文件中的内容，主要是将内容提取出来，将标签删除
    private String parseContent(File file)  {
        // 去除标签
        // 遇到 <, 将flag置为false
        // 遇到 >, 将flag置为true
        // 如果遇到文本中有><怎么办?
        // 不会，HTML规定使用转义字符来代替，< 使用 &lt; 来代替，>使用 &gt; 来代替。所以不会出现大于小于
        boolean flag = true;
        FileReader fileReader = null;
        StringBuilder content = new StringBuilder();
        try {
            fileReader = new FileReader(file);
            while (true) {
                int read = fileReader.read();
                if (read == -1) {
                    System.out.println(file.getAbsolutePath() + "读完了.");
                    break;
                }
                char c = (char) read;
                if (flag) {
                    if (c == '<') {
                        flag = false;
                        continue;
                    }
                    // 如果开关打开, 证明可以读取
                    // 如果该字符是换行，替换为空格
                    if (c == '\n' || c == '\r') {
                        c = ' ';
                    }
                    content.append(c);

                } else {
                    // 如果开关关闭
                    if (c == '>') {
                        flag = true;
                        continue;
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 返回最终结果
        return content.toString();
    }

    // filePath: 路径
    // fileList: 将所有.html文件放入这个集合
    public void enumFile(String filePath, ArrayList<File> fileList) {
        File rootFile = new File(filePath);
        // 得到第一层所有的文件/文件夹
        File[] files = rootFile.listFiles();

        // 递归所有文件夹，取出所有文件
        for (File file : files) {
            // 如果是文件夹.递归调用
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath(), fileList);
            } else {
                // 如果是普通文件并且文件后缀为html，加入list, 如果不是，直接跳过
                // 因为文件可能有.css文件
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
