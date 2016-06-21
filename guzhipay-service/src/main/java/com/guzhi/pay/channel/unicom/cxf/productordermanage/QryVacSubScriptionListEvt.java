
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.guzhi.pay.channel.unicom.cxf.InaccessInfo;


/**
 * <p>qryVacSubScriptionListEvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qryVacSubScriptionListEvt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inaccessInfo" type="{http://ws.ifdp.womusic.cn/}inaccessInfo" minOccurs="0"/>
 *         &lt;element name="productIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qryVacSubScriptionListEvt", propOrder = {
    "accountID",
    "inaccessInfo",
    "productIds"
})
public class QryVacSubScriptionListEvt {

    protected String accountID;
    protected InaccessInfo inaccessInfo;
    @XmlElement(nillable = true)
    protected List<String> productIds;

    /**
     * 获取accountID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountID() {
        return accountID;
    }

    /**
     * 设置accountID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountID(String value) {
        this.accountID = value;
    }

    /**
     * 获取inaccessInfo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link InaccessInfo }
     *     
     */
    public InaccessInfo getInaccessInfo() {
        return inaccessInfo;
    }

    /**
     * 设置inaccessInfo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link InaccessInfo }
     *     
     */
    public void setInaccessInfo(InaccessInfo value) {
        this.inaccessInfo = value;
    }

    /**
     * Gets the value of the productIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProductIds() {
        if (productIds == null) {
            productIds = new ArrayList<String>();
        }
        return this.productIds;
    }

}
