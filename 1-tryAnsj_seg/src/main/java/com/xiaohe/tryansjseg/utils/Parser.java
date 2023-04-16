package com.xiaohe.tryansjseg.utils;


import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 加载jdk文档，将其分词
 */
@Component
public class Parser {
    // 需要转换的文件名
    private static final String FILE_PATH = "D:\\tools\\jdk-20_doc-all\\docs\\api";
    private static final String PATH = "https://docs.oracle.com/javase/8/docs/api";

    // 索引，
    private Index index = new Index();

    // 计算读取HTML内容花费的时间
    private AtomicLong time1 = new AtomicLong();
    // 计算将文档加入索引库(正排/倒排索引)需要的时间
    private AtomicLong time2 = new AtomicLong();


    public void run() throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("开始解析");
        // 加载api文件夹中的所有html文件
        System.out.println("开始遍历文件");
        long start2 = System.currentTimeMillis();
        ArrayList<File> files = new ArrayList<>();
        enumFile(FILE_PATH, files);
        long end2 = System.currentTimeMillis();
        System.out.println("文件遍历结束，花费时间: " + (end2 - start2));
        // 解析上述html文件

        long start1 = System.currentTimeMillis();
        System.out.println("开始制作索引");
        for (File file : files) {
            System.out.println("开始解析: " + file.getAbsolutePath());
            parseHTML(file);
        }
        long end1 = System.currentTimeMillis();
        System.out.println("索引文件遍历结束，花费时间: " + (end1 - start1));
        // 存储
        index.save();
        long end = System.currentTimeMillis();
        System.out.println("索引解析完毕，耗时: " + (end-start)/1000 + "s --->" + (end-start) + "ms");

    }
    /**
     * 多线程制作索引
     */
    public void runConcurrent() throws InterruptedException, IOException {
        long start1 = System.currentTimeMillis();

        ArrayList<File> files = new ArrayList<>();
        // 读取索引
        enumFile(FILE_PATH, files);
        System.out.println("开始多线程制作索引");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        // 遍历文件，解析文件
        for (File file : files) {
            executorService.submit(() -> {
                System.out.println("开始解析: " + file.getAbsolutePath());
                parseHTML(file);
                countDownLatch.countDown();
            });

        }
        countDownLatch.await();
        // 终止线程
        executorService.shutdown();

        long end1 = System.currentTimeMillis();
        System.out.println("索引制作完毕，花费时间: " + (end1 - start1));
        index.save();


        System.out.println("读取HTML文档中的Content花费时间: " + time1.get());
        System.out.println("将文档加入(正、倒)索引所花费的时间: " + time2.get());




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

        // 转换html的URL, 加载出本地文件路径对应的在线文件路径
        String url = paresURL(file);
        long start = System.nanoTime();

        // 解析HTML的正文
        String content = parseContentByRegex(file);

        long mid = System.nanoTime();
        // 将这些title url content装入索引中。
        index.addDoc(title, url, content);
        long end = System.nanoTime();


        time1.addAndGet(mid - start);
        time2.addAndGet(end - mid);

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

    public static void main(String[] args) throws IOException, InterruptedException {
        Parser parser = new Parser();
//        parser.run();
        parser.runConcurrent();

    }


}
