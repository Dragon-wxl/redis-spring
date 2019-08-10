package com.csuft.wxl.lucene;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorld extends HttpServlet {
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String userName = (String) request.getSession().getAttribute("account");
		response.setContentType("text/html;charset=UTF-8");
		TestLucene testLucene = new TestLucene();
		ProductUtil productUtil=new ProductUtil();
		try {

			StringBuffer str = new StringBuffer();
			str.append("<pre>");
			str.append(productUtil.search());
			str.append("</pre>");
			response.getWriter().print(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
