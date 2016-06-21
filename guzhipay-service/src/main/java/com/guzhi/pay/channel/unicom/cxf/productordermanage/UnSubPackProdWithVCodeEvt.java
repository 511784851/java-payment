
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>unSubPackProdWithVCodeEvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="unSubPackProdWithVCodeEvt">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}unSubProductEvt">
 *       &lt;sequence>
 *         &lt;element name="verifyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unSubPackProdWithVCodeEvt", propOrder = {
    "verifyCode"
})
@XmlSeeAlso({
    UnSubProductWithVCodeEvt.class
})
public class UnSubPackProdWithVCodeEvt
    extends UnSubProductEvt
{

    protected String verifyCode;

    /**
     * 获取verifyCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerifyCode() {
        return verifyCode;
    }

    /**
     * 设置verifyCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerifyCode(String value) {
        this.verifyCode = value;
    }

}
