package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.service.RewardService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.POrdinaryRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 打赏接口
 * 
 * @author zhaoyong
 *
 */
@Path("payment/v1/reward")
public class RewardProcess {

	// @Autowired
	private RewardService rewardService = InstanceFactory.getInstance("rewardService");

	/**
	 * 打赏
	 * 
	 * @param uuid
	 * @param token
	 * @return
	 */
	@POST
	@Path("ordinary")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage ordinary(POrdinaryRed ordinaryRed, @CookieParam("uuid") long send_uuid) {
		send_uuid = 1468419313301436967l;
		return rewardService.reward(ordinaryRed, send_uuid);
	}
}