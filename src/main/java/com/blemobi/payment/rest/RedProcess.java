package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;

import com.blemobi.payment.service.RedService;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("payment/red")
public class RedProcess {

	@Autowired
	private RedService redService;

	/**
	 * 发送一对一红包
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@GET
	@Path("send")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage send(@CookieParam("uuid") String uuid) {
		int amount = 100;
		String receiveUUID = "";
		return redService.send(null);
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
		return null;
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