
package com.guzhi.pay.channel.unicom.cxf.verifycodemanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>sendLoginCodeEvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="sendLoginCodeEvt">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}requestEvent">
 *       &lt;sequence>
 *         &lt;element name="callNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendLoginCodeEvt", propOrder = {
    "callNumber"
})
public class SendLoginCodeEvt
    extends RequestEvent
{

    protected String callNumber;

    /**
     * 获取callNumber属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallNumber() {
        return callNumber;
    }

    /**
     * 设置callNumber属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallNumber(String value) {
        this.callNumber = value;
    }

}
