
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>qryVacSubScriptionList complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qryVacSubScriptionList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://ws.ifdp.womusic.cn/}qryVacSubScriptionListEvt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qryVacSubScriptionList", propOrder = {
    "arg0"
})
public class QryVacSubScriptionList {

    protected QryVacSubScriptionListEvt arg0;

    /**
     * 获取arg0属性的值。
     * 
     * @return
     *     possible object is
     *     {@link QryVacSubScriptionListEvt }
     *     
     */
    public QryVacSubScriptionListEvt getArg0() {
        return arg0;
    }

    /**
     * 设置arg0属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link QryVacSubScriptionListEvt }
     *     
     */
    public void setArg0(QryVacSubScriptionListEvt value) {
        this.arg0 = value;
    }

}
