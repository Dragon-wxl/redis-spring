package com.csuft.wxl.Controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//专门用于显示页面的控制器
@Controller
@RequestMapping("")
public class PageController {

	@RequestMapping("index")
	public String index() {
		return "index";
	}

	@RequiresPermissions("deleteOrder")
	@RequestMapping("deleteOrder")
	public String deleteOrder() {
		return "deleteOrder";
	}

	@RequiresRoles("admin")
	@RequestMapping("deleteProduct")
	public String deleteProduct() {
		return "deleteProduct";
	}

	@RequestMapping("listProduct")
	public String listProduct() {
		return "listProduct";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String name, String password) {
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(name, password);
		try {
			subject.login(token);
			Session session = subject.getSession();
			session.setAttribute("subject", subject);
			return "redirect:index";

		} catch (AuthenticationException e) {
			model.addAttribute("error", "验证失败");
			return "login";
		}
	}

	@RequestMapping("unauthorized")
	public String noPerms() {
		return "unauthorized";
	}

}
