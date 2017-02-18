package com.blemobi.payment.service;

import com.blemobi.payment.model.Transaction;

public interface NotifyService {
	public String callback(Transaction transaction, String sign);
}
