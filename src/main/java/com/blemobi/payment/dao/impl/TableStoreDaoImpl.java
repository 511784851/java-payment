package com.blemobi.payment.dao.impl;

//github.com/blemobi/java-payment.git
import org.springframework.stereotype.Repository;

import com.alicloud.openservices.tablestore.SyncClient;
import com.blemobi.payment.dao.TableStoreDao;

/**
 * 阿里云表格存储操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("tableStoreDao")
public class TableStoreDaoImpl implements TableStoreDao {
	// ACCESSID = "LTAIqjq4OplpzZRS"
	// ACCESSKEY = "GbtpMcQHxTBElHLVwC4UDl1lSsmFK4"

	// "http://rongliang-test.cn-shenzhen.ots-internal.aliyuncs.com"
	// "http://rongliang-test.cn-shenzhen.ots-internal.aliyuncs.com"

	// "http://xingneng-test.cn-shenzhen.ots-internal.aliyuncs.com"
	// "http://xingneng-test.cn-shenzhen.ots-internal.aliyuncs.com"

	final String endPoint = "http://rongliang-test.cn-shenzhen.ots.aliyuncs.com";
	final String accessKeyId = "LTAIqjq4OplpzZRS";
	final String accessKeySecret = "GbtpMcQHxTBElHLVwC4UDl1lSsmFK4";
	final String instanceName = "rongliang-test";

	private SyncClient client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

	@Override
	public boolean existsByKey(String key, String member) {
		// TODO Auto-generated method stub

		return true;
	}
}