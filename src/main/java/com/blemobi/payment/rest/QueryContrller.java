package com.blemobi.payment.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.sql.SqlHelper;
import com.blemobi.payment.util.CommonUtil;
import com.blemobi.payment.util.ReslutUtil;
import com.blemobi.sep.probuf.PaymentProtos.PPayOrder;
import com.blemobi.sep.probuf.PaymentProtos.PPayOrderList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * @author andy.zhao@blemobi.com 支付订单查询类
 */
@Path("/query")
public class QueryContrller {
	/**
	 * 根据订单号查询订单信息
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param orderNo
	 *            要查询的订单号
	 * @return PMessage 返回PMessage数据
	 * @throws Exception
	 *             抛出Exception异常
	 */
	@GET
	@Path("payOrder")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage payOrder(@CookieParam("uuid") String uuid, @QueryParam("orderNo") String orderNo) throws Exception {
		Map<String, Object> map = SqlHelper.query(uuid, orderNo);
		if (map != null) {
			PPayOrder payOrder = createPayOrder(map);
			return ReslutUtil.createReslutMessage(payOrder);
		} else {
			return ReslutUtil.createErrorMessage(190011, "订单不存在");
		}
	}

	/**
	 * 批量查询用户支付订单信息
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param offset
	 *            批量查询起始值
	 * @param count
	 *            批量查询的数量
	 * @return PMessage 返回PMessage数据
	 * @throws Exception
	 *             抛出Exception异常
	 */
	@GET
	@Path("payOrders")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage payOrders(@CookieParam("uuid") String uuid, @QueryParam("offset") int offset,
			@QueryParam("count") int count) throws Exception {
		PPayOrderList.Builder payOrderListBuilder = PPayOrderList.newBuilder();

		List<Map<String, Object>> list = SqlHelper.queryList(uuid, offset, count);
		if (list != null) {
			for (Map<String, Object> map : list) {
				PPayOrder payOrder = createPayOrder(map);
				payOrderListBuilder.addPayOrders(payOrder);
			}
		}
		return ReslutUtil.createReslutMessage(payOrderListBuilder.build());
	}

	/**
	 * 生成PPayOrder数据
	 * 
	 * @param map
	 *            订单信息
	 * @return PPayOrder 返回PPayOrder数据
	 * @throws Exception
	 */
	private PPayOrder createPayOrder(Map<String, Object> map) {
		PPayOrder.Builder payOrderBuilder = PPayOrder.newBuilder().setOrderNo(CommonUtil.getMapValue(map, "order_no"))
				.setPayType(CommonUtil.getMapValue(map, "pay_type"))
				.setOrderSubject(CommonUtil.getMapValue(map, "orderSubject"))
				.setOrderBody(CommonUtil.getMapValue(map, "orderBody"))
				.setAmount(Long.parseLong(CommonUtil.getMapValue(map, "amount")))
				.setPayTime(Long.parseLong(CommonUtil.getMapValue(map, "pay_time")));

		int pay_statu = Integer.parseInt(CommonUtil.getMapValue(map, "pay_statu"));
		payOrderBuilder.setPayStatu(pay_statu);
		if (pay_statu != 0) {// 已支付
			payOrderBuilder.setBankType(CommonUtil.getMapValue(map, "bank_type"))
					.setTotalFee(Long.parseLong(CommonUtil.getMapValue(map, "total_fee")))
					.setErrCodeDes(CommonUtil.getMapValue(map, "err_code_des"))
					.setEndTime(Long.parseLong(CommonUtil.getMapValue(map, "end_time")));
		}

		return payOrderBuilder.build();
	}

}