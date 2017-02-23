package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.service.SendRedService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("payment/v1/send")
public class RedProcess {

	// @Autowired
	private SendRedService sendRedService = InstanceFactory.getInstance("sendRedService");

	/**
	 * 发普通红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@POST
	@Path("ordinary")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage ordinary(POneRed oneRed, @CookieParam("uuid") long senduuid) {
		senduuid = 1468419313301436967l;
		return sendRedService.sendOrdinary(oneRed, senduuid);
	}

	/**
	 * 发群红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@POST
	@Path("group")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage receive(PGroupRed groupRed, @CookieParam("uuid") long senduuid) {
		return sendRedService.sendGroup(groupRed, senduuid);
	}
}