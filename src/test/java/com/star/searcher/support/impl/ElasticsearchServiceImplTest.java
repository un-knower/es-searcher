package com.star.searcher.support.impl;

import com.star.searcher.annotation.ESProperty;
import com.star.searcher.annotation.ESProperty.SearchMethod;
import com.star.searcher.annotation.ESSorter;
import com.star.searcher.support.ElasticsearchService;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-06 01:55:00
 */
public class ElasticsearchServiceImplTest {
    private ElasticsearchService elasticsearchService;

    @Before
    public void setUp() throws Exception {
        Settings settings = Settings.builder().build();
        elasticsearchService = new ElasticsearchServiceImpl(null);
    }

    @org.junit.Test
    public void search() {
        List<String> list = new ArrayList<>();
        Student student = new Student();
        student.setId(1);
        student.setName("cc");
        student.setBooks(Arrays.asList("a", "b"));
        elasticsearchService.search(list, list, student, 1, 2, Student.class);
    }

    @org.junit.Test
    public void search1() {
    }

    public static class Student extends Persion {
        @ESProperty(value = "id", method = SearchMethod.EQUAL)
        @ESSorter
        private int id;
        @ESProperty(value = "book", method = SearchMethod.EQUAL)
        private List<String> books;
        private List<Integer> scores;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getBooks() {
            return books;
        }

        public void setBooks(List<String> books) {
            this.books = books;
        }

        public List<Integer> getScores() {
            return scores;
        }

        public void setScores(List<Integer> scores) {
            this.scores = scores;
        }
    }

    public static class Persion {
        @ESProperty(value = "name", method = SearchMethod.LIKE)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}