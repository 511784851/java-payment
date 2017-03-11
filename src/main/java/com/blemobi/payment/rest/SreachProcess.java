package com.blemobi.payment.rest;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.SreachService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 搜索接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/sreach")
public class SreachProcess {

	private SreachService sreachService = InstanceFactory.getInstance(SreachService.class);

	/**
	 * 搜素发红包和领到的打赏
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param keyword
	 *            昵称关键字
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @QueryParam("keyword") String keyword) throws IOException {
		return sreachService.list(uuid, keyword);
	}
}