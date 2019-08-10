package com.csuft.wxl.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;

import com.csuft.wxl.pojo.Produc;

public class SolrUtil {
	public static SolrClient client;
	private static String url;
	static {
		url = "http://localhost:8983/solr/how2j";
		client = new HttpSolrClient.Builder(url).build();
	}

	public static <T> boolean batchSaveOrUpdate(List<T> entities) throws SolrServerException, IOException {

		DocumentObjectBinder binder = new DocumentObjectBinder();
		int total = entities.size();
		int count = 0;
		for (T t : entities) {
			SolrInputDocument doc = binder.toSolrInputDocument(t);
			client.add(doc);
			System.out.printf("添加数据到索引中，总共要添加 %d 条记录，当前添加第%d条 %n", total, ++count);
		}
		client.commit();
		return true;
	}

	public static List<Produc> getList() throws IOException {
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
		return producs;
	}

	// 分页查询
	public static QueryResponse query(String keywords, int startOfPage, int numberOfPage)
			throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		query.setStart(startOfPage);
		query.setRows(numberOfPage);

		query.setQuery(keywords);
		QueryResponse rsp = client.query(query);
		return rsp;
	}
	private static void query(String keyword) throws SolrServerException, IOException {
        QueryResponse queryResponse = SolrUtil.query(keyword,0,10);
        SolrDocumentList documents= queryResponse.getResults();
        System.out.println("累计找到的条数："+documents.getNumFound());
        if(!documents.isEmpty()){
              
            Collection<String> fieldNames = documents.get(0).getFieldNames();
            for (String fieldName : fieldNames) {
                System.out.print(fieldName+"\t");
            }
            System.out.println();
        }
          
        for (SolrDocument solrDocument : documents) {
              
            Collection<String> fieldNames= solrDocument.getFieldNames();
              
            for (String fieldName : fieldNames) {
                System.out.print(solrDocument.get(fieldName)+"\t");
                  
            } 
            System.out.println();
              
        }
    }

	// 高亮
	public static void queryHighlight(String keywords) throws SolrServerException, IOException {
		SolrQuery q = new SolrQuery();
		// 开始页数
		q.setStart(0);
		// 每页显示条数
		q.setRows(10);
		// 设置查询关键字
		q.setQuery(keywords);
		// 开启高亮
		q.setHighlight(true);
		// 高亮字段
		q.addHighlightField("name");
		// 高亮单词的前缀
		q.setHighlightSimplePre("<span style='color:red'>");
		// 高亮单词的后缀
		q.setHighlightSimplePost("</span>");
		// 摘要最长100个字符
		q.setHighlightFragsize(100);
		// 查询
		QueryResponse query = client.query(q);
		// 获取高亮字段name相应结果
		NamedList<Object> response = query.getResponse();
		NamedList<?> highlighting = (NamedList<?>) response.get("highlighting");
		for (int i = 0; i < highlighting.size(); i++) {
			System.out.println(highlighting.getName(i) + "：" + highlighting.getVal(i));
		}

		// 获取查询结果
		SolrDocumentList results = query.getResults();
		for (SolrDocument result : results) {
			System.out.println(result.toString());
		}
	}

	//一个对象的增加或者更新
	public static <T> boolean saveOrUpdate(T entity) throws SolrServerException, IOException {
        DocumentObjectBinder binder = new DocumentObjectBinder();
        SolrInputDocument doc = binder.toSolrInputDocument(entity);
        client.add(doc);
        client.commit();
        return true;
    }
	//根据id删除这个索引
	public static boolean deleteById(String id) {
        try {
            client.deleteById(id);
            client.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	public static void main(String[] args) throws IOException, SolrServerException {
		// 提交到solr服务器
//		List<Produc> list=getList();
//		batchSaveOrUpdate(list);

		// 查询
//		QueryResponse queryResponse = SolrUtil.query("name:手机", 0, 10);
//		SolrDocumentList documents = queryResponse.getResults();
//		System.out.println("累计找到的条数：" + documents.getNumFound());
//		if (!documents.isEmpty()) {
//
//			Collection<String> fieldNames = documents.get(0).getFieldNames();
//			for (String fieldName : fieldNames) {
//				System.out.print(fieldName + "\t");
//			}
//			System.out.println();
//		}
//
//		for (SolrDocument solrDocument : documents) {
//
//			Collection<String> fieldNames = solrDocument.getFieldNames();
//
//			for (String fieldName : fieldNames) {
//				System.out.print(solrDocument.get(fieldName) + "\t");
//
//			}
//			System.out.println();
//		}
		
		//高亮查询
//		queryHighlight("name:手机");
		
		String keyword = "name:鞭";
        System.out.println("修改之前");
        query(keyword);
//        
//        Produc p = new Produc();
//        p.setId(51173);
//        p.setName("修改后的神鞭");
//        SolrUtil.saveOrUpdate(p);
//        System.out.println("修改之后");
//        query(keyword);
//        
//        SolrUtil.deleteById("51173");
//        System.out.println("删除之后");
//        query(keyword);
	}
}
