package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.LanguageDaoLocal;
import com.edafa.web2sms.dalayer.model.Template;
import com.edafa.web2sms.service.model.TemplateModel;

@Stateless
public class TemplateConversionBean {

	@EJB
	LanguageDaoLocal languageDao;

	public Template getTemplate(TemplateModel templateModel) {
		Template newTemplate = new Template();
		newTemplate.setTemplateId(templateModel.getTemplateId());
		newTemplate.setSystemTemplateFlag(templateModel.getTemplateType());
		newTemplate.setTemplateName(templateModel.getTemplateName());
		newTemplate.setText(templateModel.getText());
		newTemplate.setLanguage(languageDao.getCachedObjectByName(templateModel.getLanguage()));
		return newTemplate;
	}

	public List<Template> getTemplate(List<TemplateModel> templatesModel) {
		List<Template> templates = new ArrayList<>();
		for (TemplateModel templateModel : templatesModel) {
			Template newTemplate = new Template();
			newTemplate.setTemplateId(templateModel.getTemplateId());
			newTemplate.setSystemTemplateFlag(templateModel.getTemplateType());
			newTemplate.setTemplateName(templateModel.getTemplateName());
			newTemplate.setText(templateModel.getText());
			newTemplate.setLanguage(languageDao.getCachedObjectByName(templateModel.getLanguage()));
			templates.add(newTemplate);
		}
		return templates;
	}

	public TemplateModel getTemplateModel(Template template) {
		TemplateModel newTemplateModel = new TemplateModel();
		newTemplateModel.setTemplateId(template.getTemplateId());
		newTemplateModel.setTemplateName(template.getTemplateName());
		newTemplateModel.setTemplateType(template.getSystemTemplateFlag());
		newTemplateModel.setText(template.getText());
		newTemplateModel.setLanguage(template.getLanguage().getLanguageName());
		return newTemplateModel;
	}

	public List<TemplateModel> getTemplateModel(List<Template> templates) {
		List<TemplateModel> newTemplatesModel = new ArrayList<>();

		for (Template template : templates) {
			TemplateModel templateModel = new TemplateModel();
			templateModel.setTemplateId(template.getTemplateId());
			templateModel.setTemplateName(template.getTemplateName());
			templateModel.setTemplateType(template.getSystemTemplateFlag());
			templateModel.setText(template.getText());
			templateModel.setLanguage(template.getLanguage().getLanguageName());
			newTemplatesModel.add(templateModel);
		}
		return newTemplatesModel;
	}
}
