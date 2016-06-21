package com.guzhi.pay.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.MD5Utils;

public class PwdConfig {
    private final static Logger LOG = LoggerFactory.getLogger(PwdConfig.class);

    private String pwd;
    private String user;

    public void setUser(String user) {
        this.user = user;
    }

    public void setPwd(String pwd) throws Exception {
        try {
            this.pwd = DESEncrypt.decryptByAES(MD5Utils.getMD5(this.user + this.user), pwd);
            return;
        } catch (Exception e) {
            LOG.warn("pwd decrypt error", e.getMessage());
            throw e;
        }
    }

    public String getPwd() {
        return this.pwd;
    }
}
