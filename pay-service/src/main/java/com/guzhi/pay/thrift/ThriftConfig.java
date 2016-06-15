/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.thrift;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingFactorgbean;

import com.duowan.pooling.IObjectPoolManager;
import com.duowan.pooling.InitalizableObjectPool;
import com.duowan.pooling.PoolConfig;
import com.duowan.pooling.impl.InspectableBalanceController;
import com.duowan.pooling.impl.WeightedObjectPoolManager;
import com.duowan.pooling.thrift.TFramedTransportFactory;
import com.duowan.pooling.thrift.TSocketConfig;
import com.duowan.pooling.thrift.ThriftClientFactory;
import com.guzhi.pay.thrift.gbpay.gbPayClientValidator;

/**
 * @author administrator
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ThriftConfig {

    /** 客户端的类名 **/
    private String clientClassName;

    // pool的默认配置
    private int minIdle = 5;
    private int maxActive = 20;
    private int maxIdle = -1;
    // 检查空闲连接检查一次(1分钟)
    private int timeBetweenEvictionRunsMillis = 60000;
    private boolean testWhileIdle = true;
    // 隔多久检查连接池时候可用(默认配置为1分钟)
    private int checkPoolMills = 60000;

    // 连接池的验证器
    private AbstractClientValidator validator;
    // (默认为包名最后一个单词)
    private String validatatorName = "";

    // 第一个连接池的配置（poolname1 默认为包名最后一个 + 类名 + 1）
    private String poolName1 = "";
    private int poolWeight1 = 1;
    private String host1;
    private int port1;
    // 注意:该时间设置过短,会导致ping的方法超时
    private int soTimeout1 = 5000;

    // 第二个连接池的配置（poolname2 默认为包名最后一个 + 类名 + 2）
    private String poolName2 = "";
    private int poolWeight2 = 0;
    private String host2;
    private int port2;
    // 注意:该时间设置过短,会导致ping的方法超时
    private int soTimeout2 = 5000;

    private ThriftClientFactory factory;

    protected static final Logger LOG = LoggerFactory.getLogger(ThriftConfig.class);

    public void init() throws Exception {

        try {
            // 1.连接池的通用设置
            PoolConfig config = new PoolConfig();
            config.setMinIdle(minIdle);
            config.setMaxActive(maxActive);
            config.setMaxIdle(maxIdle);
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            config.setTestWhileIdle(testWhileIdle);

            // 2.thrife的客户端设置
            InspectableBalanceController balance = new InspectableBalanceController(checkPoolMills);
            IObjectPoolManager objPoolMgr = new WeightedObjectPoolManager(balance);

            // 3.连接池的有效验证器
            validator.setPoolManager(objPoolMgr);
            validator.setPoolName(getValidatatorName());

            // 4.第一个连接池
            TSocketConfig config1 = new TSocketConfig(host1, port1, soTimeout1);
            TFramedTransportFactory factory1 = new TFramedTransportFactory(config1, validator);
            InitalizableObjectPool pool1 = new InitalizableObjectPool(factory1, config);

            // 5.第二个连接池
            TSocketConfig config2 = new TSocketConfig(host2, port2, soTimeout2);
            TFramedTransportFactory factory2 = new TFramedTransportFactory(config2, validator);
            InitalizableObjectPool pool2 = new InitalizableObjectPool(factory2, config);

            // 6.权重管理
            MethodInvokingFactorgbean methodBean1 = new MethodInvokingFactorgbean();
            methodBean1.setTargetObject(objPoolMgr);
            methodBean1.setTargetMethod("addPool");
            methodBean1.setArguments(new Object[] { getPoolName1(), pool1, poolWeight1 });
            methodBean1.prepare();
            methodBean1.invoke();

            MethodInvokingFactorgbean methodBean2 = new MethodInvokingFactorgbean();
            methodBean2.setTargetObject(objPoolMgr);
            methodBean2.setTargetMethod("addPool");
            methodBean2.setArguments(new Object[] { getPoolName2(), pool2, poolWeight2 });
            methodBean2.prepare();
            methodBean2.invoke();

            // objPoolMgr.addPool(getPoolName1(), pool1, getPoolWeight1());
            // objPoolMgr.addPool(getPoolName2(), pool2, getPoolWeight2());

            // 7.生成factory(放在最后,否则会报错)
            factory = new ThriftClientFactory(objPoolMgr, clientClassName);

            // 8.test （如果失败,不允许启动
            TFramedTransportFactory factory3 = new TFramedTransportFactory(config1);
            LOG.info("【test】 validator:{}:{}", getValidatatorName(),
                    validator.isValid((TTransport) factory3.makeObject()));
            LOG.info("[ThriftConfig] success : " + getValidatatorName());
        } catch (Throwable e) {
            LOG.error("[ThriftConfig] fail : " + getValidatatorName(), e);
        }

    }

    public ThriftClientFactory getFactory() {
        return factory;
    }

    public void setClientClassName(String clientClassName) {
        this.clientClassName = clientClassName;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setCheckPoolMills(int checkPoolMills) {
        this.checkPoolMills = checkPoolMills;
    }

    public void setValidator(AbstractClientValidator validator) {
        this.validator = validator;
    }

    // 比如com.gb.pay.bussiness.thrift.gbpay.PaymentService$Client
    // 返回gbpay.PaymentService
    public String getValidatatorName() {
        if (StringUtils.isEmpty(validatatorName)) {
            String className = clientClassName;
            if (!StringUtils.isEmpty(className)) {
                String[] splits = className.split("\\.");
                if (splits.length >= 2) {
                    String temp = splits[splits.length - 2] + "." + splits[splits.length - 1];
                    int index = temp.indexOf("$");
                    if (index > 1) {
                        return temp.substring(0, index);
                    }
                    return temp;
                } else {
                    throw new RuntimeException("请检查配置文件 className= " + clientClassName);
                }
            }
        }
        return validatatorName;
    }

    public void setValidatatorName(String validatatorName) {
        this.validatatorName = validatatorName;
    }

    // 比如com.gb.pay.bussiness.thrift.gbpay.PaymentService$Client
    // 返回gbpay1.PaymentService
    public String getPoolName1() {
        if (StringUtils.isEmpty(poolName1)) {
            String className = clientClassName;
            if (!StringUtils.isEmpty(className)) {
                String[] splits = className.split("\\.");
                if (splits.length >= 2) {
                    String temp = splits[splits.length - 2] + "1." + splits[splits.length - 1];
                    int index = temp.indexOf("$");
                    if (index > 1) {
                        return temp.substring(0, index);
                    }
                    return temp;
                } else {
                    throw new RuntimeException("请检查配置文件 className= " + clientClassName);
                }
            }
        }
        return poolName1;
    }

    public void setPoolName1(String poolName1) {
        this.poolName1 = poolName1;
    }

    public void setPoolWeight1(int poolWeight1) {
        this.poolWeight1 = poolWeight1;
    }

    public void setHost1(String host1) {
        this.host1 = host1;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    public void setSoTimeout1(int soTimeout1) {
        this.soTimeout1 = soTimeout1;
    }

    // 比如com.gb.pay.bussiness.thrift.gbpay.PaymentService$Client
    // 返回gbpay2.PaymentService
    public String getPoolName2() {
        if (StringUtils.isEmpty(poolName2)) {
            String className = clientClassName;
            if (!StringUtils.isEmpty(className)) {
                String[] splits = className.split("\\.");
                if (splits.length >= 2) {
                    String temp = splits[splits.length - 2] + "2." + splits[splits.length - 1];
                    int index = temp.indexOf("$");
                    if (index > 1) {
                        return temp.substring(0, index);
                    }
                    return temp;
                } else {
                    throw new RuntimeException("请检查配置文件 className= " + clientClassName);
                }
            }
        }
        return poolName1;
    }

    public void setPoolName2(String poolName2) {
        this.poolName2 = poolName2;
    }

    public int getPoolWeight2() {
        return poolWeight2;
    }

    public void setPoolWeight2(int poolWeight2) {
        this.poolWeight2 = poolWeight2;
    }

    public void setHost2(String host2) {
        this.host2 = host2;
    }

    public void setPort2(int port2) {
        this.port2 = port2;
    }

    public void setSoTimeout2(int soTimeout2) {
        this.soTimeout2 = soTimeout2;
    }

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {

        // 1.连接池的通用设置
        PoolConfig config = new PoolConfig();
        config.setMinIdle(1);
        config.setMaxActive(10);
        config.setMaxIdle(1);
        config.setTimeBetweenEvictionRunsMillis(800);
        config.setTestWhileIdle(true);

        // 2.thrift的客户端设置
        InspectableBalanceController balance = new InspectableBalanceController(6000);
        IObjectPoolManager objPoolMgr = new WeightedObjectPoolManager(balance);

        // 3.连接池的有效验证器
        AbstractClientValidator validator = new gbPayClientValidator();
        validator.setPoolManager(objPoolMgr);
        validator.setPoolName("test");

        // 4.第一个连接池
        TSocketConfig config1 = new TSocketConfig("172.19.104.38", 10270, 300);
        TFramedTransportFactory factory1 = new TFramedTransportFactory(config1, validator);
        InitalizableObjectPool pool1 = new InitalizableObjectPool(factory1, config);

        // 6.权重管理
        MethodInvokingFactorgbean methodBean1 = new MethodInvokingFactorgbean();
        methodBean1.setTargetObject(objPoolMgr);
        methodBean1.setTargetMethod("addPool");
        methodBean1.setArguments(new Object[] { "test", pool1, 1 });
        methodBean1.prepare();
        methodBean1.invoke();

        ThriftClientFactory factory = new ThriftClientFactory(objPoolMgr,
                "com.gb.pay.bussiness.thrift.gbpay.PaymentService$Client");
        factory.createClient();
    }
}
