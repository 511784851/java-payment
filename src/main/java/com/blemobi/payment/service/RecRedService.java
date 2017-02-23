package com.blemobi.payment.service;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

public interface RecRedService {
	public PMessage receive(String ord_no, String rec_uuid);
}
