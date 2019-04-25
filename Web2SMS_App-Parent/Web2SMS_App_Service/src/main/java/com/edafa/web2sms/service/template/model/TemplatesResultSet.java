package com.edafa.web2sms.service.template.model;

import java.util.List;

import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.TemplateModel;

public class TemplatesResultSet extends ResultStatus {
	
	List<TemplateModel> templateList;

	public List<TemplateModel> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(List<TemplateModel> templateList) {
		this.templateList = templateList;
	}

}
