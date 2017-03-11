package com.blemobi.payment.rest;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.service.SreachService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 发红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/sreach")
public class SreachProcess {

	private SreachService sreachService = InstanceFactory.getInstance(SreachService.class);

	/**
	 * 发普通红包
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param money
	 *            金额（分）
	 * @param content
	 *            描述
	 * @param rece_uuid
	 *            接受者uuid
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @FormParam("keyword") String keyword) throws IOException {
		return sreachService.list(uuid, keyword);
	}
}