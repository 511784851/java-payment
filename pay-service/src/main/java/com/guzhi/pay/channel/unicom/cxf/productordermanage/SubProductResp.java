
package com.guzhi.pay.channel.unicom.cxf.productordermanage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>subProductResp complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="subProductResp">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ws.ifdp.womusic.cn/}response">
 *       &lt;sequence>
 *         &lt;element name="product" type="{http://ws.ifdp.womusic.cn/}productInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subProductResp", propOrder = {
    "product"
})
public class SubProductResp
    extends Response
{

    protected ProductInfo product;

    /**
     * 获取product属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ProductInfo }
     *     
     */
    public ProductInfo getProduct() {
        return product;
    }

    /**
     * 设置product属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ProductInfo }
     *     
     */
    public void setProduct(ProductInfo value) {
        this.product = value;
    }

}
