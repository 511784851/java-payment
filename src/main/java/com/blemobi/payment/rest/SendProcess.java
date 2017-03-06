package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.common.base.Strings;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/redEnve")
public class SendProcess {

	// @Autowired
	private RedSendService redSendService = InstanceFactory.getInstance(RedSendService.class);

	/**
	 * 发普通红包
	 * 
	 * @param ordinRedEnve
	 *            红包信息
	 * @param send_uuid
	 *            发送者uuid
	 * @return
	 */
	@POST
	@Path("send-ordin")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage sendOrdinary(POrdinRedEnve ordinRedEnve, @CookieParam("uuid") String send_uuid) {
		return redSendService.sendOrdinary(ordinRedEnve, send_uuid);
	}

	/**
	 * 发群红包
	 * 
	 * @param ordinRedEnve
	 *            红包信息
	 * @param send_uuid
	 *            发送者uuid
	 * @return
	 */
	@POST
	@Path("send-group")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage sendGroup(PGroupRedEnve groupRedEnve, @CookieParam("uuid") String send_uuid) {
		return redSendService.sendGroup(groupRedEnve, send_uuid);
	}

	/**
	 * 查询红包发送历史
	 * 
	 * @param uuid
	 * @param idx
	 * @param count
	 * @return
	 */
	@GET
	@Path("send-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @QueryParam("idx") int idx, @QueryParam("count") int count) {
		if (Strings.isNullOrEmpty(uuid))
			uuid = "1471175703665920835";
		return redSendService.list(uuid, idx, count);
	}

}