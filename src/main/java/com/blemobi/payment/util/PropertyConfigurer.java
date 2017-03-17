/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util
 *
 *    Filename:    PropHandler.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月13日 下午7:04:27
 *
 *    Revision:
 *
 *    2017年3月13日 下午7:04:27
 *
 *****************************************************************/
package com.blemobi.payment.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.blemobi.library.consul.BaseService;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName PropHandler
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月13日 下午7:04:27
 * @version 1.0.0
 */
@Log4j
public class PropertyConfigurer extends PropertyPlaceholderConfigurer {

    private static final List<String> DESKEYS = new ArrayList<String>();

    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        String kvVal = BaseService.getProperty(propertyName);
        if (!StringUtils.isEmpty(kvVal)) {
            log.info("----properties key:" + propertyName + ",OrgVal: " + propertyValue + ", KvVal:" + kvVal);
        }
        return StringUtils.isEmpty(kvVal) ? propertyValue : kvVal;
    }

    static {
        // DESKEYS.add("db.username");
        // DESKEYS.add("db.password");
    }
}
