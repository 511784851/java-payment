package com.guzhi.pay.channel.unicom.cxf.verifycodemanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.guzhi.pay.channel.unicom.cxf.InaccessInfo;

/**
 * <p>
 * requestEvent complex type的 Java 类。
 * 
 * <p>
 * 以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="requestEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inaccessInfo" type="{http://ws.ifdp.womusic.cn/}inaccessInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestEvent", propOrder = { "inaccessInfo" })
@XmlSeeAlso({ SendLoginCodeEvt.class, SendVerifyCodeEvt.class, CodeLoginEvt.class })
public class RequestEvent {

    protected InaccessInfo inaccessInfo;

    /**
     * 获取inaccessInfo属性的值。
     * 
     * @return
     *         possible object is {@link InaccessInfo }
     * 
     */
    public InaccessInfo getInaccessInfo() {
        return inaccessInfo;
    }

    /**
     * 设置inaccessInfo属性的值。
     * 
     * @param value
     *            allowed object is {@link InaccessInfo }
     * 
     */
    public void setInaccessInfo(InaccessInfo value) {
        this.inaccessInfo = value;
    }

}
