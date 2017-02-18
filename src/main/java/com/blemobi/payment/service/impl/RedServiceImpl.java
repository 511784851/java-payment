package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.model.Red;
import com.blemobi.payment.service.RedService;
import com.blemobi.payment.service.helper.RedHelper;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

@Service("redService")
public class RedServiceImpl implements RedService {

	@Autowired
	private RedDao redDao;

	public PMessage send(Red red) {
		RedHelper redHelper = new RedHelper(red.getSenduuid(), red.getReceiveuuid(), red.getAmount());
		red = redHelper.initOne();
		redDao.insert(red.getAmount());
		return null;
	}

	public Red selectByKey(String custorderno) {
		return redDao.selectByKey(custorderno);
	}
}
