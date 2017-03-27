package com.blemobi.payment.service.helper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import com.blemobi.library.client.ChatHttpClient;
import com.blemobi.library.grpc.NotifyGrpcClient;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.CommonUtil;
import com.blemobi.sep.probuf.NotificationApiProtos.PNotifyInternalMessage;
import com.blemobi.sep.probuf.NotificationApiProtos.PNotifyInternalMessageList;
import com.blemobi.sep.probuf.NotificationProtos.ENotifyType;
import com.blemobi.sep.probuf.NotificationProtos.PNotifyMessage;
import com.blemobi.sep.probuf.NotificationProtos.PNotifyRawMessage;
import com.blemobi.sep.probuf.NotificationProtos.PNotifySimple;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseGiftEx;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PResult;
import com.blemobi.sep.probuf.RobotApiProtos.PBLotteryNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PBRedPacketNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsgList;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotRawNotifyMsg;
import com.blemobi.sep.probuf.RobotProtos.ERobotPushType;

import lombok.extern.log4j.Log4j;

/**
 * 业务消息推送
 * 
 * @author zhaoyong
 */
@Log4j
public class PushMsgHelper {

	private String send_uuid;
	private String ord_no;
	private List<String> toList;
	private String desc;

	/**
	 * 构造方法
	 * 
	 * @param send_uuid
	 *            消息来源用户uuid
	 * @param ord_no
	 *            业务订单号
	 * @param toList
	 *            消息目标用户uuid
	 * @param desc
	 *            消息内容
	 */
	public PushMsgHelper(String send_uuid, String ord_no, List<String> toList, String desc) {
		super();
		this.send_uuid = send_uuid;
		this.ord_no = ord_no;
		this.toList = toList;
		this.desc = desc;
	}

	/**
	 * 构造方法
	 * 
	 * @param send_uuid
	 *            消息来源用户uuid
	 * @param ord_no
	 *            业务订单号
	 * @param desc
	 *            消息内容
	 */
	public PushMsgHelper(String send_uuid, String ord_no, String desc) {
		this.send_uuid = send_uuid;
		this.ord_no = ord_no;
		this.desc = desc;
	}

	/**
	 * 推送红包消息（智能机器人）
	 */
	public void redPacketMsg() {
		// 红包信息
		PBRedPacketNotifyMsg redPacketNotifyMsg = PBRedPacketNotifyMsg.newBuilder().setOrdNo(ord_no).setText(desc)
				.build();
		// 消息内容
		PRobotRawNotifyMsg robotRawNotifyMsg = PRobotRawNotifyMsg.newBuilder().setRedpacket(redPacketNotifyMsg).build();
		push(robotRawNotifyMsg, ERobotPushType.RedPacket);

	}

	/**
	 * 推送抽奖消息（智能机器人）
	 */
	public void lotteryMsg() {
		lotteryMsg(ERobotPushType.Lottery);
	}

	/**
	 * 推送抽奖消息（智能机器人）
	 * 
	 * ERobotPushType 枚举类型定义如下 
	 * Regards 息推送 
	 * Remind 生日提醒 
	 * Lottery 抽奖 
	 * RedPacket 红包
	 * Stream 直播 
	 * LotteryRemind 领奖提醒 
	 * LotteryExpire 抽奖过期提醒 
	 * LotteryCourier
	 */
	public void lotteryMsg(ERobotPushType type) {
		// 红包信息
		PBLotteryNotifyMsg lotteryNotifyMsg = PBLotteryNotifyMsg.newBuilder().setOrdNo(ord_no).setText(desc).build();
		// 消息内容
		PRobotRawNotifyMsg robotRawNotifyMsg = PRobotRawNotifyMsg.newBuilder().setLottery(lotteryNotifyMsg).build();
		push(robotRawNotifyMsg, type);

	}

	/**
	 * 推送（智能机器人）
	 * 
	 * @param robotRawNotifyMsg
	 *            消息内容
	 * @param robotPushType
	 *            消息类型
	 */
	private void push(PRobotRawNotifyMsg robotRawNotifyMsg, ERobotPushType robotPushType) {
		// 消息类型
		PRobotNotifyMsg robotNotifyMsg = null;
		if (toList != null && toList.size() > 0) {
			robotNotifyMsg = PRobotNotifyMsg.newBuilder().addAllTo(toList).setFrom(send_uuid).setMsgType(robotPushType)
					.setContent(robotRawNotifyMsg).build();
		} else {
			robotNotifyMsg = PRobotNotifyMsg.newBuilder().setFrom(send_uuid).setMsgType(robotPushType)
					.setContent(robotRawNotifyMsg).build();
		}
		// 批量消息
		PRobotNotifyMsgList robotNotifyMsgList = PRobotNotifyMsgList.newBuilder().addList(robotNotifyMsg).build();

		RobotGrpcClient client = new RobotGrpcClient();
		client.push(robotNotifyMsgList);
	}

	/**
	 * 抽奖发货提醒消息（系统）
	 */
	public void sendShipping() {
		StringBuffer url = new StringBuffer("payment://lottery/shipping?");
		url.append("lottery_id=");
		url.append(ord_no);
		send(url.toString());
	}

	/**
	 * 更新领奖资料提醒消息（系统）
	 * 
	 * @param userBaseGiftEx
	 */
	public void sendEditrcv(PUserBaseGiftEx userBaseGiftEx) {
		StringBuffer url = new StringBuffer("payment://lottery/editrcv?");
		url.append("uuid=");
		url.append(userBaseGiftEx.getInfo().getUUID());
		url.append("&lottery_id=");
		url.append(userBaseGiftEx.getGift().getGiftId());
		url.append("&gift_nm=");
		url.append(userBaseGiftEx.getGift().getGiftNm());
		url.append("&rcv_nm=");
		url.append(userBaseGiftEx.getRcvNm());
		url.append("&rcv_addr=");
		url.append(userBaseGiftEx.getRcvAddr());
		url.append("&rcv_phone=");
		url.append(userBaseGiftEx.getRcvPhone());
		url.append("&rcv_email=");
		url.append(userBaseGiftEx.getRcvEmail());
		url.append("&rcv_remark=");
		url.append(userBaseGiftEx.getRcvRemark());
		send(url.toString());
	}

	/**
	 * 推送（系统）
	 * 
	 * @param url
	 *            消息url已经参数
	 */
	@SuppressWarnings("deprecation")
	public void send(String url) {
		url = URLEncoder.encode(url);
		PNotifySimple notifySimple = PNotifySimple.newBuilder().setUri(url).build();

		PNotifyRawMessage notifyRawMessage = PNotifyRawMessage.newBuilder().setSimple(notifySimple).build();

		PNotifyMessage notifyMessage = PNotifyMessage.newBuilder().setType(ENotifyType.SimpleMessage)
				.setTime(System.currentTimeMillis()).setContent(notifyRawMessage).build();

		PNotifyInternalMessage notifyInternalMessage = PNotifyInternalMessage.newBuilder().setStateless(true)
				.setService("payment").setMessage(notifyMessage).addAllRecipient(toList).build();

		PNotifyInternalMessageList request = PNotifyInternalMessageList.newBuilder().addList(notifyInternalMessage)
				.build();

		NotifyGrpcClient client = new NotifyGrpcClient();
		client.send(request);
	}

	/**
	 * 实物抽奖即将过期未领取推送消息（阿里）
	 * 
	 * @param conent
	 *            消息提示内容
	 * @throws IOException
	 */
	public void pushOver(String conent) throws IOException {
		Map<String, String> info = new HashMap<String, String>();
		info.put("MsgType", "ov");
		info.put("Title", "BB");
		info.put("Id", ord_no);

		String infoString = JSONObject.toJSONString(info);

		Map<String, String> param = new HashMap<String, String>();
		param.put("info", infoString);

		String receiverUUIDs = CommonUtil.collectionToString(toList);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("receiverUUIDs", receiverUUIDs));
		params.add(new BasicNameValuePair("title", "BB"));
		params.add(new BasicNameValuePair("msgid", "ov"));
		params.add(new BasicNameValuePair("description", "BB"));
		params.add(new BasicNameValuePair("alertContent", conent));
		params.add(new BasicNameValuePair("info", infoString));

		ChatHttpClient httpClient = new ChatHttpClient();
		PMessage message = httpClient.multi(params);
		PResult result = PResult.parseFrom(message.getData());
		if (result.getErrorCode() != 0) {
			log.error("推送消息失败  -> " + result.getErrorCode());
		}
	}
}
