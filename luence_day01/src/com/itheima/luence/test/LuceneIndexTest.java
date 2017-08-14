package com.itheima.luence.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.QueryTemplateManager;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneIndexTest {

	public IndexReader getIndexReader() throws Exception{
		//ָ��������λ��
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//��ȡ����
		IndexReader indexReader = DirectoryReader.open(directory);
		//����������������
		return indexReader;
		
	}
	
	public IndexWriter getIndexWriter() throws Exception{
		Analyzer analyzer = new IKAnalyzer();
		//ָ������������Ŀ¼ �ڴ���ߴ���
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//indexWriterConfig ������
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		//��ȡд����
		IndexWriter indexWriter = new IndexWriter(directory, config );
		return indexWriter;
	}
	
	@Test
	public void testAddIndex() throws Exception{
		Document document = new Document();
		IndexableField field1 = new StoredField("id", "1");
		IndexableField field2 = new FloatField("price", 11111f,Store.YES);
		IndexableField field3 = new TextField("desc", "�򻯲�������",Store.YES);
		document.add(field1);
		document.add(field2);
		document.add(field3);
		Analyzer analyzer = new IKAnalyzer();
		//ָ������������Ŀ¼ �ڴ���ߴ���
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//indexWriterConfig ������
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		//��ȡд����
		IndexWriter indexWriter = new IndexWriter(directory, config );
		indexWriter.updateDocument(new Term("id", "1"), document);
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
		//���ֲ�ѯ
		/*NumericRangeQuery query = NumericRangeQuery.newFloatRange("price", 70f, 80f, true, true);
		TopDocs topDocs = searcher.search(query, 1);*/
		//������ѯ
		/*BooleanQuery query = new BooleanQuery();
		Term t1 = new Term("name", "apache");
		Term t2 = new Term("name", "lucene");
		TermQuery query1 = new TermQuery(t1);
		TermQuery query2 = new TermQuery(t2);
		query.add(query1, Occur.MUST);
		query.add(query2, Occur.MUST);*/
		//��������ѯ
//		QueryParser parser = new QueryParser("name", analyzer);
//		Query query = parser.parse("name:apache AND name:lucene");
		//��������ѯ
	    MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"name","id"}, analyzer);
		Query query = parser.parse("name:apache OR id:3");
		
		TopDocs topDocs = searcher.search(query,5);
		//��ѯ��������
//		int totalHits = topDocs.totalHits;
     	print(searcher, topDocs);
		indexReader.close();
	}
	
	public void print(IndexSearcher searcher,TopDocs topDocs) throws Exception{
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
	}
}
