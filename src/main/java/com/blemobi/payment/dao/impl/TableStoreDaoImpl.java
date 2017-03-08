package com.blemobi.payment.dao.impl;

import org.springframework.stereotype.Repository;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.blemobi.payment.dao.TableStoreDao;

/**
 * 阿里云表格存储操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("tableStoreDao")
public class TableStoreDaoImpl implements TableStoreDao {
	final static String endPoint = "http://rongliang-test.cn-shenzhen.ots.aliyuncs.com";
	final static String accessKeyId = "LTAIqjq4OplpzZRS";
	final static String accessKeySecret = "GbtpMcQHxTBElHLVwC4UDl1lSsmFK4";
	final static String instanceName = "rongliang-test";

	private static SyncClient client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

	@Override
	public boolean existsByKey(String tableName, String key, String member) {
		SingleRowQueryCriteria criteria = buildCriteria(tableName, key);
		// 设置读取某些列
		criteria.addColumnsToGet(member);
		GetRowResponse getRowResponse = client.getRow(new GetRowRequest(criteria));
		Row row = getRowResponse.getRow();
		return !(row == null);
	}

	@Override
	public Row selectByKey(String tableName, String key) {
		SingleRowQueryCriteria criteria = buildCriteria(tableName, key);
		GetRowResponse getRowResponse = client.getRow(new GetRowRequest(criteria));
		Row row = getRowResponse.getRow();
		return row;
	}

	/**
	 * 读取条件
	 * 
	 * @param tableName
	 *            表名称
	 * @param key
	 *            行的key
	 * @return
	 */
	private SingleRowQueryCriteria buildCriteria(String tableName, String key) {
		// 构造主键
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
		primaryKeyBuilder.addPrimaryKeyColumn("key", PrimaryKeyValue.fromString(key));
		PrimaryKey primaryKey = primaryKeyBuilder.build();
		// 读一行
		SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
		// 设置读取最新版本
		criteria.setMaxVersions(1);
		return criteria;
	}
}