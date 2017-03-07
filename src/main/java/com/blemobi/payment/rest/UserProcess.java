package com.blemobi.payment.rest;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.library.util.MD5;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.service.UserService;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

import lombok.extern.log4j.Log4j;

/**
 * 用户授权接口
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Path("v1/payment/user")
public class UserProcess {

	private UserService userService = InstanceFactory.getInstance(UserService.class);

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
		String sign = MD5.GetMD5Code(uuid + "2uZCpuScM6Fko");
		PStringSingle stringSingle = PStringSingle.newBuilder().setVal(sign).build();
		return ReslutUtil.createReslutMessage(stringSingle);
	}

	/**
	 * 获取用户thirdToken
	 * 
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("celebrity-list")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage celebrity(@CookieParam("uuid") String uuid, @QueryParam("keyword") String keyword)
			throws IOException {
		return userService.celebrity(uuid, keyword);
	}
}