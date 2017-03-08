package com.blemobi.payment.service.helper;

import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.sep.probuf.RobotApiProtos.PBLotteryNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PBRedPacketNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsgList;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotRawNotifyMsg;
import com.blemobi.sep.probuf.RobotProtos.ERobotPushType;

/**
 * 业务消息推送
 * 
 * @author zhaoyong
 *
 */
public class PushMsgHelper {
	private String send_uuid;
	private String ord_no;

	/**
	 * 构造方法
	 * 
	 * @param send_uuid
	 *            消息来源用户uuid
	 * @param ord_no
	 *            业务订单号
	 * @param targetKey
	 *            OTS存储的KEY
	 */
	public PushMsgHelper(String send_uuid, String ord_no) {
		this.send_uuid = send_uuid;
		this.ord_no = ord_no;
	}

	/**
	 * 推送红包消息
	 */
	public void redPacketMsg() {
		// 红包信息
		PBRedPacketNotifyMsg redPacketNotifyMsg = PBRedPacketNotifyMsg.newBuilder().setOrdNo(ord_no).build();
		// 消息内容
		PRobotRawNotifyMsg robotRawNotifyMsg = PRobotRawNotifyMsg.newBuilder().setRedpacket(redPacketNotifyMsg).build();
		push(robotRawNotifyMsg, ERobotPushType.RedPacket);

	}

	/**
	 * 推送抽奖消息
	 */
	public void lotteryMsg() {
		// 红包信息
		PBLotteryNotifyMsg lotteryNotifyMsg = PBLotteryNotifyMsg.newBuilder().setOrdNo(ord_no).build();
		// 消息内容
		PRobotRawNotifyMsg robotRawNotifyMsg = PRobotRawNotifyMsg.newBuilder().setLottery(lotteryNotifyMsg).build();
		push(robotRawNotifyMsg, ERobotPushType.Lottery);

	}

	/**
	 * 推送
	 * 
	 * @param robotRawNotifyMsg
	 *            消息内容
	 * @param robotPushType
	 *            消息类型
	 */
	private void push(PRobotRawNotifyMsg robotRawNotifyMsg, ERobotPushType robotPushType) {
		// 消息类型
		PRobotNotifyMsg robotNotifyMsg = PRobotNotifyMsg.newBuilder().setFrom(send_uuid).setMsgType(robotPushType)
				.setContent(robotRawNotifyMsg).build();
		// 批量消息
		PRobotNotifyMsgList robotNotifyMsgList = PRobotNotifyMsgList.newBuilder().addList(robotNotifyMsg).build();

		RobotGrpcClient client = new RobotGrpcClient();
		client.push(robotNotifyMsgList);
	}
}
