/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;

/**
 * 用于校验针对业务的接入层的数据校验工作。<br>
 * 校验通过则无返回，否则抛出{@link PayException}<br>
 * 基于尽最大努力返回的目的，对返回的数据暂时不作较验，后续如果因为安全原因，可以再加上。<br>
 * 
 * @author administrator
 */
public class ValidateHelper {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validatePayOrderFields(PayOrder payOrder, @SuppressWarnings("rawtypes") Class validateGroup) {
        Set<ConstraintViolation<PayOrder>> validations = validator.validate(payOrder, validateGroup);
        if (validations == null || validations.size() < 1) {
            return;
        }

        String errorMsg = "";
        for (ConstraintViolation<PayOrder> violation : validations) {
            errorMsg = errorMsg + violation.getMessage() + " ";
        }
        if (StringUtils.isBlank(errorMsg)) {
            return;
        }
        throw new PayException(Consts.SC.DATA_ERROR, errorMsg, payOrder, null);
    }
}
