package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.library.cache.UserBaseCache;
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
import com.google.common.base.Strings;

/**
 * 发红包业务实现类
 * 
 * @author zhaoyong
 *
 */
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
		String other_uuid = "";
		PSreachList sreachList = PSreachList.newBuilder().build();
		if (!Strings.isNullOrEmpty(other_uuid)) {
			// 全部发送红包记录
			List<RedSend> allRedSendList = redSendDao.selectByPage(uuid, 0, 100000);
			// 符合搜索条件的发送红包记录
			List<RedSend> sreachRedSendList = new ArrayList<>();
			for (RedSend redSend : allRedSendList) {
				// 是否符合搜索条件
				boolean bool = false;
				if (redSend.getRece_uuid5().indexOf(other_uuid) >= 0)
					bool = true;
				else
					bool = tableStoreDao.existsByKey(TABLE_NAMES.RED_PKG_TB.getValue(), redSend.getOrd_no(),
							other_uuid);
				if (bool)
					sreachRedSendList.add(redSend);
			}
			List<PRedEnveBaseInfo> redList = new ArrayList<PRedEnveBaseInfo>();
			for (RedSend redSend : sreachRedSendList) {
				PRedEnveBaseInfo redInfo = buildRedEnveBaseInfo(redSend);
				redList.add(redInfo);
			}

			List<Reward> allRewardList = rewardDao.selectReceByPage(uuid, "", 0, 100000);
			// 符合搜索条件的发送红包记录
			List<Reward> sreachRewardList = new ArrayList<>();
			for (Reward reward : allRewardList) {
				// 是否符合搜索条件
				if (other_uuid.equals(reward.getSend_uuid()))
					sreachRewardList.add(reward);
			}
			List<PRewardInfo> rewardInfoList = buildRewardList(sreachRewardList);

			sreachList = PSreachList.newBuilder().addAllRedEnveBaseInfo(redList).addAllRewardInfo(rewardInfoList)
					.build();
		}
		return ReslutUtil.createReslutMessage(sreachList);

	}

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
	 * 构建PRewardInfo列表对象
	 * 
	 * @param list
	 * @return
	 * @throws IOException
	 */
	private List<PRewardInfo> buildRewardList(List<Reward> list) throws IOException {
		List<PRewardInfo> rewardList = new ArrayList<PRewardInfo>();
		for (Reward reward : list) {
			PUserBase userBase = UserBaseCache.get(reward.getUuid());
			PRewardInfo rewardInfo = buildRawardInfo(userBase, reward);
			rewardList.add(rewardInfo);
		}
		return rewardList;
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
