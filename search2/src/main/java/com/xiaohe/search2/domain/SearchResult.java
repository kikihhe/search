package com.xiaohe.search2.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String title;

    private String url;

    // 描述，从正文中提取
    private List<String> description;



}
