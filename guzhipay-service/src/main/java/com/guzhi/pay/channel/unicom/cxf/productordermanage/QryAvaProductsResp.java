
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>qryAvaProductsResp complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="qryAvaProductsResp">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}response">
 *       &lt;sequence>
 *         &lt;element name="products" type="{http://ws.ifdp.womusic.cn/}productInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qryAvaProductsResp", propOrder = {
    "products"
})
public class QryAvaProductsResp
    extends Response
{

    @XmlElement(nillable = true)
    protected List<ProductInfo> products;

    /**
     * Gets the value of the products property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the products property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProducts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductInfo }
     * 
     * 
     */
    public List<ProductInfo> getProducts() {
        if (products == null) {
            products = new ArrayList<ProductInfo>();
        }
        return this.products;
    }

}
