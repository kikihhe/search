package com.xiaohe.search2.utils;


import com.xiaohe.search2.domain.Doc;
import com.xiaohe.search2.domain.SearchResult;
import com.xiaohe.search2.domain.Weight;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-04-13 19:53
 */

public class DocSearch {

    private static Index index = new Index();
    private static final String STOP_WORDS_PATH = "D:\\JAVA-projects\\search\\stopWords" ;
    private HashSet<String> stopWords = new HashSet<>();


    public DocSearch() throws IOException, InterruptedException {
        // 加载索引
        index.load();
        // 加载暂停词表
        loadStopWords();
    }
    public List<String> generateDescription(String content, List<String> terms) {
        // 返回结果
        List<String> result = new ArrayList<>();

        // 去除普通标签和script标签
        content = content.replaceAll("<script.*?>(.*?)</script>", "");
        content = content.replaceAll("<.*?>", "");
        int pos = -1;
        while (pos < content.length()) {
            String w = "";
            for (String word : terms) {
                w = word;
                pos = content.toLowerCase(Locale.ROOT).indexOf(word, pos+1);
                if (pos >= 0) {
                    break;
                }
            }
            // 遍历完也没有找到, 所有分词结果都没有
            if (pos == -1) {
               return result;
            }
            // 从pos向前找10个字符
            int begin = pos < 10 ? 0 : pos - 10;
            int end = Math.min(pos + w.length() + 10, content.length());
            String resultString = "";

            resultString = content.substring(begin, end);

            for (String word : terms) {
                // 全字匹配，不能把ArrayList中的List标红
                // (?i): 表示不区分大小写
                resultString = resultString.replaceAll(word, "<i>" + word + "</i>");
            }
            result.add(resultString);
        }
        return result;
    }

    /**
     * 对查询的关键词进行分词
     * @return
     */
    public List<String> participle(String content) {

        // 加载停用分词
        StopRecognition stopRecognition = new StopRecognition();
        stopRecognition.insertStopWords(stopWords);

        // 开始分词
        List<Term> oldTerms = ToAnalysis.parse(content).recognition(stopRecognition).getTerms();
        List<String> words = new ArrayList<>();

//        // 使用暂停词表过滤分词结果
//        for (Term term : oldTerms) {
//            if (!stopWords.contains(term.getName())) {
//                words.add(term.getName());
//            }
//        }
        oldTerms.forEach(item -> {
            words.add(item.getName());
        });
        return words;
    }

    /**
     * @param
     * @return
     */
    public List<SearchResult> search(List<String> words) {
        // 查倒排
        List<List<Weight>> termResult = new ArrayList<>();
        for (String word : words) {
            List<Weight> inverted = index.getInverted(word);
            // 如果这个词在所有文档中不存在
            if (inverted == null || inverted.size() == 0) {
                continue;
            }
            termResult.add(inverted);
        }
        // 如果使用ArrayList查询，可能查出来一个文档既包含 array 又包含list, 那么他就会重复
        // 一个Collections 计算的是 array的权重
        // 另一个 Collections 计算的是 List 的权重
        // 现在就要对这个文档去重、合并权重
        List<Weight> list = mergeResult(termResult);
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
            List<String> description = generateDescription(doc.getContent(), words);

            searchResults.add(new SearchResult(doc.getTitle(), doc.getUrl(), description));

        }
        return searchResults;
    }

    // 针对重复文档进行去重、权重合并
    private List<Weight> mergeResult(List<List<Weight>> termResult) {
        // 描述元素在二维数组中的位置
        class Pos {
            public int row;
            public int col;
            public Pos(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }
        // 1. 将每一行按照docID升序排序
        for (List<Weight> list : termResult) {
            Collections.sort(list);
        }
        // 2. 借助一个优先队列进行合并
        List<Weight> result = new ArrayList<>();

        PriorityQueue<Pos> queue = new PriorityQueue<>(new Comparator<Pos>() {
            @Override
            public int compare(Pos o1, Pos o2) {
                // 根据pos纵坐标找到对应的List<Weight>, 再根据pos的横坐标找到对应的Weight
                Weight weight1 = termResult.get(o1.row).get(o1.col);
                Weight weight2 = termResult.get(o2.row).get(o2.col);
                return weight1.getDocId() - weight2.getDocId();
            }
        });
        // 把每一行的第一个元素插入
        for (int i = 0; i < termResult.size(); i++) {
            queue.offer(new Pos(i, 0));
        }
        while(!queue.isEmpty()) {
            Pos pos = queue.poll();
            Weight curWeight = termResult.get(pos.row).get(pos.col);
            // 查看result中是否已经有这个weight, 如果没有直接插入
            if (result.size() == 0) {
                result.add(curWeight);
            } else {
                Weight lastWeight = result.get(result.size() - 1);
                // 如果此docID已存在，直接将权重相加
                if (lastWeight.getDocId() == curWeight.getDocId()) {
                    lastWeight.setWeight(lastWeight.getWeight() +curWeight.getWeight());
                } else {
                    // 如果不存在，直接插入
                    result.add(curWeight);
                }
            }
            // 将下标移到这一行的下一个Weight
            Pos newPos = new Pos(pos.row, pos.col+1);
            if (newPos.col >= termResult.get(pos.row).size()) {
                continue;
            }
            // 把新的pos加入堆
            queue.offer(newPos);
        }
        return result;
    }

    // 加载停用词
    public void loadStopWords() {
        stopWords.add(" ");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(STOP_WORDS_PATH))) {
            while (true) {
                String fileLine = bufferedReader.readLine();
                if (Strings.isEmpty(fileLine)) {
                    return;
                }
                stopWords.add(fileLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
