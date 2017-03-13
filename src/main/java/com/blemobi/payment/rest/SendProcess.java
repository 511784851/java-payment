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

import com.blemobi.payment.service.SendService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.common.base.Strings;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 发红包接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/redEnve")
public class SendProcess {

	private SendService sendService = InstanceFactory.getInstance(SendService.class);

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
	 */
	@POST
	@Path("send-ordin")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage sendOrdinary(@CookieParam("uuid") String send_uuid, @FormParam("money") int money,
			@FormParam("content") String content, @FormParam("rece_uuid") String rece_uuid) {
		return sendService.sendOrdinary(send_uuid, money, content, rece_uuid);
	}

	/**
	 * 发群红包
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param number
	 *            数量
	 * @param money
	 *            金额（分）
	 * @param isRandom
	 *            是否随机红包
	 * @param content
	 *            描述
	 * @param tick_uuid
	 *            勾选的用户
	 * @param filter_gender
	 *            性别过滤
	 * @param filter_region
	 *            地区过滤
	 * @param filter_negate
	 *            是否filter_region取反
	 * @param filter_skipUuid
	 *            反选掉的用户
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("send-group")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage sendGroup(@CookieParam("uuid") String send_uuid, @FormParam("number") int number,
			@FormParam("money") int money, @FormParam("isRandom") boolean isRandom,
			@FormParam("content") String content, @FormParam("tick_uuid") String tick_uuid,
			@FormParam("filter_gender") int filter_gender, @FormParam("filter_region") String filter_region,
			@FormParam("filter_negate") boolean filter_negate, @FormParam("filter_skipUuid") String filter_skipUuid)
			throws IOException {

		PFansFilterParam fansFilterParam = null;
		if (Strings.isNullOrEmpty(tick_uuid))
			fansFilterParam = buildFilterParam(send_uuid, filter_gender, filter_region, filter_negate, filter_skipUuid);

		return sendService.sendGroup(send_uuid, number, money, isRandom, content, tick_uuid, fansFilterParam);
	}

	/**
	 * 查询红包发送历史
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("send-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String send_uuid, @QueryParam("idx") int idx,
			@QueryParam("count") int count) throws IOException {
		return sendService.list(send_uuid, idx, count);
	}

	/**
	 * 构建PFansFilterParam对象
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param filter_gender
	 *            性别过滤
	 * @param filter_region
	 *            地区过滤
	 * @param filter_negate
	 *            是否filter_region取反
	 * @param filter_skipUuid
	 *            反选掉的用户
	 * @return
	 */
	private PFansFilterParam buildFilterParam(String send_uuid, int filter_gender, String filter_region,
			boolean filter_negate, String filter_skipUuid) {

		PFansFilterParam.Builder fansFilterParam = PFansFilterParam.newBuilder().setUuid(send_uuid)
				.setGender(filter_gender).setNegate(filter_negate);

		if (!Strings.isNullOrEmpty(filter_region)) {
			String[] filter_region_arr = filter_region.split(",");
			fansFilterParam.addAllRegion(Arrays.asList(filter_region_arr));
		}
		if (!Strings.isNullOrEmpty(filter_skipUuid)) {
			String[] filter_skipUuid_arr = filter_skipUuid.split(",");
			fansFilterParam.addAllSkipUuid(Arrays.asList(filter_skipUuid_arr));
		}

		return fansFilterParam.build();
	}
}