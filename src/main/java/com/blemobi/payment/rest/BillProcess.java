package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.BillService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 账单接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/bill")
public class BillProcess {

	// @Autowired
	private BillService billService = InstanceFactory.getInstance(BillService.class);

	/**
	 * 查询账单
	 * 
	 * @param uuid
	 * @param type
	 *            类型（0-收入 1-支出）
	 * @param idx
	 * @param count
	 * @return
	 */
	@GET
	@Path("info-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage list(@CookieParam("uuid") String uuid, @QueryParam("type") int type, @QueryParam("idx") int idx,
			@QueryParam("count") int count) {
		return billService.list(uuid, type, idx, count);
	}

}