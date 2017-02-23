package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public interface SendRedService {
	public PMessage sendOrdinary(POneRed oneRed, long senduuid);

	public PMessage sendGroup(PGroupRed groupRed, long senduuid);
}
