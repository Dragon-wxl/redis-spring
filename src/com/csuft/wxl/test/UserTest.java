package com.csuft.wxl.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.csuft.wxl.mapper.RoleMapper;
import com.csuft.wxl.mapper.UserMapper;
import com.csuft.wxl.pojo.Role;
import com.csuft.wxl.pojo.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserTest {
	@Autowired
	UserMapper userMapper;
	@Autowired
	RoleMapper roleMapper;

//	@Test
//	public void getAllUser() {
//		List<User> users = userMapper.selectAllUser();
//		for (User user : users) {
//			System.out.println(user);
//		}
//		User [id=1, name=zhang3, password=12345]
//		User [id=2, name=li4, password=abcde]
//	}
//	@Test
//	public void getUserById() {
//		String passwd=userMapper.selectUserById("zhang3");
//		System.out.println(passwd);
//	}
//	@Test
//	public void selectPersionWage() {
//		User user = userMapper.selectUserRolePermission("zhang3");
//		Set<String> roles = new HashSet<String>();
//		for (Role role : user.getRole()) {
//			roles.add(role.getName());
//		}
//		System.out.println(roles);
//	}
	@Test
	public void addUser() {
		User user=new User();
		user.setName("root");
		user.setPassword("1234");
		user.setSalt(new SecureRandomNumberGenerator().nextBytes().toString());
		String encodedPassword= new SimpleHash("md5",user.getPassword(),user.getSalt(),2).toString();
		user.setPassword(encodedPassword);
		
		String role=Role.ROLE_NAME.productManager.toString();
		
		try {
			int a=userMapper.insertUser(user);
			System.out.println(a);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			// TODO: handle exception
			System.out.println("用户已存在");
		}
		Map<String,Integer> userRoleMap= new HashMap<String, Integer>();
		int usetId=userMapper.selectUserByNameGetId(user.getName());
		int roleId=roleMapper.selectRoleByNameGetId(role);
		System.out.println("用户id:"+usetId+"\n角色id:"+roleId);
		userRoleMap.put("uid", usetId);
		userRoleMap.put("rid", roleId);
		try {
			int a=userMapper.insertUserRole(userRoleMap);
			System.out.println(a);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			// TODO: handle exception
			System.out.println("用户角色已存在");
		}
	}

	@Test
	public void getUser() {
		User user = userMapper.selectUserByNameGetUser("root");
		System.out.println(user);
	}

}
