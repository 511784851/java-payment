package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.client.AccountHttpClient;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.service.UserService;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.DataPublishingApiProtos.PGroupString;
import com.blemobi.sep.probuf.DataPublishingApiProtos.PGroupStringList;
import com.blemobi.sep.probuf.DataPublishingApiProtos.PQueryUserParam;
import com.blemobi.sep.probuf.PaymentProtos.PCelebrityGroup;
import com.blemobi.sep.probuf.PaymentProtos.PCelebrityInfo;
import com.blemobi.sep.probuf.PaymentProtos.PCelebrityList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringList;
import com.google.protobuf.ProtocolStringList;

import lombok.extern.log4j.Log4j;

/**
 * 账单业务实现类
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Service("userService")
public class UserServiceImpl implements UserService {

	@Override
	public PMessage celebrity(String uuid, String keyword) throws IOException {
		// 获得全部网红用户
		List<PGroupString> list = getAllCelebrity(uuid, keyword);
		log.debug("用户【" + uuid + "】，网红分组数量：" + list.size());
		List<PCelebrityGroup> cgList = new ArrayList<PCelebrityGroup>();
		for (PGroupString groupString : list) {
			PCelebrityGroup celebrityGroup = buildPCelebrityGroup(groupString);
			cgList.add(celebrityGroup);
		}

		PCelebrityList celebrityList = PCelebrityList.newBuilder().addAllCelebrityGroup(cgList).build();
		return ReslutUtil.createReslutMessage(celebrityList);
	}

	/**
	 * 获得全部网红用户
	 * 
	 * @param queryUserParam
	 * @return
	 */
	private List<PGroupString> getAllCelebrity(String uuid, String keyword) {
		PQueryUserParam queryUserParam = PQueryUserParam.newBuilder().setUuid(uuid).setKeyword(keyword).setOffset(0)
				.setSize(10000).build();

		DataPublishGrpcClient client = new DataPublishGrpcClient();
		PGroupStringList groupStringList = client.SelectVUser(queryUserParam);
		return groupStringList.getListList();
	}

	/**
	 * 构建PCelebrityGroup对象
	 * 
	 * @param groupString
	 * @param group
	 * @return
	 * @throws IOException
	 */
	private PCelebrityGroup buildPCelebrityGroup(PGroupString groupString) throws IOException {
		String group = groupString.getGroup();
		List<PCelebrityInfo> userList = getUserInfoList(groupString);
		log.debug("分组：" + group + " ; 网红总数：" + userList.size());
		PCelebrityGroup celebrityGroup = PCelebrityGroup.newBuilder().setGroup(group).addAllCelebrityInfo(userList)
				.build();
		return celebrityGroup;
	}

	/**
	 * 获得网红信息
	 * 
	 * @param groupString
	 * @return
	 * @throws IOException
	 */
	private List<PCelebrityInfo> getUserInfoList(PGroupString groupString) throws IOException {
		ProtocolStringList stringList = groupString.getListList();
		PStringList stringVOList = getUserVOInfoList(stringList);
		log.debug("网红数量：" + stringList.size() + " ; 网红VO数量：" + stringVOList.getListCount());
		List<PCelebrityInfo> userList = new ArrayList<PCelebrityInfo>();
		for (int i = 0; i < stringList.size(); i++) {
			String uuid = stringList.get(i);
			String vouuid = stringVOList.getList(i);
			PUserBase userBase = UserBaseCache.get(uuid);
			PUserBase userVOBase = UserBaseCache.get(vouuid);
			PCelebrityInfo celebrityInfo = PCelebrityInfo.newBuilder().setUserBase(userBase).setUserBaseVO(userVOBase)
					.build();
			userList.add(celebrityInfo);
		}
		return userList;
	}

	/**
	 * 获取网红对应的VO信息
	 * 
	 * @param list
	 *            网红
	 * @return
	 * @throws IOException
	 */
	private PStringList getUserVOInfoList(List<String> list) throws IOException {
		PStringList stringList = PStringList.newBuilder().addAllList(list).build();
		AccountHttpClient client = new AccountHttpClient();
		PMessage message = client.getUserVOInfo(stringList.toByteArray());
		return PStringList.parseFrom(message.getData());
	}
}
