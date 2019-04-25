//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.01 at 01:23:18 PM EET 
//

package com.edafa.web2sms.adapters.cloud.callback.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ProvisioningStatusUpdateType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ProvisioningStatusUpdateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProvisioningID" type="{http://provcallback.eagle.vodafone.com.eg}ProvisioningIDType"/>
 *         &lt;element name="Status" type="{http://provcallback.eagle.vodafone.com.eg}StatusType"/>
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "ProvisioningStatusUpdate", namespace = "http://prov-callback.eagle.vodafone.com.eg")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisioningStatusUpdateType", propOrder = { "provisioningID", "status", "errorMessage" })
public class ProvisioningStatusUpdateType {

	@XmlElement(name = "ProvisioningID", required = true)
	protected String provisioningID;
	@XmlElement(name = "Status", required = true)
	protected StatusType status;
	@XmlElement(name = "ErrorMessage", required = false, nillable = false)
	protected String errorMessage;

	/**
	 * Gets the value of the provisioningID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProvisioningID() {
		return provisioningID;
	}

	/**
	 * Sets the value of the provisioningID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProvisioningID(String value) {
		this.provisioningID = value;
	}

	/**
	 * Gets the value of the status property.
	 * 
	 * @return possible object is {@link StatusType }
	 * 
	 */
	public StatusType getStatus() {
		return status;
	}

	/**
	 * Sets the value of the status property.
	 * 
	 * @param value
	 *            allowed object is {@link StatusType }
	 * 
	 */
	public void setStatus(StatusType value) {
		this.status = value;
	}

	/**
	 * Gets the value of the errorMessage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the value of the errorMessage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setErrorMessage(String value) {
		this.errorMessage = value;
	}

	public String logId() {
		return "(" + provisioningID + "): ";
	}

}
