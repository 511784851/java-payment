package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.dao.TableStoreDao;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.model.Reward;
import com.blemobi.payment.service.SreachService;
import com.blemobi.payment.util.Constants.TABLE_NAMES;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.DatapublishingApiProtos.PQueryUserParam;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveBaseInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRewardInfo;
import com.blemobi.sep.probuf.PaymentProtos.PSreachList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringList;

import lombok.extern.log4j.Log4j;

/**
 * 搜索实现类
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Service("sreachService")
public class SreachServiceImpl implements SreachService {

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private RewardDao rewardDao;

	@Autowired
	private TableStoreDao tableStoreDao;

	@Override
	public PMessage list(String uuid, String keyword) throws IOException {
		PSreachList sreachList = PSreachList.newBuilder().build();
		PStringList stringList = getNicknameByKeyword(keyword);
		log.debug(keyword + " 匹配的uuid：" + stringList);
		if (stringList != null) {
			List<String> sreachUUIDs = stringList.getListList();
			if (sreachUUIDs != null && sreachUUIDs.size() > 0) {
				// 搜索红包发送记录
				List<PRedEnveBaseInfo> redList = sreachSendRed(uuid, sreachUUIDs);
				// 搜索收到的打赏记录
				List<PRewardInfo> rewardInfoList = sreachReward(uuid, sreachUUIDs);
				sreachList = PSreachList.newBuilder().addAllRedEnveBaseInfo(redList).addAllRewardInfo(rewardInfoList)
						.build();
			}
		}
		return ReslutUtil.createReslutMessage(sreachList);
	}

	/**
	 * 搜索红包发送记录
	 * 
	 * @param uuid
	 *            发送用户uuid
	 * @param sreachUUIDs
	 *            参与用户uuid
	 * @return
	 * @throws IOException
	 */
	private List<PRedEnveBaseInfo> sreachSendRed(String uuid, List<String> sreachUUIDs) throws IOException {
		// 全部发送红包记录
		List<RedSend> allRedSendList = redSendDao.selectByPage(uuid, Integer.MAX_VALUE, 100000);
		// 符合搜索条件的发送红包记录
		List<PRedEnveBaseInfo> redList = new ArrayList<PRedEnveBaseInfo>();
		for (RedSend redSend : allRedSendList) {
			for (String sreachUUID : sreachUUIDs) {
				// 是否符合搜索条件
				boolean bool = false;
				if (redSend.getRece_uuid5().indexOf(sreachUUID) >= 0)
					bool = true;
				else
					bool = tableStoreDao.existsByKey(TABLE_NAMES.RED_PKG_TB.getValue(), redSend.getOrd_no(),
							sreachUUID);
				if (bool) {
					PRedEnveBaseInfo redInfo = buildRedEnveBaseInfo(redSend);
					redList.add(redInfo);
					break;
				}
			}
		}
		return redList;
	}

	/**
	 * 搜索收到的打赏记录
	 * 
	 * @param uuid
	 *            领赏用户uuid
	 * @param sreachUUIDs
	 *            要搜索的打赏用户uuid
	 * @return
	 * @throws IOException
	 */
	private List<PRewardInfo> sreachReward(String uuid, List<String> sreachUUIDs) throws IOException {
		List<Reward> allRewardList = rewardDao.selectReceByPage(uuid, "", Integer.MAX_VALUE, 1000000);
		// 符合搜索条件的发送红包记录
		List<PRewardInfo> rewardInfoList = new ArrayList<PRewardInfo>();
		for (Reward reward : allRewardList) {
			for (String sreachUUID : sreachUUIDs) {
				// 是否符合搜索条件
				if (sreachUUID.equals(reward.getUuid())) {
					PUserBase userBase = UserBaseCache.get(reward.getUuid());
					PRewardInfo rewardInfo = buildRawardInfo(userBase, reward);
					rewardInfoList.add(rewardInfo);
					break;
				}
			}
		}
		return rewardInfoList;
	}

	/**
	 * 匹配uuid
	 * 
	 * @param keyword
	 *            昵称
	 * @return
	 */
	private PStringList getNicknameByKeyword(String keyword) {
		PQueryUserParam request = PQueryUserParam.newBuilder().setKeyword(keyword).setOffset(0).setSize(1000).build();
		DataPublishGrpcClient client = new DataPublishGrpcClient();
		return client.SearchUser(request);
	}

	/**
	 * 构建PRedEnveBaseInfo对象
	 * 
	 * @param redSend
	 *            红包信息
	 * @return
	 * @throws IOException
	 */
	private PRedEnveBaseInfo buildRedEnveBaseInfo(RedSend redSend) throws IOException {
		List<PUserBase> userList5 = getReceUser5(redSend.getRece_uuid5());
		return PRedEnveBaseInfo.newBuilder().setId(redSend.getId()).setOrdNo(redSend.getOrd_no())
				.setType(redSend.getType()).setContent(redSend.getContent()).setSendTm(redSend.getSend_tm())
				.setNumber(redSend.getRece_tota_num()).addAllUserBase(userList5).build();
	}

	/**
	 * 获得红包前五个可领取用户信息
	 * 
	 * @param redSend
	 *            红包信息
	 * @return
	 * @throws IOException
	 */
	private List<PUserBase> getReceUser5(String uuid5) throws IOException {
		List<PUserBase> userList5 = new ArrayList<PUserBase>();
		for (String rece_uuid : uuid5.split(",")) {
			PUserBase userBase = UserBaseCache.get(rece_uuid);
			userList5.add(userBase);
		}
		return userList5;
	}

	/**
	 * 构建 RawardInfo对象
	 * 
	 * @param userBase
	 * @param reward
	 * @return
	 */
	private PRewardInfo buildRawardInfo(PUserBase userBase, Reward reward) {
		PRewardInfo rewardInfo = PRewardInfo.newBuilder().setId(reward.getId()).setOrdNo(reward.getOrd_no())
				.setMoney(reward.getMoney()).setContent(reward.getContent()).setTime(reward.getSend_tm())
				.setUserBase(userBase).build();
		return rewardInfo;
	}
}
