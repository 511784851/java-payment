package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RedReceiveService;
import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POrdinaryRed;
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
	private RedSendService redSendService = InstanceFactory.getInstance("redSendService");

	// @Autowired
	private RedReceiveService redReceiveService = InstanceFactory.getInstance("redReceiveService");

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
	public PMessage ordinary(POrdinaryRed ordinaryRed, @CookieParam("uuid") long senduuid) {
		senduuid = 1468419313301436967l;
		return redSendService.sendOrdinary(ordinaryRed, senduuid);
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
	public PMessage group(PGroupRed groupRed, @CookieParam("uuid") long send_uuid) {
		send_uuid = 1468419313301436967l;
		return redSendService.sendGroup(groupRed, send_uuid);
	}

	/**
	 * 领红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@GET
	@Path("receive")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage receive(@CookieParam("uuid") long rece_uuid, @QueryParam("ord_no") String ord_no) {
		rece_uuid = 1468419313301436968L;
		return redReceiveService.receive(ord_no, rece_uuid);
	}
}