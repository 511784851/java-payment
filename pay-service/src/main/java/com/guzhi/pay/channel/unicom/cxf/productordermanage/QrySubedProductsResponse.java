
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>qrySubedProductsResponse complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qrySubedProductsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.ifdp.womusic.cn/}qrySubedProductsResp" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qrySubedProductsResponse", propOrder = {
    "_return"
})
public class QrySubedProductsResponse {

    @XmlElement(name = "return")
    protected QrySubedProductsResp _return;

    /**
     * 获取return属性的值。
     * 
     * @return
     *     possible object is
     *     {@link QrySubedProductsResp }
     *     
     */
    public QrySubedProductsResp getReturn() {
        return _return;
    }

    /**
     * 设置return属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link QrySubedProductsResp }
     *     
     */
    public void setReturn(QrySubedProductsResp value) {
        this._return = value;
    }

}
