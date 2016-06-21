package com.guzhi.pay.channel.unicom.cxf.verifycodemanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.guzhi.pay.channel.unicom.cxf.InextraInfo;

/**
 * <p>
 * codeLogin complex type的 Java 类。
 * 
 * <p>
 * 以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="codeLogin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="event" type="{http://ws.ifdp.womusic.cn/}codeLoginEvt" minOccurs="0"/>
 *         &lt;element name="inextraInfo" type="{http://ws.ifdp.womusic.cn/}inextraInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "codeLogin", propOrder = { "event", "inextraInfo" })
public class CodeLogin {

    protected CodeLoginEvt event;
    protected InextraInfo inextraInfo;

    /**
     * 获取event属性的值。
     * 
     * @return
     *         possible object is {@link CodeLoginEvt }
     * 
     */
    public CodeLoginEvt getEvent() {
        return event;
    }

    /**
     * 设置event属性的值。
     * 
     * @param value
     *            allowed object is {@link CodeLoginEvt }
     * 
     */
    public void setEvent(CodeLoginEvt value) {
        this.event = value;
    }

    /**
     * 获取inextraInfo属性的值。
     * 
     * @return
     *         possible object is {@link InextraInfo }
     * 
     */
    public InextraInfo getInextraInfo() {
        return inextraInfo;
    }

    /**
     * 设置inextraInfo属性的值。
     * 
     * @param value
     *            allowed object is {@link InextraInfo }
     * 
     */
    public void setInextraInfo(InextraInfo value) {
        this.inextraInfo = value;
    }

}
