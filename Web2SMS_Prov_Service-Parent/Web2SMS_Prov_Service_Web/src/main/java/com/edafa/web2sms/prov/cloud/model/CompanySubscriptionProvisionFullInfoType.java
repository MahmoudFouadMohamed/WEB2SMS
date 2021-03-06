//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.15 at 02:10:57 PM EEST 
//

package com.edafa.web2sms.prov.cloud.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CompanySubscriptionProvisionFullInfoType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="CompanySubscriptionProvisionFullInfoType">
 *   &lt;complexContent>
 *     &lt;extension base="{}ProvisioningBaseType">
 *       &lt;sequence>
 *         &lt;element name="Other" type="{}OtherData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
/**
 * @author akhalifah
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanySubscriptionProvisionFullInfoType", propOrder = { "other" })
public class CompanySubscriptionProvisionFullInfoType extends ProvisioningBaseType {

	@XmlElement(name = "Other")
	protected OtherData other;

	/**
	 * Gets the value of the other property.
	 * 
	 * @return possible object is {@link OtherData }
	 * 
	 */
	public OtherData getOther() {
		return other;
	}

	/**
	 * Sets the value of the other property.
	 * 
	 * @param value
	 *            allowed object is {@link OtherData }
	 * 
	 */
	public void setOther(OtherData value) {
		this.other = value;
	}

	@Override
	public String toString() {
		if (other != null)
			return super.toString() + ", productTier=" + other.getProductTier() + ")";
		else
			return super.toString() + ", productTier= null ";
	}

	@XmlTransient
	public boolean isValid() {
		boolean valid = super.isValid();

		if (valid) {
			if (other != null && other.getProductTier() != null) {
				if (!String.valueOf(other.getProductTier()).matches(("-?\\d+")))
					valid = false;
			} else {
				valid = false;
			}
		}

		return valid;
	}
}
