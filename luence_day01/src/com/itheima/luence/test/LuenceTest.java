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
		
		//�����ִ���
		//smartChinese�ִ��� �׶ˣ��޷���չ�ʿ�
//		Analyzer analyzer = new SmartChineseAnalyzer();
		//ik�ִ��� �ŵ㣺�����Լ�����ʿ�
		Analyzer analyzer = new IKAnalyzer();
		//ָ������������Ŀ¼ �ڴ���ߴ���
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//indexWriterConfig ������
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		//��ȡд����
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
			//��һ���ı�����д����������
			indexWriter.addDocument(document);
		}
		//�ͷ���Դ
		indexWriter.close();
	}
	
	
	@Test
	public void queryTest() throws Exception {
		//��ѯ
		//ָ��������λ��
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//��ȡ����
		IndexReader indexReader = DirectoryReader.open(directory);
		//����������������
		IndexSearcher searcher = new IndexSearcher(indexReader);
		//ָ���ú��ִַ�������
		Analyzer analyzer = new IKAnalyzer();
		//����queryת�������� ָ����ʹ�õķִ���
		QueryParser parser = new QueryParser("name", analyzer);
		//�����Ϲ�����ַ���ת����Query����
		Query query = parser.parse("name:������ѩ");
		//��ȡ������ߣ�ƥ����ӽ�����ǰ����
		TopDocs topDocs = searcher.search(query, 5);
		//��ȡ������id���������
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		for (ScoreDoc scoreDoc : scoreDocs) {
			//��ȡ������ѯ�����id
			int doc = scoreDoc.doc;
			//����id��ȡ������ѯ�����ȫ������
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
