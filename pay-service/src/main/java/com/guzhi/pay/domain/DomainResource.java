/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.MemoryData;
import com.guzhi.pay.mapper.AppChInfoMapper;
import com.guzhi.pay.mapper.AppInfoMapper;
import com.guzhi.pay.mapper.ChBankMapper;
import com.guzhi.pay.mapper.PayOrderMapper;
import com.guzhi.pay.mapper.SmsOrderMapper;
import com.guzhi.pay.mapper.TaskMapper;
import com.guzhi.pay.mapper.UserAccountLimitMapper;
import com.guzhi.pay.mapper.UserTransInfoMapper;

/**
 * Domain资源的总入口，为了便于将数据层抽离成一个独立的服务，所有针对Domain资源的操作均应该集中到这里。
 * 
 * @author administrator
 */
@Service("domainResource")
public class DomainResource {
    @Autowired
    private PayOrderMapper payOrderMapper;
    @Autowired
    private AppChInfoMapper appChInfoMapper;
    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ChBankMapper chBankMapper;
    @Autowired
    private UserTransInfoMapper userTransInfoMapper;
    @Autowired
    private UserAccountLimitMapper userAccountLimitMapper;
    @Autowired
    private SmsOrderMapper smsOrderMapper;
    // 设置的有效时间
    private static final long EXPIRED_INTERVAL = 5000;

    private static final String SEP = ".";
    private static final String PREFIX_APP_INFO = "PREFIX_APP_INFO:";
    private static final String PREFIX_APP_CH_INFO = "PREFIX_APP_CH_INFO:";
    private static final Logger LOG = LoggerFactory.getLogger(DomainResource.class);

    @Value("${tpayKey}")
    private String tpayKey;

    /**
     * 可能的场景:
     * <ul>
     * <li>appId非空，chId非空，payMethod非空：普通支付的情况
     * <li>appId非空，chId非空，payMethod为空：Refund的情况
     * <li>appId非空，chId为空，payMethod非空：普通支付，但业务拥有多个渠道，根据权重选择一个（选择的逻辑不在这里）
     * <li>其它情况均为非法
     * </ul>
     */
    public List<AppChInfo> getAppChInfo(String appId, String chId, String payMethod) {
        // 安全检查
        if (StringUtils.isBlank(appId)) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "appId should not be empty!");
        }
        if (StringUtils.isBlank(chId) && StringUtils.isBlank(payMethod)) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "chId/payMethod should not empty at same time!");
        }

        // 从缓存中找
        String key = PREFIX_APP_CH_INFO + appId + SEP + chId + SEP + payMethod;
        @SuppressWarnings("unchecked")
        List<AppChInfo> chInfos = (List<AppChInfo>) MemoryData.get(key);

        // 从DB中找。注意：如果从DB中查出来是空的，存不存缓存，每次请求都会查DB
        if (CollectionUtils.isEmpty(chInfos)) {
            chInfos = findAppChInfoInDb(appId, chId, payMethod);
            MemoryData.put(key, chInfos, new Date(System.currentTimeMillis() + EXPIRED_INTERVAL));
        }

        return chInfos;
    }

    /**
     * 在{@link #getAppChInfo(String appId, String chId,String payMethod)}
     * 基础上筛选出有bankId的appChInfo
     * 
     * @param appId
     * @param chId
     * @param payMethod
     * @param bankId
     * @return
     */
    public List<AppChInfo> getAppChInfo(String appId, String chId, String payMethod, String bankId) {
        List<AppChInfo> chInfos = getAppChInfo(appId, chId, payMethod);

        List<String> chIds = new ArrayList<String>();
        for (AppChInfo chInfo : chInfos) {
            chIds.add("'" + chInfo.getChId() + "'");
        }

        List<String> existChIds = chBankMapper.getChIds(StringUtils.join(chIds, ","), bankId);

        List<AppChInfo> result = new ArrayList<AppChInfo>();
        for (AppChInfo chInfo : chInfos) {
            if (existChIds.contains(chInfo.getChId().toLowerCase())) {
                result.add(chInfo);
            }
        }
        return result;
    }

    /**
     * 对查出appchinfo 密码解密
     * 
     * @param chInfos
     * @return
     */
    private List<AppChInfo> decryptAppChInfo(List<AppChInfo> chInfos) {
        for (AppChInfo appChInfo : chInfos) {
            try {
                if (StringUtils.isNotBlank(appChInfo.getChPayKeyMd5())) {
                    appChInfo.setChPayKeyMd5(DESEncrypt.decryptByAES(tpayKey, appChInfo.getChPayKeyMd5()));
                }
                if (StringUtils.isNotBlank(appChInfo.getChAccountsKeyMd5())) {
                    appChInfo.setChAccountsKeyMd5(DESEncrypt.decryptByAES(tpayKey, appChInfo.getChAccountsKeyMd5()));
                }
                if (StringUtils.isNotBlank(appChInfo.getAdditionalInfo())) {
                    appChInfo.setAdditionalInfo(DESEncrypt.decryptByAES(tpayKey, appChInfo.getAdditionalInfo()));
                }
            } catch (Throwable e) {
                LOG.error("decrypt error appid:{},chid:{},paymethod:{}", appChInfo.getAppId(), appChInfo.getChId(),
                        appChInfo.getPayMethod(), e);
            }
        }
        return chInfos;
    }

    /**
     * 对查出appchinfo 密码加密
     * 
     * @param chInfos
     * @return
     */
    private void encryptAppChInfo(AppChInfo appChInfo) {
        try {
            if (StringUtils.isNotBlank(appChInfo.getChPayKeyMd5())) {
                appChInfo.setChPayKeyMd5(DESEncrypt.encryptByAES(tpayKey, appChInfo.getChPayKeyMd5()));
            }
            if (StringUtils.isNotBlank(appChInfo.getChAccountsKeyMd5())) {
                appChInfo.setChAccountsKeyMd5(DESEncrypt.encryptByAES(tpayKey, appChInfo.getChAccountsKeyMd5()));
            }
            if (StringUtils.isNotBlank(appChInfo.getAdditionalInfo())) {
                appChInfo.setAdditionalInfo(DESEncrypt.encryptByAES(tpayKey, appChInfo.getAdditionalInfo()));
            }
        } catch (Throwable e) {
            LOG.error("encrypt error appid:{},chid:{},paymethod:{}", appChInfo.getAppId(), appChInfo.getChId(),
                    appChInfo.getPayMethod(), e);
        }
    }

    /**
     * 当查找不到对应的appChInfo时，会尝试用通用的渠道
     */
    private List<AppChInfo> findAppChInfoInDb(String appId, String chId, String payMethod) {
        List<AppChInfo> chInfos = null;

        if (StringUtils.isBlank(chId)) {
            chInfos = appChInfoMapper.getAppChInfoWithPayMethod(appId, payMethod);
            if (CollectionUtils.isEmpty(chInfos)) {
                chInfos = appChInfoMapper.getAppChInfoWithPayMethod(AppInfo.COMMON_APPID, payMethod);
            }
            return decryptAppChInfo(chInfos);
        }

        if (StringUtils.isBlank(payMethod)) {
            chInfos = appChInfoMapper.getAppChInfoWithChId(appId, chId);
            if (CollectionUtils.isEmpty(chInfos)) {
                chInfos = appChInfoMapper.getAppChInfoWithChId(AppInfo.COMMON_APPID, chId);
            }
            return decryptAppChInfo(chInfos);
        }

        chInfos = appChInfoMapper.getAppChInfoWithPayMethodChId(appId, chId, payMethod);
        if (CollectionUtils.isEmpty(chInfos)) {
            chInfos = appChInfoMapper.getAppChInfoWithPayMethodChId(AppInfo.COMMON_APPID, chId, payMethod);
        }
        return decryptAppChInfo(chInfos);
    }

    public AppInfo getAppInfo(String appId) {
        String key = PREFIX_APP_INFO + appId;
        AppInfo appInfo = (AppInfo) MemoryData.get(key);
        if (appInfo == null) {
            // 注意：如果从DB中查出来是空的，存不存缓存，每次请求都会查DB
            appInfo = appInfoMapper.getAppInfo(appId);
            decryptAppInfo(appInfo);
            MemoryData.put(key, appInfo, new Date(System.currentTimeMillis() + EXPIRED_INTERVAL));
        }
        return appInfo;
    }

    /**
     * 对appinfo 密码进行解密
     * 
     * @param appInfo
     */
    private void decryptAppInfo(AppInfo appInfo) {
        try {
            if (StringUtils.isNotBlank(appInfo.getKey())) {
                appInfo.setKey(DESEncrypt.decryptByAES(tpayKey, appInfo.getKey()));
            }
            if (StringUtils.isNotBlank(appInfo.getPasswdKey())) {
                appInfo.setPasswdKey(DESEncrypt.decryptByAES(tpayKey, appInfo.getPasswdKey()));
            }
        } catch (Throwable e) {
            LOG.error("decrypt error appid:{},chid:{},paymethod:{}", appInfo.getAppId(), e);
        }
    }

    /**
     * 对appinfo 密码进行解密
     * 
     * @param appInfo
     */
    private List<AppInfo> decryptAppInfos(List<AppInfo> appInfos) {
        for (AppInfo appInfo : appInfos) {
            try {
                if (StringUtils.isNotBlank(appInfo.getKey())) {
                    appInfo.setKey(DESEncrypt.decryptByAES(tpayKey, appInfo.getKey()));
                }
                if (StringUtils.isNotBlank(appInfo.getPasswdKey())) {
                    appInfo.setPasswdKey(DESEncrypt.decryptByAES(tpayKey, appInfo.getPasswdKey()));
                }
            } catch (Throwable e) {
                LOG.error("decrypt error appid:{},chid:{},paymethod:{}", appInfo.getAppId(), e);
            }
        }
        return appInfos;
    }

    /**
     * 对appinfo 密码进行加密
     * 
     * @param appInfo
     */
    private void encryptAppInfo(AppInfo appInfo) {
        try {
            if (StringUtils.isNotBlank(appInfo.getKey())) {
                appInfo.setKey(DESEncrypt.encryptByAES(tpayKey, appInfo.getKey()));
            }
            if (StringUtils.isNotBlank(appInfo.getPasswdKey())) {
                appInfo.setPasswdKey(DESEncrypt.encryptByAES(tpayKey, appInfo.getPasswdKey()));
            }
        } catch (Throwable e) {
            LOG.error("encrypt error appid:{},chid:{},paymethod:{}", appInfo.getAppId(), e);
        }
    }

    public int createTask(Task task) {
        int result = 0;
        try {
            result = taskMapper.createTask(task);
        } catch (Exception e) {
            // TODO: if "already exist" just need a debug log, since by design
            // we not need duplicated notification
            LOG.warn("Failed to create task (To improve: this might not a error): " + task, e);
        }
        return result;
    }

    public Task getTask(String appId, String appOrderId, String type) {
        Task task = taskMapper.get(appId, appOrderId, type);
        return task;
    }

    public Task getOneExcutableTask(int maxTimes) {
        return taskMapper.getOneExecutableTask(maxTimes);
    }

    public Task getOneExcutableTaskByType(String type, int maxTimes) {
        return taskMapper.getOneExecutableTaskByType(type, maxTimes);
    }

    public Task getOneExcutableTaskUnType(String type, int maxTimes) {
        return taskMapper.getOneExecutableTaskByUnType(type, maxTimes);
    }

    public List<Task> getExcutableTaskByType(String type, int maxTimes, int size) {
        return taskMapper.getExecutableTaskByType(type, maxTimes, size);
    }

    public List<Task> getExcutableTaskUnType(String type, int maxTimes, int size) {
        return taskMapper.getExecutableTaskByUnType(type, maxTimes, size);
    }

    public int updateTaskToOccupied(String appId, String appOrderId, String taskType, int retryTimes) {

        // return jdbcTempltate.update(UPATE_TASK_TO_OCCUPIED, appId,
        // appOrderId, taskType);
        return taskMapper.updateTaskToOccupied(appId, appOrderId, taskType, retryTimes);
    }

    public int updateTaskForReplacement(Task task) {
        LOG.debug("updateTaskForReplacement, task={}", task);
        return taskMapper.updateTaskForReplacement(task);
    }

    public void refreshTaskWithDelay(String appId, String appOrderId, String type, Date date) {
        taskMapper.updateTaskDelay(appId, appOrderId, type, date);
    }

    public int createPayOrder(PayOrder payOrder) {
        LOG.debug("createPayOrder, payOrder={}", payOrder);
        if (StringUtils.isBlank(payOrder.getSubmitTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("gbgbMM");
            return payOrderMapper.createPayOrder(payOrder, "_" + sdf.format(new Date()));
        }
        return payOrderMapper.createPayOrder(payOrder, "_" + StringUtils.substring(payOrder.getSubmitTime(), 0, 6));
    }

    public PayOrder getPayOrder(String appId, String appOrderId) {
        LOG.debug("getPayOrder, appId={}, appOrderId={}", appId, appOrderId);
        SimpleDateFormat sdf = new SimpleDateFormat("gbgbMM");
        Calendar calendar = Calendar.getInstance();
        PayOrder order = payOrderMapper.getPayOrder(appId, appOrderId, "_" + sdf.format(calendar.getTime()));
        if (order == null) {
            calendar.add(Calendar.MONTH, -1);
            order = payOrderMapper.getPayOrder(appId, appOrderId, "_" + sdf.format(calendar.getTime()));
        }
        if (order == null) {
            order = payOrderMapper.getPayOrder(appId, appOrderId, "");
        }
        return order;
    }

    public PayOrder getPayOrder(String chDealId) {
        LOG.debug("getPayOrder, chDealId={}", chDealId);
        SimpleDateFormat sdf = new SimpleDateFormat("gbgbMM");
        Calendar calendar = Calendar.getInstance();
        PayOrder order = payOrderMapper.getPayOrderByChDealId(chDealId, "_" + sdf.format(calendar.getTime()));
        if (order == null) {

            calendar.add(Calendar.MONTH, -1);
            order = payOrderMapper.getPayOrderByChDealId(chDealId, "_" + sdf.format(calendar.getTime()));
        }
        if (order == null) {
            order = payOrderMapper.getPayOrderByChDealId(chDealId, "");
        }
        return order;
    }

    public int updatePayOrder(PayOrder payOrder) {
        LOG.debug("updatePayOrder, payOrder={}", payOrder);
        String suffix = "";
        if (StringUtils.isNotBlank(payOrder.getSubmitTime())) {
            suffix = "_" + StringUtils.substring(payOrder.getSubmitTime(), 0, 6);
        }
        int result = payOrderMapper.updatePayOrder(payOrder, suffix);
        if (result == 0) {
            result = payOrderMapper.updatePayOrder(payOrder, "");
            suffix = "";
        }
        if (result == 0) {
            PayOrder payOrderInDb = getPayOrder(payOrder.getAppId(), payOrder.getAppOrderId());
            suffix = "_" + StringUtils.substring(payOrderInDb.getSubmitTime(), 0, 6);
        }
        result = payOrderMapper.updatePayOrder(payOrder, suffix);
        if (!StringUtils.isBlank(payOrder.getExt())) {
            payOrderMapper.updateExt(payOrder, suffix);
        }
        return result;
    }

    public int updatePayOrderExt(PayOrder payOrder) {
        String suffix = "";
        if (StringUtils.isNotBlank(payOrder.getSubmitTime())) {
            suffix = "_" + StringUtils.substring(payOrder.getSubmitTime(), 0, 6);
        }
        int result = payOrderMapper.updateExt(payOrder, suffix);
        if (result == 0) {
            result = payOrderMapper.updateExt(payOrder, "");
            suffix = "";
        }
        if (result == 0) {
            PayOrder payOrderInDb = getPayOrder(payOrder.getAppId(), payOrder.getAppOrderId());
            suffix = "_" + StringUtils.substring(payOrderInDb.getSubmitTime(), 0, 6);
        }
        result = payOrderMapper.updateExt(payOrder, suffix);
        return result;

    }

    public int updateSmsOrder(SmsOrder smsOrder) {
        LOG.debug("updatePayOrder, payOrder={}", smsOrder);
        int result = smsOrderMapper.updateSmsOrder(smsOrder);
        if (result != 1) {
            List<SmsOrder> lists = smsOrderMapper.getSmsOrderByChOrderId(smsOrder.getChOrderId());
            // if (smsOrderInDbs.size())
            throw new PayException(Consts.SC.DATA_ERROR, "update smsOrder fail, specially check lastUpdateTime field!"
                    + " SmsOrderToUpdate=" + smsOrder + ", smsOrderInDb=" + lists);
        }
        return result;
    }

    public int deleteTask(String appId, String appOrderId, String type) {
        LOG.debug("deleteTask, appId={}, appOrderId={}, type={}", appId, appOrderId, type);
        return taskMapper.deleteTask(appId, appOrderId, type);
    }

    /**
     * @param task
     */
    public int deleteTask(Task task) {
        return deleteTask(task.getAppId(), task.getAppOrderId(), task.getType());
    }

    public ChBank getBank(String chId, String bankId) {
        return chBankMapper.get(chId, bankId);
    }

    public String getHisTotalAmount(String gbuid, String chId) {
        return userTransInfoMapper.getHisTotalAmount(gbuid, chId);
    }

    public String getHisTotalAmountByTime(String gbuid, String chId, String startTime, String endTime) {
        return userTransInfoMapper.getHisTotalAmountByTime(gbuid, chId, startTime, endTime);
    }

    public UserAccountLimit getUserAccount(String account, String chId, String type, String startTime, String endTime) {
        List<UserAccountLimit> userAccountList = userAccountLimitMapper.getUserAccount(account, chId, type, startTime,
                endTime);
        if ((userAccountList != null) && (userAccountList.size() > 0)) {
            return userAccountList.get(0);
        }
        return null;
    }

    public int getAccountNumberByTime(String account, String startTime, String endTime) {
        return userTransInfoMapper.getAccountNumberByTime(account, startTime, endTime);
    }

    public String getTotalAmountByTime(String account, String startTime, String endTime) {
        return userTransInfoMapper.getTotalAmountByTime(account, startTime, endTime);
    }

    public int getgbCrospPaypal(String gbuid, String chId, String startTime, String endTime, String exAccount) {
        return userTransInfoMapper.getgbCrospPaypal(gbuid, chId, startTime, endTime, exAccount);
    }

    public int getIpCrospPaypal(String ip, String chId, String startTime, String endTime, String exAccount) {
        return userTransInfoMapper.getIpCrospPaypal(ip, chId, startTime, endTime, exAccount);
    }

    public int getPaypalCrospIp(String ip, String chId, String startTime, String endTime, String exAccount) {
        return userTransInfoMapper.getPaypalCrospIp(ip, chId, startTime, endTime, exAccount);
    }

    public int getPayTimesByEndTime(String gbuid, String chId, String endTime) {
        return userTransInfoMapper.getPayTimesByEndTime(gbuid, chId, endTime);
    }

    public int createUserTransInfo(UserTransInfo userTransInfo) {
        int result = userTransInfoMapper.createUserTransInfo(userTransInfo);
        return result;
    }

    public int updateUserTransInfo(UserTransInfo userTransInfo) {
        int result = userTransInfoMapper.updateUserTransInfo(userTransInfo);
        return result;
    }

    public int createUserAccountLimit(UserAccountLimit userAccountLimit) {
        int result = userAccountLimitMapper.createUserAccountLimit(userAccountLimit);
        return result;
    }

    public int getBlackAccountNumber(String account, String chId, String type) {
        int result = userAccountLimitMapper.getBlackAccountNumber(account, chId, type);
        return result;
    }

    // public int updateBankDealId(String appId, String appOrderId, String
    // bankDealId) {
    // return payOrderMapper.updateBankDealId(appId, appOrderId, bankDealId);
    // }
    // public Task getOneExcutableQueryTask() {
    // return taskMapper.getOneExecutableQueryTask();
    // }
    // public Task getOneExcutableNotifyTask() {
    // return taskMapper.getOneExecutableNotifyTask();
    // }
    // public boolean hasTask(String appId, String appOrderId, String type) {
    // return taskMapper.hasTask(appId, appOrderId, type) >= 1;
    // }

    /**
     * @author administrator
     * @param appChInfo
     * @return
     */
    public int createAppChInfo(AppChInfo appChInfo) {
        LOG.debug("createAppChInfo, appChInfo={}", appChInfo);
        encryptAppChInfo(appChInfo);
        return appChInfoMapper.createAppChInfo(appChInfo);
    }

    /**
     * @author administrator
     * @return
     */
    public List<AppChInfo> getAppChInfos() {
        LOG.debug("getAppChInfos");
        List<AppChInfo> appChInfos = appChInfoMapper.getAppChInfos();
        return decryptAppChInfo(appChInfos);
    }

    /**
     * @author administrator
     * @param appChInfo
     * @return
     */
    public int updateAppChInfo(AppChInfo appChInfo) {
        LOG.debug("updateAppChInfo, appChInfo={}", appChInfo);
        encryptAppChInfo(appChInfo);
        return appChInfoMapper.updateAppChInfo(appChInfo);
    }

    /**
     * @author administrator
     * @param appId
     * @param chId
     * @param payMethod
     * @return
     */
    public int deleteAppChInfo(String appId, String chId, String payMethod) {
        LOG.debug("deleteAppChInfo, appId={}, chId={}, payMethod={}", appId, chId, payMethod);
        return appChInfoMapper.deleteAppChInfo(appId, chId, payMethod);
    }

    /**
     * @author administrator
     * @param appInfo
     * @return
     */
    public int createAppInfo(AppInfo appInfo) {
        LOG.debug("createAppInfo, appInfo={}", appInfo);
        encryptAppInfo(appInfo);
        return appInfoMapper.createAppInfo(appInfo);
    }

    /**
     * @author administrator
     * @param appInfo
     * @return
     */
    public int updateAppInfo(AppInfo appInfo) {
        LOG.debug("updateAppInfo, appInfo={}", appInfo);
        encryptAppInfo(appInfo);
        return appInfoMapper.updateAppInfo(appInfo);
    }

    /**
     * @author administrator
     * @param appId
     * @return
     */
    public int deleteAppInfo(String appId) {
        LOG.debug("deleteAppInfo, appId={}", appId);
        return appInfoMapper.deleteAppInfo(appId);
    }

    /**
     * @author administrator
     * @return
     */
    public List<AppInfo> getAppInfos() {
        LOG.debug("getAppInfos");
        List<AppInfo> appInfos = appInfoMapper.getAppInfos();
        return decryptAppInfos(appInfos);
    }

    public int createSmsOrder(SmsOrder smsOrder) {
        LOG.debug("createPayOrder, smsOrder={}", smsOrder);
        return smsOrderMapper.createSmsOrder(smsOrder);
    }

    public SmsOrder getSmsOrder(String phone, String validCode, String status) {
        LOG.debug("getSmsOrder, phone={},validCode+{},status:{}", phone, validCode, status);
        List<SmsOrder> lists = smsOrderMapper.getSmsOrderByPhoneAndCode(phone, validCode, status);
        if ((lists != null) && (lists.size() > 0)) {
            return lists.get(0);
        }
        return null;
    }

    public SmsOrder getSmsOrder(String chOrderId) {
        List<SmsOrder> lists = smsOrderMapper.getSmsOrderByChOrderId(chOrderId);
        if ((lists != null) && (lists.size() > 0)) {
            return lists.get(0);
        }
        return null;
    }

}
