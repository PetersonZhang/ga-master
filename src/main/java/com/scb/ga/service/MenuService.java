package com.scb.ga.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.scb.ga.dao.jdbc.MenuRepository;
import com.scb.ga.dao.jdbc.RoleRepository;
import com.scb.ga.domain.Menu;
import com.scb.ga.domain.TreeModel;
import com.scb.ga.util.SecurityUtil;

/**
 * @author David Wang
 *
 */
@Service
@CacheConfig(cacheNames = "menulist")
public class MenuService {

	@Autowired
	protected MenuRepository menuRepository;

	@Autowired
	protected RoleRepository roleRepository;

	/* cache操作相关的注解中key都是spel表达式，字符串需要用'' */
	@Caching(put = @CachePut(key = "#menu.id"), evict = { @CacheEvict(key = "'list'"),
			@CacheEvict(value = "user-nav-menu", allEntries = true) })
	public Menu create(Menu menu) {
		validate(menu);
		menuRepository.add(menu);
		return menu;
	}

	@Caching(put = @CachePut(key = "#menu.id"), evict = { @CacheEvict(key = "'list'"),
			@CacheEvict(value = "user-nav-menu", allEntries = true) })
	public Menu modify(Menu menu) {
		validate(menu);
		menuRepository.update(menu);
		return menu;
	}

	@Cacheable
	public Menu get(String code) {
		return menuRepository.get(code);
	}

	@Caching(evict = { @CacheEvict(key = "'list'"), @CacheEvict(key = "#code"),
			@CacheEvict(value = "user-nav-menu", allEntries = true) })
	public void delete(String code) {
		roleRepository.removeRoleMenuByMenuId(code);
		menuRepository.remove(code);
	}

	@Cacheable(key = "'list'")
	public List<Menu> list() {
		List<Menu> list = menuRepository.list();
		TreeModel.sortByTree(list);
		return list;
	}

	@Caching(evict = { @CacheEvict(key = "'list'"), @CacheEvict(key = "#menu"),
			@CacheEvict(value = "user-nav-menu", allEntries = true) })
	public void switchStatus(String menu, boolean disable) {
		menuRepository.switchStatus(menu, disable);
	}

	// @Cacheable(value = "user-nav-menu")
	public List<Menu> getNavMenus(String uid) {
		List<Menu> list = null;
		if (SecurityUtil.isRoot()) {
			list = menuRepository.list();
		} else {
			list = menuRepository.getNavMenus(uid);
		}
		return (List<Menu>) TreeModel.buildTree(list);
	}

	private void validate(Menu menu) {
		Assert.hasText(menu.getId());
		Assert.hasText(menu.getLabel());
	}

}
