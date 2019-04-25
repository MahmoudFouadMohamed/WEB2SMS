package com.edafa.web2sms.service.report.offline.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.model.Reports;

@Local
public interface ReportsOfflineProcessorEngineTaskLocal {

	public void processReport(Reports report);

	public int getActiveCount();
}
