package com.blemobi.payment.dao.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.blemobi.payment.dao.TableStoreDao;
import com.google.common.base.Strings;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 阿里云表格存储操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("tableStoreDao")
public class TableStoreDaoImpl implements TableStoreDao {
	@Override
	public boolean existsByKey(String tn, String key, String uuid) throws IOException {
		String url = "http://localhost:9015/v1/tablestore/exists?tn=" + tn + "&key=" + key + "&uuid=" + uuid;
		JSONObject jsonObject = call(url);
		return jsonObject.getBooleanValue("bool");
	}

	@Override
	public String[] selectByKey(String tn, String key) throws IOException {
		String url = "http://127.0.0.1:9015/v1/tablestore/find-row?tn=" + tn + "&key=" + key;
		JSONObject jsonObject = call(url);
		String[] arr = null;
		Integer count = jsonObject.getInteger("count");
		String uuid = jsonObject.getString("uuid");
		if (count != null && count > 0 && !Strings.isNullOrEmpty(uuid))
			arr = new String[] { count + "", uuid };

		return arr;
	}

	private JSONObject call(String url) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).get().build();
		Response resp = client.newCall(request).execute();
		JSONObject json = null;
		if (resp.isSuccessful()) {
			String body = resp.body().string();
			json = JSONObject.parseObject(body);
			System.out.println(json);
		} else {
			throw new RuntimeException("访问表格存储服务失败");
		}
		return json;
	}
}