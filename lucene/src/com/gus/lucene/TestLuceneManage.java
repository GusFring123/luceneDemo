package com.gus.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class TestLuceneManage {

    public IndexWriter getIndexWriter() throws IOException {
        Path path = new File("D:\\temp\\index").toPath();
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig();
        return new IndexWriter(directory, config);
    }

    //删除全部
    @Test
    public void deleteAll() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }

    //按条件删除
    @Test
    public void deleteByQuery() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Query query = new TermQuery(new Term("fileName", "apache"));
        Query query2 = new TermQuery(new Term("fileName", "java"));
        indexWriter.deleteDocuments(query, query2);
        indexWriter.close();
    }

    //修改索引
    @Test
    public void updateIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Document document = new Document();
        Field fileNameField = new TextField("fileN", "java", Field.Store.YES);
        Field fileContentField = new TextField("fileC", "java2", Field.Store.YES);
        document.add(fileNameField);
        document.add(fileContentField);
        indexWriter.updateDocument(new Term("fileName", "apache"), document);
    }

    public IndexSearcher getIndexSeacher() throws Exception {
        Path path = new File("d:\\temp\\index").toPath();
        Directory directory = FSDirectory.open(path);
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher;
    }

    //查询所有
    @Test
    public void searchAll() throws Exception {
        IndexSearcher indexSearcher = getIndexSeacher();
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = indexSearcher.search(query, 20);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            System.out.println(document.get("fileName"));
        }
        indexSearcher.getIndexReader().close();
    }


    //根据数值范围查询
    @Test
    public void testNumericRangeQuery() throws Exception {
        IndexSearcher indexSearcher = getIndexSeacher();
        Query query = LegacyNumericRangeQuery.newLongRange("fileSize", 20L, 300L, true, true);
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("fileSize"));
            System.out.println(document.get("filePath"));
        }
        indexSearcher.getIndexReader().close();
    }

    //组合条件查询
//    @Test
//    public void testBooleanQuery() throws Exception {
//        IndexSearcher indexSearcher = getIndexSeacher();
//        BooleanQuery booleanQuery = new BooleanQuery();
//        Query query1 = new TermQuery(new Term("fileName","apache"));
//        Query query2 = new TermQuery(new Term("fileName","lucene"));
//
//
//    }
    //解析查询
    @Test
    public void testQueryParser() throws Exception {
        IndexSearcher indexSearcher = getIndexSeacher();
        QueryParser queryParser = new QueryParser("fileContent", new StandardAnalyzer());
        Query query = queryParser.parse("lucene and java");
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("fileSize"));
            System.out.println(document.get("filePath"));
        }
        indexSearcher.getIndexReader().close();
    }

    //解析多默认查询
    @Test
    public void testMultiFieldQueryParser() throws Exception {
        IndexSearcher indexSearcher = getIndexSeacher();
        String[] fields = {"fileName", "fileContnt"};
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
        Query query = queryParser.parse("lucene and apache");
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("fileSize"));
            System.out.println(document.get("filePath"));
        }
        indexSearcher.getIndexReader().close();
    }

}
