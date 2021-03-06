package com.blemobi.payment.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.blemobi.library.exception.GrpcException;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

import lombok.extern.log4j.Log4j;

/**
 * 统一异常处理器
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Provider
public class ExceptionImpl implements ExceptionMapper<Exception> {
	/**
	 * 异常处理
	 */
	public Response toResponse(Exception e) {
		log.error("Payment server catch an exception, MSG=[" + e.getMessage() + "]");
		e.printStackTrace();
		PMessage msg = null;
		if(e instanceof BizException){
		    BizException ex = (BizException) e;
		    msg = ReslutUtil.createErrorMessage(ex.getErrCd(), ex.getMsg(), ex.getExtMsg());
		}else if(e instanceof GrpcException){
		    GrpcException ex = (GrpcException) e;
            msg = ReslutUtil.createErrorMessage(ex.getErrCd(), ex.getMsg(), ex.getExtMsg());
        }else{
		    msg = ReslutUtil.createErrorMessage(1001012, "系统繁忙");
		}
		return Response.ok(msg, MediaTypeExt.APPLICATION_PROTOBUF).status(200).build();
	}
}