package com.csuft.wxl.test;

import com.csuft.wxl.pojo.Role;
import com.csuft.wxl.pojo.User;

public class Test {
	public static void main(String[] args) {
//		List<String> str=new ArrayList<String>();
//		str.add("世界");
//		str.add("世界");
//		Set<String> set=new HashSet(str);
//		set.add("世界 ");
//		System.out.println(set);
		
		//加随机数的加密
//		String password="123";
//		String salt=new SecureRandomNumberGenerator().nextBytes().toString();
//		int times=2;
//		String algoithmName="md5";
//		
//		String encodedPassword=new SimpleHash(algoithmName,password,salt,times).toString();
//		System.out.println("原始密码："+password);
//		System.out.println("salt:"+salt);
//		System.out.println("times:"+times);
//		System.out.println("encodedPassword:"+encodedPassword);
		
		Role user=new Role();
		String a=Role.ROLE_NAME.admin.toString();
		System.out.println(a);
	}
}
