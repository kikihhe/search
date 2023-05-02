package com.xiaohe.search2.controller;

import com.xiaohe.search2.domain.SearchResult;
import com.xiaohe.search2.utils.DocSearch;
import com.xiaohe.search2.utils.FileUtils;
import com.xiaohe.search2.utils.Parser;
import com.xiaohe.search2.utils.ReturnResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class SearchController {
    @PostMapping("/uploadFile")
    public ReturnResult uploadDate(HttpServletRequest request) throws IllegalStateException, IOException, InterruptedException {
        Map<String, File> files = FileUtils.getFilesFromRequest(request);
        files.forEach((filePath, file) -> {
            System.out.println(filePath + "\t" + file);
        });
        if (Objects.isNull(files) || files.size() == 0) {
            return ReturnResult.error("请上传有效文件!");
        }
        Parser parser = new Parser();
        parser.runConcurrent(files);

        // 将文件夹中的文件删除
        List<File> fileList = new ArrayList<>();
        files.forEach((filePath, file) -> {
            fileList.add(file);
        });
        for (File file : fileList) {
            file.deleteOnExit();
        }
        return ReturnResult.success("成功创建索引");
    }








    @ResponseBody
    @GetMapping("/search")
    public ReturnResult search(String content) throws IOException, InterruptedException {
        DocSearch docSearch = new DocSearch();
        // 对content进行分词
        List<String> participle = docSearch.participle(content);
        // 对分词结果查询
        List<SearchResult> search = docSearch.search(participle);

        // 返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("分词结果", participle);
        map.put("查询结果", search);
        return ReturnResult.success(map);
    }
}
