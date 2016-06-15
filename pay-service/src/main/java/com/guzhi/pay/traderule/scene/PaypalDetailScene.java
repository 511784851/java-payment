/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.scene;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.UserTransInfo;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;
import com.guzhi.pay.traderule.rule.RuleValidator;

/**
 * @author Administrator
 *         paypal详细信息风险控制
 */
public class PaypalDetailScene extends AbstractSceneValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PaypalDetailScene.class);
    private List<RuleValidator> ruleValidatorList;
    @Autowired
    private DomainResource domainResource;

    @Override
    public List<RuleValidator> getRuleList() {
        return ruleValidatorList;
    }

    @Override
    public void beforeSceneValidator(Map<String, String> params) {
        UserTransInfo userTransInfo = this.buildUserTransInfo(params);
        int result = domainResource.createUserTransInfo(userTransInfo);
        if (result != 1) {
            LOG.error("Failed to create UserTransInfo ");
        }
    }

    @Override
    public void afterSceneValidator(Map<String, String> params) {
        UserTransInfo userTransInfo = this.assembleUpdateUserTransInfo(params);
        int result = domainResource.updateUserTransInfo(userTransInfo);
        if (result != 1) {
            LOG.error("Failed to update UserTransInfo ");
        }

    }

    public List<RuleValidator> getRuleValidatorList() {
        return ruleValidatorList;
    }

    public void setRuleValidatorList(List<RuleValidator> ruleValidatorList) {
        this.ruleValidatorList = ruleValidatorList;
    }

    /**
     * 组装更新的UserTransInfo
     * 
     * @param params
     * @return
     */
    private UserTransInfo assembleUpdateUserTransInfo(Map<String, String> params) {
        String appId = params.get(TradeRuleConsts.APPID);
        String appOrderId = params.get(TradeRuleConsts.APPORDERID);
        String status = Consts.FAIL.equalsIgnoreCase(params.get(TradeRuleConsts.STATUS)) ? Consts.FAIL : Consts.SUCCESS;
        String statusMsg = params.get(TradeRuleConsts.STATUS_MSG) == null ? "" : params.get(TradeRuleConsts.STATUS);
        UserTransInfo userTransInfo = new UserTransInfo();
        userTransInfo.setAppId(appId);
        userTransInfo.setAppOrderId(appOrderId);
        userTransInfo.setStatus(status);
        userTransInfo.setStatusMsg(statusMsg);
        userTransInfo.setLastUpdateTime(TimeHelper.get(8, new Date()));
        return userTransInfo;
    }

    /**
     * 组装一个usertransinfo对象
     * 
     * @param params
     * @return
     */
    private UserTransInfo buildUserTransInfo(Map<String, String> params) {
        String gbuid = params.get(TradeRuleConsts.gbUID);
        String account = params.get(TradeRuleConsts.PAYERID);
        String ip = params.get(TradeRuleConsts.IP);
        String address = params.get(TradeRuleConsts.ADDRESS);
        String amount = params.get(TradeRuleConsts.AMOUNT);
        String ext = params.get(TradeRuleConsts.EXT);
        String appId = params.get(TradeRuleConsts.APPID);
        String appOrderId = params.get(TradeRuleConsts.APPORDERID);
        String chId = params.get(TradeRuleConsts.CHID);
        String status = StringUtils.isEmpty(params.get(TradeRuleConsts.STATUS)) ? Consts.FAIL : params
                .get(TradeRuleConsts.STATUS);
        String statusMsg = params.get(TradeRuleConsts.STATUS_MSG) == null ? "" : params.get(TradeRuleConsts.STATUS);
        UserTransInfo userTransInfo = new UserTransInfo();
        userTransInfo.setgbuid(gbuid);
        userTransInfo.setAccount(account);
        userTransInfo.setIp(ip);
        userTransInfo.setAddress(address);
        userTransInfo.setPayTime(TimeHelper.get(8, new Date()));
        userTransInfo.setAmount(new BigDecimal(amount));
        userTransInfo.setExt(ext);
        userTransInfo.setAppId(appId);
        userTransInfo.setAppOrderId(appOrderId);
        userTransInfo.setChId(chId);
        userTransInfo.setStatus(status);
        userTransInfo.setStatusMsg(statusMsg);
        userTransInfo.setLastUpdateTime(TimeHelper.get(8, new Date()));
        return userTransInfo;
    }
}
