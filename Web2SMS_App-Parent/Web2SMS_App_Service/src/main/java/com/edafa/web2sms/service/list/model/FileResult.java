package com.edafa.web2sms.service.list.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.model.FileValidation;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(namespace = "http://www.edafa.com/web2sms/service/list/model/")
public class FileResult extends ResultStatus {

	@XmlElement(required = true, nillable = true)
	FileValidation fileResult;

	public FileValidation getFileResult() {
		return fileResult;
	}

	public void setFileResult(FileValidation fileResult) {
		this.fileResult = fileResult;
	}

}
