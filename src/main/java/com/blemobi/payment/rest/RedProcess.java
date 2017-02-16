package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.model.Red;
import com.blemobi.payment.service.ReceiveRedService;
import com.blemobi.payment.service.SendRedService;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("/red")
public class RedProcess {

	/**
	 * 发送一对一红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@POST
	@Path("send")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage send(@CookieParam("uuid") String uuid) {
		int amount = 100;
		String receiveUUID = "";
		SendRedService sendRedService = new SendRedService(uuid, receiveUUID, amount);
		return sendRedService.send();
	}

	/**
	 * 领取一对一红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@PUT
	@Path("receive")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage receive(@CookieParam("uuid") String uuid) {
		String custorderno = "";
		ReceiveRedService receiveRedService = new ReceiveRedService(uuid, custorderno);
		return receiveRedService.receive();
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
		long sendTime = System.currentTimeMillis();
		long invalidTime = sendTime + 24 * 60 * 60 * 1000;

		Red red = new Red();
		red.setSenduuid(uuid);
		red.setSendtime(sendTime);
		red.setInvalidtime(invalidTime);

		return null;
	}
}