package com.xiaohe.tryansjseg;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
        String str = "小明是清华大学的学生,毕业后去美团工作，一单5.5元";
        Result parse = ToAnalysis.parse(str);
        System.out.println(parse);
        // term表示一个分词结果
        List<Term> terms = parse.getTerms();

        for (Term term : terms) {
            System.out.println("分词结果:" + term.getName());
        }
        System.out.println(terms);


    }

}
