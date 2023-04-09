package com.xiaohe.tryansjseg.domain;

import lombok.Data;

/**
 * 权重，文档id和词的关联性强弱
 */
@Data
public class Weight {
    private int docId;
    private long weight;
}
