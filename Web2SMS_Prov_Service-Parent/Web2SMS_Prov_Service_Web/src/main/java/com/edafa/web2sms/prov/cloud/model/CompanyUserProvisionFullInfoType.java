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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompanyUserProvisionFullInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompanyUserProvisionFullInfoType">
 *   &lt;complexContent>
 *     &lt;extension base="{}UserProvisionBaseType">
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanyUserProvisionFullInfoType", propOrder = {
    "other"
})
public class CompanyUserProvisionFullInfoType
    extends UserProvisionBaseType
{

    @XmlElement(name = "Other", required = false)
    protected OtherData other;

    /**
     * Gets the value of the other property.
     * 
     * @return
     *     possible object is
     *     {@link OtherData }
     *     
     */
    public OtherData getOther() {
        return other;
    }

    /**
     * Sets the value of the other property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherData }
     *     
     */
    public void setOther(OtherData value) {
        this.other = value;
    }

}