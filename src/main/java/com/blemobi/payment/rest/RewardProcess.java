package com.blemobi.payment.rest;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RewardService;
import com.blemobi.payment.util.InstanceFactory;
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
	public PMessage reward(@CookieParam("uuid") String send_uuid, @FormParam("money") int money,
			@FormParam("content") String content, @FormParam("rece_uuid") String rece_uuid) {
		return rewardService.reward(send_uuid, money, content, rece_uuid);
	}

	/**
	 * 查询历史打赏数据
	 * 
	 * @param uuid
	 * @param type
	 * @param idx
	 * @param count
	 * @return
	 * @throws IOException 
	 */
	@GET
	@Path("list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @QueryParam("other_uuid") String other_uuid,
			@QueryParam("type") int type, @QueryParam("idx") int idx, @QueryParam("count") int count) throws IOException {
		return rewardService.list(uuid, other_uuid, type, idx, count);
	}

	/**
	 * 查看打赏详情以及打赏记录
	 * 
	 * @param uuid
	 * @param type
	 * @param idx
	 * @param count
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("info-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage infoList(@CookieParam("uuid") String uuid, @QueryParam("ord_no") String ord_no,
			@QueryParam("idx") int idx, @QueryParam("count") int count) throws IOException {
		return rewardService.info(ord_no, uuid, idx, count);
	}
}