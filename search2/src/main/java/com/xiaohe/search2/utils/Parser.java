package com.xiaohe.search2.utils;



import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 加载jdk文档，将其分词
 */
@Component
public class Parser {
    // 需要转换的文件名
//    private static final String FILE_PATH = "C:\\Users\\23825\\Desktop\\测试";
//    private static final String PATH = "https://docs.oracle.com/javase/8/docs/api";
    private static final List<String> FILE_TYPE = new ArrayList<>(Arrays.asList(".txt", ".html"));

    // 索引，
    private Index index = new Index();
//    public void run(List<File> files) throws IOException {
//        // 解析上述html文件
//        for (File file : files) {
//            parseHTML(file);
//        }
//        // 存储
//        index.save();
//
//    }



    /**
     * 多线程制作索引
     */
    public void runConcurrent(List<File> files) throws InterruptedException, IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        // 遍历文件，解析文件
        for (File file : files) {
            executorService.submit(() -> {
                parseHTML(file);
                countDownLatch.countDown();
            });

        }
        countDownLatch.await();
        // 终止线程
        executorService.shutdown();
        index.save();
    }

    /**
     * 基于正则表达式实现去除标签以及取出script的功能
     * @param f
     * @return
     */
    public String readFile(File f) {
        StringBuffer content = new StringBuffer();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
            while (true) {
                int ret = bufferedReader.read();
                if (ret == -1) {
                    break;
                }
                char ch = (char) ret;
                if (ch == '\n' || ch == '\r') ch = ' ';
                content.append(ch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return content.toString();

    }
    public String  parseContentByRegex(File f) {
        String content = readFile(f);
        // 先替换script，否则会出错。
        content = content.replaceAll("<script.*?>(.*?)</script>", " ");
        content = content.replaceAll("<.*?>", " ");
        // 将多个空格缩减为一个空格
        content = content.replaceAll("\\s+", " ");
        return  content;
    }





    // 解析html文件
    private void parseHTML(File file)  {
        // 解析html的标题
        String title = parseTitle(file);

        //将本地的url作为文件的url
        String url = file.getAbsolutePath();


        // 解析HTML的正文
        String content = parseContentByRegex(file);


        // 将这些title url content装入索引中。
        index.addDoc(title, url, content);




    }

    // 解析html的标题
    private String parseTitle(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf(".");
        // 假如该文件的名称为 ArrayList.html，需要展示的是ArrayList, 将.html去掉
        return fileName.substring(0, i);
    }
//
    public static File multipartFileToFile(MultipartFile mfile) throws IOException {
        File file = new File(mfile.getOriginalFilename());
        FileUtils.copyInputStreamToFile(mfile.getInputStream(), file);
        return file;
    }

}
