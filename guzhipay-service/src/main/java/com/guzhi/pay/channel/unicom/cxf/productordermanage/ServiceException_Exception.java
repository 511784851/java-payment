
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.10
 * 2014-02-24T10:52:51.097+08:00
 * Generated source version: 2.7.10
 */

@WebFault(name = "ServiceException", targetNamespace = "http://ws.ifdp.womusic.cn/")
public class ServiceException_Exception extends Exception {
    
    private com.guzhi.pay.channel.unicom.cxf.productordermanage.ServiceException serviceException;

    public ServiceException_Exception() {
        super();
    }
    
    public ServiceException_Exception(String message) {
        super(message);
    }
    
    public ServiceException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException_Exception(String message, com.guzhi.pay.channel.unicom.cxf.productordermanage.ServiceException serviceException) {
        super(message);
        this.serviceException = serviceException;
    }

    public ServiceException_Exception(String message, com.guzhi.pay.channel.unicom.cxf.productordermanage.ServiceException serviceException, Throwable cause) {
        super(message, cause);
        this.serviceException = serviceException;
    }

    public com.guzhi.pay.channel.unicom.cxf.productordermanage.ServiceException getFaultInfo() {
        return this.serviceException;
    }
}
