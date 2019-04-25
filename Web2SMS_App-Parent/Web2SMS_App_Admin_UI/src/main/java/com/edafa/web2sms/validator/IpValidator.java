package com.edafa.web2sms.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("IpValidator")
public class IpValidator implements Validator {
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	private Pattern patternIP4 = Pattern.compile(ipv4Pattern);
	private Pattern patternIP6 = Pattern.compile(ipv6Pattern);
	private Matcher matcherIP4;
	private Matcher matcherIP6;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		matcherIP4 = patternIP4.matcher((String) value);
		if (!matcherIP4.matches()) {
			matcherIP6 = patternIP6.matcher((String) value);
			if (!matcherIP6.matches()) {
				throw new ValidatorException(new FacesMessage("IP address does not valid"));
			}
		}

	}

}
