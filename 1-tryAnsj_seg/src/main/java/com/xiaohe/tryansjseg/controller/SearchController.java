package com.xiaohe.tryansjseg.controller;

import com.xiaohe.tryansjseg.domain.Page;
import com.xiaohe.tryansjseg.domain.SearchResult;
import com.xiaohe.tryansjseg.utils.ApplicationContextUtil;
import com.xiaohe.tryansjseg.utils.DocSearch;
import com.xiaohe.tryansjseg.utils.Result;
import org.apache.logging.log4j.util.Strings;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-04-16 21:17
 */
@RestController
@CrossOrigin
public class SearchController {


    @GetMapping("/hello")
    public String hello() {
        return "你好";
    }





    /**
     * 实现搜索功能
     * @param query 用户想要实现的搜索词
     * @param page 分页
     * @return
     */
    @GetMapping("/search")
    public Result search(String query, Page page) throws IOException, InterruptedException {
        if (Strings.isEmpty(query)) {
            return Result.success(null);
        }
        DocSearch docSearch = (DocSearch) ApplicationContextUtil.getBean("docSearch");
        List<SearchResult> search = docSearch.search(query);

        return Result.success(search);

    }

    @PostMapping("/uploadFile")
    public Result uploadFile(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> file = multipartHttpServletRequest.getFiles("file");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < file.size(); i++) {
            System.out.println(file.get(i).getOriginalFilename() + "\t" + file.get(i).getName());
            list.add(file.get(i).getOriginalFilename());
        }
        return Result.success(list);
    }



}
