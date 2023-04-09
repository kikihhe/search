package com.xiaohe.tryansjseg.utils;

import com.xiaohe.tryansjseg.domain.Doc;
import com.xiaohe.tryansjseg.domain.Weight;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;


import java.util.*;

/**
 * 索引的各种操作
 */

public class Index {
    // 正排索引
    // List 的下标对应着文档的id
    private List<Doc> forwardIndex = new ArrayList<>();

    // 倒排索引
    // 使用哈希表表示倒排索引
    // key: 词
    // value: 与词相关的文章列表(Weight)
    private HashMap<String , ArrayList<Weight>> invertedIndex = new HashMap<>();


    /**
     * 获取文档信息
     */
    public Doc getDoc(int docId) {
        return forwardIndex.get(docId);
    }


    /**
     * 给定一个词，在倒排索引中查找哪些文档和这个词有关联
     * @param term 用户查找的词
     * @return 文档id与该词的权重的相关性
     */
    public List<Weight> getInverted(String term) {
        ArrayList<Weight> weights = invertedIndex.get(term);
        return weights;
    }

    /**
     * 新增一个文档到索引结构中
     * @param title
     * @param url
     * @param content
     */
    public void addDoc(String title, String url, String content) {
        // 构建一个正排索引
        Doc doc = buildForward(title, url, content);
        // 构建一个倒排索引
        buildInverted(doc);
    }

    /**
     * 给每个文档构建一个倒排索引
     * @param doc
     */
    private void buildInverted(Doc doc) {
        // 文档中被分出的每一个词都会拥有一个WordCnt
        class WordCnt {
            public int titleCount;
            public int contentCount;
        }
        // 用于统计词频, String: 被分出的词 WordCnt: 它出现在标题、内容之中的次数
        HashMap<String, WordCnt> wordCntHashMap = new HashMap<>();
        // 需要针对标题和正文进行分词
        Result result1= ToAnalysis.parse(doc.getTitle());
        Result result2 = ToAnalysis.parse(doc.getContent());

        // 分别遍历分词结果，统计每一个词出现的次数计算权重
        // 打中标题，加10分
        // 打中内容，加1分
        // 1. 遍历标题的分词结果，统计出现次数
        List<Term> terms1 = result1.getTerms();
        for (Term term : terms1) {
            String word = term.getName();
            WordCnt wordCnt = wordCntHashMap.get(word);

            if (Objects.isNull(wordCnt)) {
                WordCnt w = new WordCnt();
                w.titleCount = 1;
                w.contentCount = 0;
                wordCntHashMap.put(word, w);
            } else {
                wordCnt.titleCount++;
            }
        }

        // 2. 遍历内容的分词结果，统计出现次数
        List<Term> terms2 = result2.getTerms();
        for (Term term : terms2) {
            String word = term.getName();
            WordCnt wordCnt = wordCntHashMap.get(word);
            if (Objects.isNull(wordCnt)) {
                WordCnt w = new WordCnt();
                w.titleCount = 0;
                w.contentCount = 1;
                wordCntHashMap.put(word, w);
            } else {
                wordCnt.contentCount++;
            }
        }


        // 将上述结果汇总到map中。
        for (Map.Entry<String, WordCnt> entry : wordCntHashMap.entrySet()) {
            String word = entry.getKey();
            WordCnt wordCnt = entry.getValue();
            Weight weight = new Weight();
            weight.setDocId(doc.getDocId());
            weight.setWeight((long)(wordCnt.titleCount * 10L + wordCnt.contentCount));

            ArrayList<Weight> weights = invertedIndex.get(word);
            if (Objects.isNull(weights)) {
                ArrayList<Weight> list = new ArrayList<>();
                list.add(weight);
                invertedIndex.put(word, list);
            } else {
                weights.add(weight);
            }
        }

    }

    /**
     * 构建一个正排索引
     * @param title
     * @param url
     * @param content
     * @return
     */
    private Doc buildForward(String title, String url, String content) {
        Doc doc = null;
        synchronized (this) {
            doc = new Doc(forwardIndex.size(), title, url, content);
            // 将文档放入正排索引中，
            forwardIndex.add(doc);
        }
        return doc;
    }

    /**
     * 把内存中的索引结构保存到磁盘中
     */
    public void save() {

    }

    /**
     * 把磁盘中的索引结构加载到内存里
     */
    public void load() {

    }
}
