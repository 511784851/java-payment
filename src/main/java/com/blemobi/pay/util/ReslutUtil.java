package com.blemobi.pay.util;

import com.blemobi.demo.probuf.ResultProtos.PMessage;
import com.blemobi.demo.probuf.ResultProtos.PResult;
import com.google.protobuf.GeneratedMessage;

import lombok.extern.log4j.Log4j;

/**
 * @author 赵勇<andy.zhao@blemobi.com>
 * 返回PMessage数据封装类
 */

@Log4j
public class ReslutUtil {
	
	private static final int magic = 9833;// 固定值
	private static final String errorType = "PResult";// 返回PResult数据

	/**
	 * 返回Protocol对象数据
	 * @param message message对象
	 * @return PMessage 返回message对象数据
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

	/**
	 * 返回的Protocol对象数据
	 * @param type message名称
	 * @param message message对象
	 * @return PMessage 返回message对象数据
	 */
	private static PMessage createReslutMessage(String type, GeneratedMessage message) {
		log.info("type：" + type + "；data：" + message);
		
		return PMessage.newBuilder()
				.setMagic(magic)
				.setType(type)
				.setData(message.toByteString())
				.build();
	}

	/**
	 * 返回PResult数据
	 * @param errorCode 错误码
	 * @param errorMsg 错误简要描述
	 * @param extraInfo 扩展信息
	 * @return PMessage 返回message对象数据
	 */
	public static PMessage createErrorMessage(int errorCode, String errorMsg, String extraInfo) {
		PResult result = PResult.newBuilder()
				.setErrorCode(errorCode)
				.setErrorMsg(errorMsg)
				.setExtraInfo(extraInfo)
				.build();

		return ReslutUtil.createReslutMessage(errorType, result);
	}

	/**
	 * 返回PResult数据
	 * @param errorCode 错误码
	 * @param errorMsg 错误简要描述
	 * @return PMessage 返回message对象数据
	 */
	public static PMessage createErrorMessage(int errorCode, String errorMsg) {
		return createErrorMessage(errorCode, errorMsg, "");
	}
	
	/**
	 * 成功时返回PResult数据
	 * @return PMessage 返回message对象数据
	 */
	public static PMessage createSucceedMessage() {
		return createSucceedMessage("success");
	}
	
	/**
	 * 成功时返回PResult数据
	 * @param errorMsg 错误简要描述
	 * @return PMessage 返回message对象数据
	 */
	public static PMessage createSucceedMessage(String errorMsg) {
		return createSucceedMessage(errorMsg, "");
	}
	
	/**
	 * 成功时返回PResult数据
	 * @param errorMsg 简要描述
	 * @param extraInfo 扩展信息
	 * @return PMessage 返回message对象数据
	 */
	public static PMessage createSucceedMessage(String errorMsg, String extraInfo) {
		return createErrorMessage(0, errorMsg, extraInfo);
	}
}
