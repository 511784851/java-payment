
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>buyProductEvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="buyProductEvt">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}subProductEvt">
 *       &lt;sequence>
 *         &lt;element name="contentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "buyProductEvt", propOrder = {
    "contentId"
})
@XmlSeeAlso({
    BuyProductWithVCodeEvt.class
})
public class BuyProductEvt
    extends SubProductEvt
{

    protected String contentId;

    /**
     * 获取contentId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * 设置contentId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentId(String value) {
        this.contentId = value;
    }

}
