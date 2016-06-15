/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.broadbrand;

/**
 * @author 
 * 
 */
public interface BroadbandConsts {
    String KEY_MCH_ID = "mch_id";
    String KEY_VERSION = "version";
    String KEY_MCH_ORDER_ID = "mch_order_id";
    String KEY_GAME_ID = "game_id";
    String KEY_PAR = "par";
    String KEY_CARD_NUM = "card_num";
    String KEY_CARD_PWD = "card_pwd";
    String KEY_RETURN_URL = "return_url";
    String KEY_SIGN = "sign";
    String KEY_DATE = "date";

    String KEY_ORDER_ID = "order_id";
    String KEY_CODE = "code";
    String KEY_CONSUME_PAR = "consume_par";
    String KEY_MESSAGE = "message";

    String VERSION = "V1.0";
    String NOTIFY_URL = "/ch/notify/broadband-txtong.do";

    String DESKEY = "deskey";
    String GAMEID = "11";
    String NOTIFYSUCCESS = "OK";

    /**
     * 新泛联游戏供货卡接口
     * 支付请求同步返回的状态码和对应的信息
     */
    public interface RETURNCODE {
        String CODE_100 = "100";
        String MSG_100 = "请求参数错误";
        String CODE_101 = "101";
        String MSG_101 = "提交成功";
        String CODE_103 = "103";
        String MSG_103 = "签名验证失败";
        String CODE_104 = "104";
        String MSG_104 = "游戏卡未开放";
        String CODE_105 = "105";
        String MSG_105 = "系统内部错误";
        String CODE_106 = "106";
        String MSG_106 = "订单已存在";
        String CODE_107 = "107";
        String MSG_107 = "IP地址验证失败";
        String CODE_110 = "110";
        String MSG_110 = "解密卡信息失败";
        String CODE_111 = "111";
        String MSG_111 = "游戏卡卡号或密码格式错误";
        String CODE_112 = "112";
        String MSG_112 = "游戏卡面值错误";
    }

    /**
     * 新泛联游戏供货卡接口
     * 异步通知和查询返回的状态码和描述
     */

    public interface NOTIFYCODE {
        String CODE_201 = "201";
        String MSG_201 = "消耗成功";
        String CODE_200 = "200";
        String MSG_200 = "消耗异常，未知结果";
        String CODE_203 = "203";
        String MSG_203 = "消耗失败，卡号，密码信息错误、卡余额不足等卡基本错误";
        String CODE_207 = "207";
        String MSG_207 = "消耗失败，官方系统错误";
        String CODE_208 = "208";
        String MSG_208 = "消耗失败，其他错误";
        String CODE_221 = "221";
        String MSG_221 = "处理中";
        String CODE_222 = "222";
        String MSG_222 = "无记录";
    }
}