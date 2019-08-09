package com.csuft.wxl;

import redis.clients.jedis.Jedis;

public class TestJedis {
	public static void main(String[] args) {
		Jedis jedis=new Jedis("localHost");
		jedis.set("fot", "bar");
		String value=jedis.get("fot");
		System.out.println(value);
	}
}
