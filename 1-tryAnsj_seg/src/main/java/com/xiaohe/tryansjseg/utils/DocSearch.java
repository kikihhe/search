package com.xiaohe.tryansjseg.utils;

import com.xiaohe.tryansjseg.domain.Doc;
import com.xiaohe.tryansjseg.domain.SearchResult;
import com.xiaohe.tryansjseg.domain.Weight;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-04-13 19:53
 */
@Component
public class DocSearch {

    private static Index index = new Index();


    public DocSearch() throws IOException, InterruptedException {
        // 加载索引
        index.load();
    }
    public String generateDescription(String content, List<Term> terms) {
        // 去除普通标签和script标签
        content = content.replaceAll("<script.*?>(.*?)</script>", "");
        content = content.replaceAll("<.*?>", "");
        int pos = -1;
        for (Term term : terms) {
            String word = term.getName();
            pos = content.toLowerCase(Locale.ROOT).indexOf(" " + word + " ");
            if (pos >= 0) {
                break;
            }
        }
        // 遍历完也没有找到, 所有分词结果都没有
        if (pos == -1) {
           return "";
        }
        // 从pos向前找60个字符
        int begin = pos < 60 ? 0 : pos - 60;
        String result = "";

        if (begin + 160 > content.length()) {
            result = content.substring(begin);
        } else {
            result = content.substring(begin, begin + 160) + "...";
        }
        return result;
    }

    /**
     * // TODO 按照我的想法改一下搜索功能
     * @param query
     * @return
     */
    public List<SearchResult> search(String query) {
        // 分词
        List<Term> terms = ToAnalysis.parse(query).getTerms();


        // 查倒排
        List<Weight> list = new ArrayList<>();
        for (Term term : terms) {
            String word = term.getName();
            List<Weight> inverted = index.getInverted(word);
            // 如果这个词在所有文档中不存在
            if (inverted == null || inverted.size() == 0) {
                continue;
            }
            list.addAll(inverted);
        }

        // 根据权重排序
        list.sort((o1, o2) -> {
            if ((o2.getWeight() > o1.getWeight())) {
                return 1;
            } else {
                return 0;
            }
        });



        // 查正排
        List<SearchResult> searchResults = new ArrayList<>();
        for (Weight weight : list) {
            int docId = weight.getDocId();
            Doc doc = index.getDoc(docId);
            // 根据正文生成摘要
            String description = generateDescription(doc.getContent(), terms);

            searchResults.add(new SearchResult(doc.getTitle(), doc.getUrl(), description));

        }
        return searchResults;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DocSearch search = new DocSearch();

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入您想查询的内容: ");
        String text = scanner.next();
        List<SearchResult> searchResults = search.search(text);
        for (SearchResult searchResult : searchResults) {
            System.out.println(searchResult);
        }
    }
}
