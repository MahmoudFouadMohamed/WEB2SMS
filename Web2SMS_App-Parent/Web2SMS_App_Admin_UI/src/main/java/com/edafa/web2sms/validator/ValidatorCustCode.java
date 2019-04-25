package com.edafa.web2sms.validator;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ValidatorCustCode")
public class ValidatorCustCode implements Validator
{
	// ///////////;
	FacesContext facesContext = FacesContext.getCurrentInstance();
	String messageBundleName = facesContext.getApplication().getMessageBundle();
	Locale locale = facesContext.getViewRoot().getLocale();
	ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value) throws ValidatorException
	{

		if (!validateCustCode(String.valueOf(value)))
		{
			FacesMessage msg = new FacesMessage("Invalid number", "Invalid number");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

	private boolean validateCustCode(String valueOf)
	{
		if (valueOf == null || valueOf.isEmpty())
		{
			return true;
		}
		// Only numbers and '.' allowed
		// Cust length changed from 20 : 24
		if (valueOf.trim().length() < 1 || valueOf.length() > 24 || !valueOf.replace('.', '0').matches("[0-9]{1,24}")
				|| valueOf.startsWith(".") || valueOf.endsWith(".") || valueOf.contains(".."))
		{
			return false;
		}
		return true;
	}

}
