package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RedFindService;
import com.blemobi.payment.service.RedReceiveService;
import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/send")
public class RedSendProcess {

	// @Autowired
	private RedSendService redSendService = InstanceFactory.getInstance(RedSendService.class);
	// @Autowired
	private RedReceiveService redReceiveService = InstanceFactory.getInstance(RedReceiveService.class);
	// @Autowired
	private RedFindService redFindService = InstanceFactory.getInstance(RedFindService.class);

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
	public PMessage ordinary(POrdinRedEnve ordinRedEnve, @CookieParam("uuid") String send_uuid) {
		send_uuid = "1471175703665920835";
		return redSendService.sendOrdinary(ordinRedEnve, send_uuid);
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
	public PMessage group(PGroupRedEnve groupRedEnve, @CookieParam("uuid") String send_uuid) {
		send_uuid = "1471175703665920835";
		return redSendService.sendGroup(groupRedEnve, send_uuid);
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
	public PMessage receive(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no) {
		rece_uuid = "1471175703665920835";
		return redReceiveService.receive(ord_no, rece_uuid);
	}

	@GET
	@Path("history")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage history(@CookieParam("uuid") String uuid, @QueryParam("id") int id, @QueryParam("size") int size) {
		uuid = "1471175703665920835";
		return redFindService.history(uuid, id, size);
	}

}