
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>subedProductInfo complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="subedProductInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}productInfo">
 *       &lt;sequence>
 *         &lt;element name="cantUnSubscribeReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unSubTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unSubscribeable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subedProductInfo", propOrder = {
    "cantUnSubscribeReason",
    "status",
    "subTime",
    "unSubTime",
    "unSubscribeable"
})
public class SubedProductInfo
    extends ProductInfo
{

    protected String cantUnSubscribeReason;
    protected String status;
    protected String subTime;
    protected String unSubTime;
    protected boolean unSubscribeable;

    /**
     * 获取cantUnSubscribeReason属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCantUnSubscribeReason() {
        return cantUnSubscribeReason;
    }

    /**
     * 设置cantUnSubscribeReason属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCantUnSubscribeReason(String value) {
        this.cantUnSubscribeReason = value;
    }

    /**
     * 获取status属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置status属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * 获取subTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubTime() {
        return subTime;
    }

    /**
     * 设置subTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubTime(String value) {
        this.subTime = value;
    }

    /**
     * 获取unSubTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnSubTime() {
        return unSubTime;
    }

    /**
     * 设置unSubTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnSubTime(String value) {
        this.unSubTime = value;
    }

    /**
     * 获取unSubscribeable属性的值。
     * 
     */
    public boolean isUnSubscribeable() {
        return unSubscribeable;
    }

    /**
     * 设置unSubscribeable属性的值。
     * 
     */
    public void setUnSubscribeable(boolean value) {
        this.unSubscribeable = value;
    }

}
