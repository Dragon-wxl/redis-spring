package com.csuft.wxl.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.Account;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.csuft.wxl.mapper.UserMapper;
import com.csuft.wxl.pojo.Permission;
import com.csuft.wxl.pojo.Role;
import com.csuft.wxl.pojo.User;

public class DatabaseRealm extends AuthorizingRealm {
	@Autowired
	UserMapper userMapper;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		// TODO Auto-generated method stub
		// 能进入到这里，表示账号已经通过验证了
		String userName = (String) principalCollection.getPrimaryPrincipal();
		// 通过DAO获取角色和权限
		User user = userMapper.selectUserRolePermission(userName);
		Set<String> permissions = new HashSet<String>();
		for (Permission role : user.getPermission()) {
			permissions.add(role.getName());
		}
		Set<String> roles = new HashSet<String>();
		for (Role role : user.getRole()) {
			roles.add(role.getName());
		}

		// 授权对象
		SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
		// 把通过DAO获取到的角色和权限放进去
		s.setStringPermissions(permissions);
		s.setRoles(roles);
		return s;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// TODO Auto-generated method stub
		UsernamePasswordToken t = (UsernamePasswordToken) token;
		String userName = token.getPrincipal().toString();
		String password = new String(t.getPassword());
//		String passwordInDB = userMapper.selectUserByIdGetPassword(userName);

		User user=userMapper.selectUserByNameGetUser(userName);
		String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        String passwordEncoded = new SimpleHash("md5",password,salt,2).toString();
        System.out.println(password);
        System.out.println(passwordInDB);
//        如果为空就是账号不存在，如果不相同就是密码错误，但是都抛出AuthenticationException，而不是抛出具体错误原因，免得给破解者提供帮助信息
		if (null == passwordInDB || !passwordInDB.equals(passwordEncoded))
			throw new AuthenticationException();
		// 认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
		SimpleAuthenticationInfo a = new SimpleAuthenticationInfo(userName, password, getName());
		return a;
	}

}
