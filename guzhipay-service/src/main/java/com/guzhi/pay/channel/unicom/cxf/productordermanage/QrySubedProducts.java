
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.guzhi.pay.channel.unicom.cxf.InextraInfo;


/**
 * <p>qrySubedProducts complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qrySubedProducts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://ws.ifdp.womusic.cn/}qrySubedProductsEvt" minOccurs="0"/>
 *         &lt;element name="arg1" type="{http://ws.ifdp.womusic.cn/}inextraInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qrySubedProducts", propOrder = {
    "arg0",
    "arg1"
})
public class QrySubedProducts {

    protected QrySubedProductsEvt arg0;
    protected InextraInfo arg1;

    /**
     * 获取arg0属性的值。
     * 
     * @return
     *     possible object is
     *     {@link QrySubedProductsEvt }
     *     
     */
    public QrySubedProductsEvt getArg0() {
        return arg0;
    }

    /**
     * 设置arg0属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link QrySubedProductsEvt }
     *     
     */
    public void setArg0(QrySubedProductsEvt value) {
        this.arg0 = value;
    }

    /**
     * 获取arg1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link InextraInfo }
     *     
     */
    public InextraInfo getArg1() {
        return arg1;
    }

    /**
     * 设置arg1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link InextraInfo }
     *     
     */
    public void setArg1(InextraInfo value) {
        this.arg1 = value;
    }

}
