package com.blemobi.payment.util;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PResult;
import com.google.protobuf.GeneratedMessage;

import lombok.extern.log4j.Log4j;

/**
 * 返回PMessage数据封装类
 * 
 * @author 赵勇<andy.zhao@blemobi.com>
 */

@Log4j
public class ReslutUtil {
	
	private static final int magic = 9833;// 固定值
	private static final String errorType = "PResult";// 返回PResult数据

	/**
	 * 正确时返回具体的Protocol对象数据
	 * 
	 * @param Data
	 *            Protocol对象
	 * @return
	 */
	public static PMessage createReslutMessage(GeneratedMessage message) {
		char charSubClassKey = '$';
		char charPackage = '.';

		String packageAndClass = message.getClass().getName();

		int subIndex = packageAndClass.lastIndexOf(charSubClassKey);
		int packageIndex = packageAndClass.lastIndexOf(charPackage);

		int lassPoint = (subIndex > 0) ? subIndex : packageIndex;
		String className = packageAndClass.substring(lassPoint + 1);

		return createReslutMessage(className, message);
	}

	private static PMessage createReslutMessage(String type, GeneratedMessage message) {
		log.info("type：" + type + "；data：" + message);
		
		return PMessage.newBuilder()
				.setMagic(magic)
				.setType(type)
				.setData(message.toByteString())
				.build();
	}

	/**
	 * 错误时返回PResult数据（自定义错误码和错误描述）
	 * 
	 * @param errorCode
	 *            错误码
	 * @param errorMsg
	 *            错误简要描述
	 * @return
	 * @throws IOException
	 */
	public static PMessage createErrorMessage(int errorCode, String errorMsg, String extraInfo) {
		PResult result = PResult.newBuilder()
				.setErrorCode(errorCode)
				.setErrorMsg(errorMsg)
				.setExtraInfo(extraInfo)
				.build();

		return ReslutUtil.createReslutMessage(errorType, result);
	}

	public static PMessage createErrorMessage(int errorCode, String errorMsg) {
		return createErrorMessage(errorCode, errorMsg, "");
	}
	
	public static PMessage createSucceedMessage() {
		return createSucceedMessage("success");
	}
	public static PMessage createSucceedMessage(String errorMsg) {
		return createSucceedMessage(errorMsg, "");
	}
	public static PMessage createSucceedMessage(String errorMsg, String extraInfo) {
		return createErrorMessage(0, errorMsg, extraInfo);
	}
}
