package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public interface RedService {
	public PMessage sendOrdinary(POneRed oneRed, String senduuid);

	public PMessage sendGroup(PGroupRed groupRed, String senduuid);
}
