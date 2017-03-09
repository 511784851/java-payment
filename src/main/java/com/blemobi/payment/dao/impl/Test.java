package com.blemobi.payment.dao.impl;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.ListTableResponse;

public class Test {
	final static String endPoint = "http://rongliang-test.cn-shenzhen.ots.aliyuncs.com";
	final static String accessKeyId = "LTAIqjq4OplpzZRS";
	final static String accessKeySecret = "GbtpMcQHxTBElHLVwC4UDl1lSsmFK4";
	final static String instanceName = "rongliang-test";

	private static SyncClient client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ListTableResponse response = client.listTable();
	    System.out.println("表的列表如下：");
	    for (String tableName : response.getTableNames()) {
	        System.out.println(tableName);
	    }
	}

}
