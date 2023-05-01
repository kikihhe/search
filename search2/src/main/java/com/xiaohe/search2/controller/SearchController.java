package com.xiaohe.search2.controller;

import com.xiaohe.search2.domain.SearchResult;
import com.xiaohe.search2.utils.DocSearch;
import com.xiaohe.search2.utils.Parser;
import com.xiaohe.search2.utils.ReturnResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class SearchController {
    @PostMapping("/uploadFile")
    public ReturnResult uploadFile(HttpServletRequest request) throws IOException, InterruptedException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> file = multipartHttpServletRequest.getFiles("file");


        List<File> files = new ArrayList<>();
        for (MultipartFile file1: file) {
            File e = Parser.multipartFileToFile(file1);
            files.add(e);
        }
        Parser parser = new Parser();
        parser.runConcurrent(files);

        // 将文件夹中的文件删除
        for (File file1 : files) {
            if (file1.exists()) {
                file1.delete();
            }
        }
        return ReturnResult.success("成功");
    }




    @ResponseBody
    @GetMapping("/search")
    public ReturnResult search(String content) throws IOException, InterruptedException {
        System.out.println(content);
        DocSearch docSearch = new DocSearch();
        // 对content进行分词
        List<String> participle = docSearch.participle(content);
        System.out.println(participle);
        // 对分词结果展示
        List<SearchResult> search = docSearch.search(participle);
        System.out.println(search);
        Map<String, Object> map = new HashMap<>();
        map.put("分词结果", participle);
        map.put("查询结果", search);
        return ReturnResult.success(map);
    }
}
