package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.service.RedService;
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
@Path("payment/send")
public class RedProcess {

	// @Autowired
	private RedService redService = InstanceFactory.getInstance("redService");

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
	public PMessage ordinary(POneRed oneRed, @CookieParam("uuid") String senduuid) {
		return redService.sendOrdinary(oneRed, senduuid);
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
	public PMessage receive(PGroupRed groupRed, @CookieParam("uuid") String senduuid) {
		return redService.sendGroup(groupRed, senduuid);
	}

	/**
	 * 查看一对一红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@GET
	@Path("deatil")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage deatil(@CookieParam("uuid") String uuid) {

		return null;
	}
}