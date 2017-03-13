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
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveBaseInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRewardInfo;
import com.blemobi.sep.probuf.PaymentProtos.PSreachList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringList;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;

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
		PStringSingle request = PStringSingle.newBuilder().setVal(keyword).build();
		DataPublishGrpcClient client = new DataPublishGrpcClient();
		PStringList stringList = client.SearchUser(request);
		log.debug("匹配的uuid：" + stringList);
		if (stringList != null) {
			List<String> sreachUUIDs = stringList.getListList();
			log.debug("匹配的uuid：" + sreachUUIDs);
			if (sreachUUIDs != null && sreachUUIDs.size() > 0) {
				// 全部发送红包记录
				List<RedSend> allRedSendList = redSendDao.selectByPage(uuid, Integer.MAX_VALUE, 100000);
				log.debug("共有发红包数量：" + allRedSendList.size());
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
						log.debug(sreachUUID + " 是否符合搜索条件：" + bool);
						if (bool) {
							PRedEnveBaseInfo redInfo = buildRedEnveBaseInfo(redSend);
							redList.add(redInfo);
							break;
						}
					}
				}

				List<Reward> allRewardList = rewardDao.selectReceByPage(uuid, "", Integer.MAX_VALUE, 1000000);
				// 符合搜索条件的发送红包记录
				log.debug("共有 打赏数量：" + allRewardList.size());
				List<PRewardInfo> rewardInfoList = new ArrayList<PRewardInfo>();
				for (Reward reward : allRewardList) {
					for (String sreachUUID : sreachUUIDs) {
						log.debug(sreachUUID + "：" + reward.getSend_uuid());
						// 是否符合搜索条件
						if (sreachUUID.equals(reward.getSend_uuid())) {
							PUserBase userBase = UserBaseCache.get(reward.getUuid());
							PRewardInfo rewardInfo = buildRawardInfo(userBase, reward);
							rewardInfoList.add(rewardInfo);
							break;
						}
					}
				}
				sreachList = PSreachList.newBuilder().addAllRedEnveBaseInfo(redList).addAllRewardInfo(rewardInfoList)
						.build();
			}
		}

		return ReslutUtil.createReslutMessage(sreachList);

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
