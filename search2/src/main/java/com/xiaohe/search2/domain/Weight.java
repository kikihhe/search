package com.xiaohe.search2.domain;

import lombok.Data;

/**
 * 权重，文档id和词的关联性强弱
 */
@Data
public class Weight implements Comparable<Weight> {
    private int docId;
    private long weight;


    @Override
    public int compareTo(Weight o) {
        return this.docId - o.docId;
    }
}
