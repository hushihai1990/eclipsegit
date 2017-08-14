package com.itheima.luence.test;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.luence.dao.BookDao;
import com.itheima.luence.dao.BookDaoImpl;
import com.itheima.luence.domain.Book;

public class LuenceTest {
	
	@Test
	public void luenceTest() throws Exception {
		BookDao bookDao = new BookDaoImpl();
		List<Book> books = bookDao.queryBookList();
		
		//创建分词器
		//smartChinese分词器 弊端：无法扩展词库
//		Analyzer analyzer = new SmartChineseAnalyzer();
		//ik分词器 优点：可以自己管理词库
		Analyzer analyzer = new IKAnalyzer();
		//指定索引库所在目录 内存或者磁盘
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//indexWriterConfig 配置类
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		//获取写入流
		IndexWriter indexWriter = new IndexWriter(directory, config );
		
		for (Book book : books) {
			Document document = new Document();
			IndexableField idField = new StringField("id", String.valueOf(book.getId()), Store.YES);
			IndexableField nameField = new TextField("name", String.valueOf(book.getName()), Store.YES);
			IndexableField picField = new StoredField("pic", String.valueOf(book.getPic()));
			IndexableField priceField = new FloatField("price", book.getPrice(), Store.YES);
			TextField descField = new TextField("desc", String.valueOf(book.getDesc()), Store.YES);
			document.add(idField);
			document.add(nameField);
			document.add(picField);
			document.add(priceField);
			document.add(descField);
			if (4 == book.getId()) {
				descField.setBoost(100f);
			}
			//将一条文本对象写入索引库中
			indexWriter.addDocument(document);
		}
		//释放资源
		indexWriter.close();
	}
	
	
	@Test
	public void queryTest() throws Exception {
		//查询
		//指定索引库位置
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//获取读流
		IndexReader indexReader = DirectoryReader.open(directory);
		//创建索引搜索对象
		IndexSearcher searcher = new IndexSearcher(indexReader);
		//指定用何种分词器搜索
		Analyzer analyzer = new IKAnalyzer();
		//创建query转换器对象 指定所使用的分词器
		QueryParser parser = new QueryParser("name", analyzer);
		//将符合规则的字符串转换成Query对象
		Query query = parser.parse("name:塞北的雪");
		//获取评分最高（匹配最接近）的前五条
		TopDocs topDocs = searcher.search(query, 5);
		//获取仅带有id的搜索结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		for (ScoreDoc scoreDoc : scoreDocs) {
			//获取该条查询结果的id
			int doc = scoreDoc.doc;
			//根据id获取单条查询结果的全部数据
			Document document = searcher.doc(doc);
			System.out.println(document.get("id"));
			System.out.println(document.get("name"));
			System.out.println(document.get("pic"));
			System.out.println(document.get("price"));
			System.out.println(document.get("desc"));
		}
		
		indexReader.close();
		
	}
}
