package com.blemobi.payment.core;

import java.util.ArrayList;
import java.util.List;

import com.blemobi.library.filter.FromFilter;
import com.blemobi.library.jetty.ServerFilter;
import com.blemobi.payment.filter.TokenFilter;

/**
 * 服务启动过滤器配置
 * 
 * @author zhaoyong
 *
 */
public class FilterProperty {
	/**
	 * 需要验证用户uuid&token的path，对应filter：JettyFilter
	 */
	private List<String> tokenPathArray;

	/**
	 * 需要验证调用服务的path，对应filter：FromFilter
	 */
	private List<String> fromPathArray;

	/**
	 * 构造方法
	 */
	public FilterProperty() {
		tokenPathArray = new ArrayList<String>();
		// tokenPathArray.add("/chat/user/token");

		fromPathArray = new ArrayList<String>();
		// fromPathArray.add("/room/update/tag");
	}

	/**
	 * 获取要配置过滤器的路径
	 * 
	 * @return List<ServerFilter>
	 */
	public List<ServerFilter> getFilterList() {
		List<ServerFilter> serverFilterList = new ArrayList<ServerFilter>();

		if (tokenPathArray != null) {
			serverFilterList.add(new ServerFilter(new TokenFilter(), tokenPathArray));
		}
		if (fromPathArray != null) {
			serverFilterList.add(new ServerFilter(new FromFilter(), fromPathArray));
		}

		return serverFilterList;
	}
}
