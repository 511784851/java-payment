
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>qrySubedProductsResp complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qrySubedProductsResp">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}response">
 *       &lt;sequence>
 *         &lt;element name="subedProducts" type="{http://ws.ifdp.womusic.cn/}subedProductInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qrySubedProductsResp", propOrder = {
    "subedProducts"
})
public class QrySubedProductsResp
    extends Response
{

    @XmlElement(nillable = true)
    protected List<SubedProductInfo> subedProducts;

    /**
     * Gets the value of the subedProducts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subedProducts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubedProducts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubedProductInfo }
     * 
     * 
     */
    public List<SubedProductInfo> getSubedProducts() {
        if (subedProducts == null) {
            subedProducts = new ArrayList<SubedProductInfo>();
        }
        return this.subedProducts;
    }

}
