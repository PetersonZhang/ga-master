package com.scb.ga.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scb.ga.security.SecurityUser;
import com.scb.ga.service.MenuService;
import com.scb.ga.util.SecurityUtil;

/**
 * @author David Wang
 *
 */
@ControllerAdvice
public class PublicAdvice {

	@Autowired
	protected MenuService menuService;

	@ExceptionHandler
	public void handleControllerException(HttpServletRequest request, HttpServletResponse response, Throwable ex)
			throws IOException {
		ex.printStackTrace();
		String ajax = request.getHeader("X-Requested-With");
		response.setCharacterEncoding("utf-8");
		if (StringUtils.isBlank(ajax)) {
			response.sendRedirect("/error");
		} else {
			response.getWriter().println("出错了:" + ex.getMessage());
		}

	}

	@ModelAttribute
	public void addCommonModel(Model model, HttpServletRequest request) {
		SecurityUser user = SecurityUtil.getUser();
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("navs", menuService.getNavMenus(user.getUid()));
		}
	}

}
