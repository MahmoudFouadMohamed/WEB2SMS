package com.edafa.web2sms.service.prov.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.service.model.AdminTrxInfo;

@Local
public interface ServiceProvisioningLocal {

	List<ProvRequestArch> findProvArchByAccount(AdminTrxInfo trxInfo, String accountId) throws DBException;

}
