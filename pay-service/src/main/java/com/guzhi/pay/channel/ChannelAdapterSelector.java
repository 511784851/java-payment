/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * adapter的命名规则为"channel.adapter.{chId}.{payMethod}"。
 * eg: "channel.adapter.Kq.Gate"
 * 
 * @author administrator
 */
@Service("channelAdapterSelector")
public class ChannelAdapterSelector implements ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public ChannelIF get(String chId, String payMethod) {
        String beanName = getAdapterName(chId, payMethod);
        return context.getBean(beanName, ChannelIF.class);
    }

    private String getAdapterName(String chId, String payMethod) {
        return chId.toLowerCase() + StringUtils.capitalize(payMethod.toLowerCase()) + "Adapter";
    }
}
