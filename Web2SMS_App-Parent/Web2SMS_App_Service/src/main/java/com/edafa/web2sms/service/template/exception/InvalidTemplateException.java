package com.edafa.web2sms.service.template.exception;

import com.edafa.web2sms.dalayer.model.Template;

public class InvalidTemplateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4748218094233410389L;

	Template template;

	public InvalidTemplateException(Template template) {
		this.template = template;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(template + " Invalid [");

		if (template.getTemplateName() == null) {

			sb.append(sb.charAt(sb.length()) == '[' ? " template name: " + template.getTemplateName()
					: ", template name: " + template.getTemplateName());
		}
		if (template.getText() == null) {
			sb.append(sb.charAt(sb.length()) == '[' ? " template text: " + template.getText()  : ", template text: "
					+ template.getText() );
		}
		if (template.getTemplateId() == null) {
			sb.append(sb.charAt(sb.length()) == '[' ? " template ID: " + template.getTemplateId() : ", template ID: "
					+ template.getTemplateId());
		}
		sb.append("]. ");

		return sb.toString();

	}
}
