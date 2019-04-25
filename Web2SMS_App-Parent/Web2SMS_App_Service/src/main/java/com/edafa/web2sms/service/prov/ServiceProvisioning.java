package com.edafa.web2sms.service.prov;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestsArchDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class ServiceProvisioning
 */
@Stateless
public class ServiceProvisioning implements ServiceProvisioningLocal {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	@EJB
	private ProvRequestsArchDaoLocal provRequestsArchDao;

	@Override
	public List<ProvRequestArch> findProvArchByAccount(AdminTrxInfo trxInfo, String accountId) throws DBException {
		try {
			List<ProvRequestArch> result = provRequestsArchDao.findProvRequestByAccountId(accountId);
			return result;

		} catch (DBException e) {
			throw new DBException("DBException while getting provRequestArch for accountID :(" + accountId + " ).", e);
		}

	}
       
}