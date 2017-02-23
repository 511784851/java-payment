package com.blemobi.payment.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.RecRedService;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

@Service("recRedService")
public class RecRedServiceImpl implements RecRedService {

	@Autowired
	private RedDao redDao;

	@Transactional
	public PMessage receive(String ord_no, String rec_uuid) {
		// TODO Auto-generated method stub

		Set<String> set = redDao.findByOrdNo(ord_no);
		if (!set.contains(rec_uuid)) {

		}

		RedSend redSend = redDao.selectByKey(ord_no);
		if (redSend.getPay_status() != 1) {

		}
		if (redSend.getRec_status() != 1) {

		}

		int red_type = redSend.getRed_type();
		if (red_type == 1) {// 普通红包

		}

		return null;
	}
}
