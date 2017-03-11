package com.blemobi.payment.rest;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.ReceiveService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 领红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/redEnve")
public class receiveProcess {

	// @Autowired
	private ReceiveService receiveService = InstanceFactory.getInstance(ReceiveService.class);

	/**
	 * 查询红包针对用户状态
	 * 
	 * @param rece_uuid
	 *            用户uuid
	 * @param ord_no
	 *            订单号
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("status")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage findRedEnveStatus(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no)
			throws IOException {
		return receiveService.checkStatus(ord_no, rece_uuid);
	}

	/**
	 * 领红包
	 * 
	 * @param rece_uuid
	 *            领取用户uuid
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("receive")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage receive(@CookieParam("uuid") String rece_uuid, @FormParam("ord_no") String ord_no)
			throws IOException {
		return receiveService.receive(ord_no, rece_uuid);
	}

	/**
	 * 查看红包详情
	 * 
	 * @param rece_uuid
	 *            领取用户uuid
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("info")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage info(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no)
			throws IOException {
		return receiveService.findInfo(ord_no, rece_uuid);
	}

	/**
	 * 查询领红包用户
	 * 
	 * @param rece_uuid
	 *            领红包用户
	 * @param ord_no
	 *            业务订单号
	 * @param last_id
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("receive-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage find(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no,
			@QueryParam("last_id") int last_id, @QueryParam("count") int count) throws IOException {
		return receiveService.findList(ord_no, rece_uuid, last_id, count);
	}
}