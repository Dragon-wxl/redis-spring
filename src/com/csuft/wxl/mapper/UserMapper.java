package com.csuft.wxl.mapper;

import java.util.List;
import java.util.Map;

import com.csuft.wxl.pojo.User;

public interface UserMapper {
	public List<User> selectAllUser();

	public String selectUserByIdGetPassword(String name);

	public User selectUserRolePermission(String name);

	public int selectUserByNameGetId(String name);

	public User selectUserByNameGetUser(String name);

	public int insertUser(User user);

	public int insertUserRole(Map<String, Integer> map);

}
