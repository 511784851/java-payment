package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RedReceiveService;
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
	private RedReceiveService redReceiveService = InstanceFactory.getInstance(RedReceiveService.class);

	/**
	 * 查询红包针对用户状态
	 * 
	 * @param rece_uuid
	 * @param ord_no
	 * @param last_id
	 * @param count
	 * @return
	 */
	@GET
	@Path("status")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage findRedEnveStatus(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no) {
		return redReceiveService.findRedEnveStatus(ord_no, rece_uuid);
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
	 */
	@POST
	@Path("receive/redEnve")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage receive(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no) {
		return redReceiveService.receive(ord_no, rece_uuid);
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
	 */
	@POST
	@Path("find/redEnveInfo")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage findRedEnveInfo(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no) {
		return redReceiveService.findRedEnveInfo(ord_no, rece_uuid);
	}

	/**
	 * 批量加载领红包用户
	 * 
	 * @param rece_uuid
	 *            领红包用户
	 * @param ord_no
	 *            业务订单号
	 * @param last_id
	 *            上一条数据id
	 * @param count
	 *            数量大小
	 * @return
	 */
	@GET
	@Path("find/receRedEnve")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage find(@CookieParam("uuid") String rece_uuid, @QueryParam("ord_no") String ord_no,
			@QueryParam("last_id") int last_id, @QueryParam("count") int count) {
		return redReceiveService.find(ord_no, rece_uuid, last_id, count);
	}
}