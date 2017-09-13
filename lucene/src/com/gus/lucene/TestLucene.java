package com.gus.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class TestLucene {
    @Test
    public void createIndex() throws IOException {
//        第一步：创建一个java工程，并导入jar包。
//        第二步：创建一个indexwriter对象。
//        1）指定索引库的存放位置Directory对象
//        2）指定一个分析器，对文档内容进行分析。
        Path path = new File("d:\\temp\\index").toPath();
        Directory directory = FSDirectory.open(path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //原始文档路径
        File dir = new File("F:\\web\\【阶段15】luncene、Solr使用\\Lucene&solr\\01.参考资料\\searchsource");

        for (File file : dir.listFiles()) {
//        第三步：创建field对象，将field添加到document对象中。

            //文件名
            String fileName = file.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(file, "UTF-8");
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(file);

            //创建文件名域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储

            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
            Field filePathField = new StoredField("filePath", filePath);
            Field fileSizeField = new LegacyLongField("fileSize", fileSize, Field.Store.YES);
//        第二步：创建document对象。
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);
//        第四步：使用indexwriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }
//        第五步：关闭IndexWriter对象。
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws Exception {

        //指定索引库存放的路径
        Path path = new File("d:\\temp\\index").toPath();
        Directory dictionary = FSDirectory.open(path);
        //创建indexReader对象
        IndexReader indexReader = DirectoryReader.open(dictionary);
        //创建indexsearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询
        Term testTerm = new Term("fileName", "apache");
        Query query = new TermQuery(testTerm);
        //执行查询
        //第一个参数是查询对象，第二个参数是查询结果返回的最大值
        TopDocs topDocs = indexSearcher.search(query, 2);
        //查询结果的总条数
        System.out.println(topDocs.totalHits);
        //遍历查询结果
        //topDocs.scoreDocs存储了document对象的id
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //scoreDoc.doc属性就是document对象的id
            //根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("filePath"));
            System.out.println(document.get("fileSize"));
        }
        //关闭indexreader对象
        indexReader.close();
    }

    //查看标准分析器的分词效果
    @Test
    public void testTokenStream() throws Exception {
        //创建一个标准分析器对象
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new StandardAnalyzer();
        //获得tokenStream对象
        //第一个参数：域名，可以随便给一个
        //第二个参数：要分析的文本内容
//        TokenStream tokenStream = analyzer.tokenStream("test", "The Spring Framework provides a comprehensive programming and configuration model.");
        TokenStream tokenStream = analyzer.tokenStream("test", "我是一个高富帅，她是一个白富美，我想将她娶进门，然后一起环游世界。");
        //添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()) {
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //取关键词
            System.out.println(charTermAttribute);
            //结束位置
            System.out.println("end->" + offsetAttribute.endOffset());
        }
        tokenStream.close();
    }

}
