package com.blemobi.payment.consul;
/**
 * 
 * @author 李子才<davis.lee@blemobi.com>
 * 这是Consul服务器的取信息的线程。
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.blemobi.payment.redis.RedisManager;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.model.CatalogService;

import lombok.extern.log4j.Log4j;
@Log4j
public class ConsulMonitorThread extends Thread {
	//声明变量,聊天系统的在consul中的key-value中键名的前缀
	private static final String KEY_PRE_FIX_CHAT = "blemobi/sep/payment/"+System.getProperty("EnvMode", "")+"/"; 
	
	//声明变量,聊天系统依赖的两个服务分别是账户系统[account]和好友[oss]系统。
	private static final String[] SERVICE_NAME_LIST = new String[]{"account","oss","login"};

	private Logger logger = Logger.getLogger(ConsulMonitorThread.class);

	//连接consul服务器的对象
	private ConsulClient consulClient = null;
	
	//适配器队列。
	private ArrayList<ConsulChangeListener> allListener = null;

	//依赖的两个服务{"account","oss"}的信息记录表
	java.util.HashMap<String, String[][]> allService = makeAllService(SERVICE_NAME_LIST);
	
	//Consul中的Key-value的值的map
	Map<String,String> propInfo = new java.util.HashMap();

	//访问consul服务器的Token
	private String token = null;

	//访问consul服务器的间隔时间，默认值.会被Constant.ConsulIntervalTime的值替代！
	private long loopTime = 1000 * 60 * 30 * 1L;

	/**
	 * 构造函数ConsulMonitorThread。
	 * @param consulClient 是consul连接对象
	 * @param loopTime 连接的间隔时间
	 * @param allListener 适配器对象。
	 * @param token 访问consul服务器的token认证信息。
	 */
	public ConsulMonitorThread(ConsulClient consulClient, long loopTime, ArrayList<ConsulChangeListener> allListener, String token) {
		this.consulClient  = consulClient;
		this.allListener  = allListener;
		this.loopTime  = loopTime;
		this.token   = token;
		try {doNotifyAllListener();} catch (Exception e) {}//触发更新一次
	}

	/**
	 * 创建“依赖服务(account和oss)”的存储空间。
	 * @param serviceNameList 服务名称的列表。
	 * @return 返回Map,<String, String[][]> 的记录模式，其中String[][]的String[i][0]为IP地址，String[i][1]为端口 。
	 */
	private HashMap<String, String[][]> makeAllService(String[] serviceNameList) {
		java.util.HashMap<String, String[][]> rtn = new java.util.HashMap<String, String[][]>();
		for(String name:serviceNameList){ 
			rtn.put(name, new String[0][2]);
		}
		return rtn;
	}

	/**
	 * 线程类的run()函数。
	 */
	public void run() {
		while(true){
			// 循环调用显示consul信息 
			try {sleep(loopTime);} catch (InterruptedException e) {}
			try {doNotifyAllListener();} catch (Exception e) {}
		}

	}


	/**
	 * 从consul服务器获取信息变更，并且通知到所有的适配器对象。
	 */
	private void doNotifyAllListener() {
		for(String serviceName:SERVICE_NAME_LIST){ 
			String[][] info = getServiceInfo(consulClient,serviceName); 
			
			log.info("list consul Server("+serviceName+")");
			for(String[] serv: info){
				log.info("consul server("+serviceName+") addr="+serv[0]+", port=["+serv[1]+"]");
			}
			
			
			boolean isSame = true;
			if(info.length>0) isSame = checkServiceInfoSame(allService.get(serviceName),info);
			if(!isSame){
				allService.put(serviceName, info);//有变化时则更新
				for(ConsulChangeListener listener:allListener){
					listener.onServiceChange(serviceName, info);
				}
			}
		}
		
		Map<String, String> prop = getEvnInfo(consulClient); 
		log.info("list consul Env(key-value)");
		for(String key: prop.keySet()){
			log.info("consul "+key+"=["+prop.get(key)+"]");
		}
		boolean isPropSame = true;
		if(prop.size()>0) isPropSame = checkPopSame(propInfo,prop); // 检查2个map的内容是否一样
		if(!isPropSame){
			propInfo = prop;
			for(ConsulChangeListener listener:allListener){
				listener.onEnvChange(prop);
			}
		}
	}
	
	/**
	 * 从consul服务器获取到的信息，通知到某个适配器对象，一般是用到某个适配器首次加入管理是调用。
	 * @param listener 某个适配器对象。
	 */
	private void doNotifyOneListener(ConsulChangeListener listener) {
		for(String serviceName:SERVICE_NAME_LIST){ 
			String[][] info = allService.get(serviceName); 
			listener.onServiceChange(serviceName, info);
		}
		listener.onEnvChange(propInfo);
	}
	

	/**
	 * 比对两个数组内的数据内容相同，并且不考虑排序问题。
	 * @param frist 第一个数组对象。
	 * @param second 第二个数组对象。
	 * @return 返回布尔值，两个数组中的内容相同时为true。
	 */
	private boolean checkServiceInfoSame(String[][] frist, String[][] second) {
		boolean rtn = true;
		if(frist.length == second.length){
			for(int i=0;i<frist.length;i++){
				boolean inFlag = checkElementInArray(frist[i],second);
				if(!inFlag){//元素不在集合里面，则返回false
					rtn = false;
					break;
				}
			}
		}else{
			rtn = false;
		}
		return rtn;
	}

	/**
	 * 检查元素是否在数组里面。
	 * @param element 集合中某个元素。
	 * @param array 大集合。
	 * @return 返回布尔值，如果元素存在于集合内，则为true。
	 */
	private boolean checkElementInArray(String[] element, String[][] array) {
		for(int i=0;i<array.length;i++){
			if(element[0].equals(array[i][0]) && element[1].equals(array[i][1]) ){
				return true;
			}
		}
		return false;
	}

	/**
	 * 比对两个属性类的数据内容相同
	 * @param frist 第一个属性类对象。
	 * @param second 第二个属性类对象。
	 * @return 返回布尔值，如果两个map的内容相同，则为true。
	 */
	private boolean checkPopSame(Map<String, String> frist, Map<String, String> second) {
		boolean rtn = true;
		if(frist.size()==second.size()){
			String[] keys = new String[frist.size()];
			frist.keySet().toArray(keys); // 第1个map的key值放入keys中去. 
			for(int i=0;i<keys.length;i++){
				String key = keys[i];
				String value = frist.get(key); // 根据第i个key,找到对应的value
				boolean inFlag = checkElementInMap(key,value,second);  // 检查第一个key-value对是否存在第2个map中.
				if(!inFlag){//元素不在集合里面，则返回false
					rtn = false;
					break;
				}
			}
		}else{
			rtn = false;
		}
		return rtn;
	}

	/**
	 * 检查<key,value>是否在某个map里面
	 * @param key 是key键。
	 * @param value 是key键对应的值。
	 * @param porp 是Map属性类。
	 * @return 返回布尔值，如果该<key-value>,存在于集合内，则为true。
	 */
	private boolean checkElementInMap(String key,String value, Map<String, String> porp) {
		String mv = porp.get(key);
		return mv.equals(value);
	}

	/**
	 * 获取consul服务器中的key-value的信息内容。
	 * @param client 访问consul服务的对象。
	 * @return 返回<key-value>的map对象。
	 */
	private Map<String, String> getEvnInfo(ConsulClient client) {
		Map rtn = new java.util.HashMap<String, String>();
		List<String> keys = (token==null)?consulClient.getKVKeysOnly(KEY_PRE_FIX_CHAT).getValue():consulClient.getKVKeysOnly(KEY_PRE_FIX_CHAT,null,token).getValue();

		int size = keys.size();
		int preLen = KEY_PRE_FIX_CHAT.length();
		for(int i=0;i<size;i++){ // foreach key 
			String key = keys.get(i);
			if(!key.endsWith("/")){
				try{
					String value = getConsulKVValue(client,key); // 获取value
					rtn.put(key.substring(preLen), value); // 保存key-value ,return 
				}catch(Exception e){

				}
			}
		}
		return rtn;
	}
	
	/**
	 * 获取consul服务器中的key-value的信息内容。
	 * @param client 访问consul服务的对象。
	 * @param key 指定的对应的key的名称。
	 * @return 返回对应的value的值。
	 */
	private String getConsulKVValue(ConsulClient client, String key) {
		byte[] data = null;
		if (token == null || token.equals("")) {
			data = client.getKVBinaryValue(key).getValue().getValue();
		} else {
			data = client.getKVBinaryValue(key, this.token).getValue().getValue();
		}

		if(data==null){
			return "";
		}else{
			return new String(data);
		}
	}


	/**
	 * 获取consul服务器中的的各服务的服务器列表内容。
	 * @param client 访问consul服务的对象。
	 * @param name 服务的名称。
	 * @return 返回对应的服务的服务器列表。String[i][0]为IP地址，String[i][1]为端口。
	 */
	private String[][] getServiceInfo(ConsulClient client,String name) {
		String[][] rtn = new String[0][2];  //2维的2个字符串，只是2个临时变量 

		String param = (token==null)?"":"?token="+token;

		Response<List<CatalogService>> response = client.getCatalogService(name+param, null);

		List<CatalogService> nodes = response.getValue();


		if (nodes.size() != 0) {
			CatalogService[] service = new CatalogService[nodes.size()];
			nodes.toArray(service);
			rtn = new String[service.length][2];
			for(int i=0;i<service.length;i++){
				rtn[i][0] = service[i].getServiceAddress();
				rtn[i][1] = ""+service[i].getServicePort();
			}
		} else {
			logger.error("Please Notice！  Consul Serivie["+name+ "] Count = 0");
		}
		return rtn;
	}

	/**
	 * 提供给外部适配器注册登记。登记后适配器，都能收到Consul服务器配置信息有变更时的通知。
	 * @param adapter 需要登记的适配器。
	 */
	public void addConsulChangeListener(ConsulChangeListener adapter) {
		doNotifyOneListener(adapter);
		allListener.add(adapter);
	}
	
	/**
	 * 从服务列表中移除适配器。移除后，该适配器不再收到Consul服务器配置信息变更。
	 * @param adapter 需要移除的适配器。
	 */
	public void removeConsulChangeListener(ConsulChangeListener adapter) {
		allListener.remove(adapter);
	}

}
