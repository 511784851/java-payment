package com.blemobi.payment.rest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.MediaTypeExt;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;

/**
 * 用户授权接口
 * 
 * @author zhaoyong
 *
 */
@Path("v1/payment/user")
public class UserProcess {

	/**
	 * 获取用户thirdToken
	 * 
	 * @param uuid
	 * @return
	 */
	@GET
	@Path("thirdToken")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage thirdToken(@CookieParam("uuid") String uuid) {
		SignHelper signHelper = new SignHelper(uuid);
		String sign = signHelper.getThirdToken();
		PStringSingle stringSingle = PStringSingle.newBuilder().setVal(sign).build();
		return ReslutUtil.createReslutMessage(stringSingle);
	}
}