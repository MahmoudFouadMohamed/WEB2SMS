//
//This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
//See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
//Any modifications to this file will be lost upon recompilation of the source schema. 
//Generated on: 2014.05.19 at 02:16:38 PM EEST 
//

package com.edafa.web2sms.prov.tibco.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for changeSenderNameRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="confirmSenderNameAddRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="senderName" type="{http://www.edafa.com/web2sms/prov/TIBCO}senderName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmSenderNameAddRequest", propOrder = { "accountHolderId", "senderName" })
public class ConfirmSenderNameAddRequest  extends ProvisioningRequest{

	@XmlElement(required = true)
	protected String senderName;


	/**
	 * Gets the value of the senderName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * Sets the value of the senderName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSenderName(String value) {
		this.senderName = value;
	}

	boolean isNonEmptyStr(String s) {
		return (s != null && !s.isEmpty());
	}

	public boolean isValid() {
		return isNonEmptyStr(requestId) && isNonEmptyStr(senderName);
	}

	@Override
	public String toString() {
		return "AddSenderNameRequest (senderName=" + senderName + ")";
	}
}
