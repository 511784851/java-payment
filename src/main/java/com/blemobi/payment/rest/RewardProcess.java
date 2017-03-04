package com.blemobi.payment.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RewardService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 打赏接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/reward")
public class RewardProcess {

	// @Autowired
	private RewardService rewardService = InstanceFactory.getInstance(RewardService.class);

	/**
	 * 打赏
	 * 
	 * @param ordinRedEnve
	 *            打赏信息
	 * @param send_uuid
	 *            发送者uuid
	 * @return
	 */
	@POST
	@Path("send")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage reward(POrdinRedEnve ordinRedEnve, @QueryParam("uuid") String send_uuid) {
		return rewardService.reward(ordinRedEnve, send_uuid);
	}

	/**
	 * 查询历史打赏数据
	 * 
	 * @param uuid
	 * @param type
	 * @param idx
	 * @param count
	 * @return
	 */
	@GET
	@Path("list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@QueryParam("uuid") String uuid, @QueryParam("type") int type, @QueryParam("idx") int idx,
			@QueryParam("count") int count) {
		return rewardService.list(uuid, type, idx, count);
	}

	/**
	 * 查看打赏详情以及打赏记录
	 * 
	 * @param uuid
	 * @param type
	 * @param idx
	 * @param count
	 * @return
	 */
	@GET
	@Path("info-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage infoList(@QueryParam("uuid") String uuid, @QueryParam("ord_no") String ord_no,
			@QueryParam("idx") int idx, @QueryParam("count") int count) {
		return rewardService.info(ord_no, uuid, idx, count);
	}
}