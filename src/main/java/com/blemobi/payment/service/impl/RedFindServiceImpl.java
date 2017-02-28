package com.blemobi.payment.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.dao.RedReceiveDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.model.Bill;
import com.blemobi.payment.model.RedReceive;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.RedFindService;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveHistory;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveRece;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发红包业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("redFindService")
public class RedFindServiceImpl implements RedFindService {

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private RedReceiveDao redReceiveDao;

	@Autowired
	private BillDao billDao;

	@Override
	public PMessage history(String uuid, int id, int size) {
		if (id < 0)
			id = Integer.MAX_VALUE;
		List<RedSend> list = redSendDao.selectByPage(uuid, id, size);
		List<PRedEnveInfo> reiList = new ArrayList<PRedEnveInfo>();
		for (RedSend redSend : list) {
			List<RedReceive> receiveList = redReceiveDao.selectByKey(redSend.getOrd_no());
			List<PRedEnveRece> p_receive_list = new ArrayList<PRedEnveRece>();
			for (RedReceive redReceive : receiveList) {
				PUserBase userBase = PUserBase.newBuilder().setUUID(redReceive.getRece_uuid() + "").build();
				PRedEnveRece p_receive = PRedEnveRece.newBuilder().setId(redReceive.getId())
						.setMoney(redReceive.getMoney()).setReceTm(redReceive.getRece_tm()).setUserBase(userBase)
						.build();
				p_receive_list.add(p_receive);
			}

			PUserBase userBase = PUserBase.newBuilder().setUUID(redSend.getSend_uuid() + "").build();
			PRedEnveInfo redInfo = PRedEnveInfo.newBuilder().setOrdNo(redSend.getOrd_no()).setUserBase(userBase)
					.setStatus(0).setType(redSend.getType()).setTotaMoney(redSend.getTota_money())
					.setTotaNumber(redSend.getTota_number()).setReceMoney(redSend.getRece_money())
					.setReceNumber(redSend.getRece_number()).setContent(redSend.getContent())
					.setSendTm(redSend.getSend_tm()).addAllRedEnveRece(p_receive_list).build();

			reiList.add(redInfo);
		}

		PRedEnveHistory history = PRedEnveHistory.newBuilder().addAllRedEnveInfo(reiList).build();
		return ReslutUtil.createReslutMessage(history);
	}

	public PMessage bill(String uuid, int id, int size) {
		List<Bill> list = billDao.selectByPage(uuid, id, size);

		return null;
	}
}
