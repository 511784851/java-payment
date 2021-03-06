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
		tokenPathArray.add("/v1/payment/user/thirdToken");
		tokenPathArray.add("/v1/payment/redEnve/send-ordin");
		tokenPathArray.add("/v1/payment/redEnve/send-group");
		tokenPathArray.add("/v1/payment/reward/send");
		tokenPathArray.add("/v1/payment/redEnve/status");
		tokenPathArray.add("/v1/payment/redEnve/receive");
		tokenPathArray.add("/v1/payment/redEnve/info");
		tokenPathArray.add("/v1/payment/redEnve/receive-list");
		tokenPathArray.add("/v1/payment/bill/info-list");
		tokenPathArray.add("/v1/payment/redEnve/send-list");
		tokenPathArray.add("/v1/payment/reward/list");
		tokenPathArray.add("/v1/payment/reward/info-list");

		fromPathArray = new ArrayList<String>();
		// fromPathArray.add("/*");
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
