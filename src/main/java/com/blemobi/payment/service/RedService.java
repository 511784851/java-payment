package com.blemobi.payment.service;

import com.blemobi.payment.model.Red;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public interface RedService {
	public PMessage send(Red red);

	public Red selectByKey(String custorderno);
}
