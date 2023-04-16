package com.xiaohe.tryansjseg.controller;

import com.sun.istack.internal.NotNull;
import com.xiaohe.tryansjseg.domain.SearchResult;
import com.xiaohe.tryansjseg.utils.DocSearch;
import com.xiaohe.tryansjseg.utils.Result;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-04-16 21:17
 */
@RestController
public class SearchController {


    @Autowired
    private DocSearch docSearch;
    /**
     * 实现搜索功能
     * @param query
     * @return
     */
    @GetMapping("/search")
    public Result search(String query) {
        if (Strings.isEmpty(query)) {
            return Result.success(null);
        }
        List<SearchResult> search = docSearch.search(query);

        return Result.success(search);

    }
}
