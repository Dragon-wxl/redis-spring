package com.csuft.wxl.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.csuft.wxl.pojo.Produc;

public class ProductUtil {

	public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
		ProductUtil productUtil = new ProductUtil();
		StringBuffer str = productUtil.search();
		System.out.println(str);
	}

	public static Directory getIndex() throws IOException {

		String fileName = "C:\\Users\\Administrator\\Desktop\\练习\\140k_products.txt";
		File f = new File(fileName);
		List<String> lines = FileUtils.readLines(f, "UTF-8");
		List<Produc> producs = new ArrayList<Produc>();
		for (String string : lines) {
			String[] strings = string.split(",");
			Produc p = new Produc();
			p.setId(Integer.parseInt(strings[0]));
			p.setName(strings[1]);
			p.setCategory(strings[2]);
			p.setPrice(Float.parseFloat(strings[3]));
			p.setPlace(strings[4]);
			p.setCode(strings[5]);
			producs.add(p);
		}

		// 搜索
		// 1. 准备中文分词器
		IKAnalyzer analyzer = new IKAnalyzer();
		// 2. 索引 Directory
		Directory index = new RAMDirectory();
		System.out.println(index);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(index, config);
		int total = producs.size();
		System.out.println("total:" + total);
		int count = 0;
		int per = 0;
		int oldPer = 0;
		for (Produc p : producs) {
			Document doc = new Document();
			doc.add(new TextField("id", String.valueOf(p.getId()), Field.Store.YES));
			doc.add(new TextField("name", p.getName(), Field.Store.YES));
			doc.add(new TextField("category", p.getCategory(), Field.Store.YES));
			doc.add(new TextField("price", String.valueOf(p.getPrice()), Field.Store.YES));
			doc.add(new TextField("place", p.getPlace(), Field.Store.YES));
			doc.add(new TextField("code", p.getCode(), Field.Store.YES));
			writer.addDocument(doc);
			count++;
			per = count * 100 / total;
			if (per != oldPer) {
				oldPer = per;
				System.out.printf("索引中，总共要添加 %d 条记录，当前添加进度是： %d%% %n", total, per);
			}
		}
		writer.close();

		return index;
	}

	public StringBuffer search() throws IOException, ParseException, InvalidTokenOffsetsException {
		StringBuffer str = new StringBuffer();
		IKAnalyzer analyzer = new IKAnalyzer();

		Directory index = getIndex();

		int times = 0;

//			Scanner s = new Scanner(System.in);

//		while (true) {
		System.out.print("请输入查询关键字：");
//				String keyword = s.nextLine();
		String keyword = "手机";
		System.out.println("当前关键字是：" + keyword);
		Query query = new QueryParser("name", analyzer).parse(keyword);

		// 4. 搜索
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
//		int numberPerPage = 20;
		int pageNow = 854;
		int pageSize = 10;

		ScoreDoc[] hits = pageSearch1(query, searcher, pageNow, pageSize);
		;
//		ScoreDoc[] hits = searcher.search(query, numberPerPage).scoreDocs;

		// 5. 显示查询结果
		System.out.println("找到 " + hits.length + " 个命中.");

		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));

		System.out.println("找到 " + hits.length + " 个命中.");
		System.out.println("序号\t匹配度得分\t结果");
		for (int i = 0; i < hits.length; ++i) {
			ScoreDoc scoreDoc = hits[i];
			int docId = scoreDoc.doc;
			Document d = searcher.doc(docId);
			List<IndexableField> fields = d.getFields();
			System.out.print((i + 1));

			str.append((i + 1));

			System.out.print("\t" + scoreDoc.score);
			for (IndexableField f1 : fields) {

				if ("name".equals(f1.name())) {
					TokenStream tokenStream = analyzer.tokenStream(f1.name(), new StringReader(d.get(f1.name())));
					String fieldContent = highlighter.getBestFragment(tokenStream, d.get(f1.name()));
					System.out.print("\t" + fieldContent);

					str.append("\t" + fieldContent);

				} else {
					System.out.print("\t" + d.get(f1.name()));

					str.append("\t" + d.get(f1.name()));

				}
			}
			System.out.println("<br>");

			str.append("<br>\n");

		}
		// 6. 关闭查询
		reader.close();
//		}
		return str;
	}

	private static ScoreDoc[] pageSearch1(Query query, IndexSearcher searcher, int pageNow, int pageSize)
			throws IOException {
		TopDocs topDocs = searcher.search(query, pageNow * pageSize);
		TopDocs topDocs1 = searcher.search(query, 10);
		System.out.println(topDocs1.totalHits);
		System.out.println("查询到的总条数\t" + topDocs.totalHits);
		ScoreDoc[] alllScores = topDocs.scoreDocs;

		List<ScoreDoc> hitScores = new ArrayList<>();

		int start = (pageNow - 1) * pageSize;
		int end = pageSize * pageNow;
		for (int i = start; i < end; i++)
			hitScores.add(alllScores[i]);

		ScoreDoc[] hits = hitScores.toArray(new ScoreDoc[] {});
		return hits;
	}

	private static ScoreDoc[] pageSearch2(Query query, IndexSearcher searcher, int pageNow, int pageSize)
			throws IOException {
		int start = (pageNow - 1) * pageSize;
		if (0 == start) {
			TopDocs topDocs = searcher.search(query, pageNow * pageSize);
			return topDocs.scoreDocs;
		}
		// 查询数据， 结束页面自前的数据都会查询到，但是只取本页的数据
		TopDocs topDocs = searcher.search(query, start);
		// 获取到上一页最后一条

		ScoreDoc preScore = topDocs.scoreDocs[start - 1];
		// 查询最后一条后的数据的一页数据
		topDocs = searcher.searchAfter(preScore, query, pageSize);
		return topDocs.scoreDocs;

	}
}
