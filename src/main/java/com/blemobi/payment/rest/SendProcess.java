package com.blemobi.payment.rest;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
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
	public PMessage sendOrdinary(@CookieParam("uuid") String send_uuid, @FormParam("money") int money,
			@FormParam("content") String content, @FormParam("rece_uuid") String rece_uuid) {
		return redSendService.sendOrdinary(send_uuid, money, content, rece_uuid);
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
	public PMessage sendGroup(@CookieParam("uuid") String send_uuid, @FormParam("number") int number,
			@FormParam("money") int money, @FormParam("isRandom") boolean isRandom,
			@FormParam("content") String content, @FormParam("tick_uuid") String tick_uuid,
			@FormParam("filter_gender") int filter_gender, @FormParam("filter_region") String filter_region,
			@FormParam("filter_negate") boolean filter_negate, @FormParam("filter_skipUuid") String filter_skipUuid) {

		String[] filter_region_arr = filter_region.split(",");
		String[] filter_skipUuid_arr = filter_skipUuid.split(",");

		PFansFilterParam fansFilterParam = PFansFilterParam.newBuilder().setUuid(send_uuid).setGender(filter_gender)
				.addAllRegion(Arrays.asList(filter_region_arr)).setNegate(filter_negate)
				.addAllSkipUuid(Arrays.asList(filter_skipUuid_arr)).build();

		return redSendService.sendGroup(send_uuid, number, money, isRandom, content, tick_uuid, fansFilterParam);
	}

	/**
	 * 查询红包发送历史
	 * 
	 * @param uuid
	 * @param idx
	 * @param count
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("send-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @QueryParam("idx") int idx, @QueryParam("count") int count)
			throws IOException {
		return redSendService.list(uuid, idx, count);
	}

}