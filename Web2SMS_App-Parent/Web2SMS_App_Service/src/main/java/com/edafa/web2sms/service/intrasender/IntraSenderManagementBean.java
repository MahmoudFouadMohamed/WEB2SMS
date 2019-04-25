package com.edafa.web2sms.service.intrasender;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.IntraSenderDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderAlreadyExistException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderNotFoundException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderShouldHaveAcctListException;
import com.edafa.web2sms.service.intrasender.interfaces.IntraSenderManagementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.SMSUtils;
import com.edafa.web2sms.utils.sms.SenderType;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

/**
 * Session Bean implementation class IntraSenderManagementBean
 */
@Stateless
public class IntraSenderManagementBean implements IntraSenderManagementBeanLocal {

	@EJB
	IntraSenderDaoLocal intraSenderDao;

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger acctLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name()); // @gomaa_acc move intra senders to account management
	Logger adminLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	/**
	 * Default constructor.
	 */
	public IntraSenderManagementBean() {
	}

	public String logTrxId(String trxId) {
		String str = "Trx(";
		str += trxId;
		str += "): ";
		return str;
	}

	public String userLogInfo(String id, String userName) {
		String str = "User(";
		str += id;
		str += ",";
		str += userName;
		str += "): ";
		return str;
	}

	@Override
	public void createIntraSender(AdminTrxInfo trxInfo, IntraSender intraSender) throws InvalidSMSSender, DBException,
			IntraSenderAlreadyExistException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {
			adminLogger.info(logId + "adding intra sender name (" + intraSender.getSenderName()
					+ ") with systemFlag : (" + intraSender.getSystemSenderFlag() + ").");

			validateIntraSenderName(logId, intraSender.getSenderName());

			adminLogger.debug(logId + "intra sender is valid, start presist it with accounts: "
					+ intraSender.getAccountList().size());
			intraSenderDao.create(intraSender);
		} catch (InvalidSMSSender e) {
			appLogger.error(logId + "Invalid SMS IntraSender" + intraSender.getSenderName(), e);
			throw e;
		} catch (DBException e) {
			appLogger.error(logId + "DBException while creating Intra sender name : (" + intraSender.getSenderName()
					+ ").", e);
			throw e;
		} catch (IntraSenderAlreadyExistException e) {
			appLogger.error(logId + "IntraSender" + intraSender.getSenderName() + " Already Exists", e);
			throw e;
		}

	}

	@Override
	public List<IntraSender> findSystemIntraSender(AdminTrxInfo trxInfo) throws DBException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {
			adminLogger.info(logId + "Fetching system intra sender list.");
			List<IntraSender> intraSenderList = intraSenderDao.findSystemIntraSendersList();
			adminLogger.debug(logId + "intra sender list fitched successfully, found(" + intraSenderList.size()
					+ ") intraSender(s)");
			return intraSenderList;
		} catch (DBException e) {
			adminLogger.error("DBException while fetching intra sender list. ", e);
			return null;

		} catch (Exception e) {
			adminLogger.error("Error while fetching intra sender list. ", e);
			return null;
		}
	}

	private void validateIntraSenderName(String logId, String intraSenderName) throws InvalidSMSSender,
			IntraSenderAlreadyExistException, DBException {

		int intraSenderCounter = intraSenderDao.countIntraSender(intraSenderName);
		adminLogger.debug(logId + "Validate intra name: " + intraSenderName + ", getting SenderType");
		SenderType senderType = SMSUtils.getSenderType(intraSenderName);
		adminLogger.debug(logId + " SenderType is : ( " + senderType + " ). ");
		if (!senderType.equals(SenderType.ALPHANUMERIC)) {
			adminLogger.error(logId + "Invalid SMS IntraSender" + intraSenderName);
			throw new InvalidSMSSender(intraSenderName, "Invalid intra sender");
		}
		if (intraSenderCounter > 0) {
			adminLogger.error(logId + "IntraSender" + intraSenderName + " Already Exists");
			throw new IntraSenderAlreadyExistException();
		}

	}

	@Override
	public void editIntraSender(AdminTrxInfo trxInfo, IntraSender newIntraSender) throws InvalidSMSSender,
			IntraSenderAlreadyExistException, IntraSenderNotFoundException, DBException,
			IntraSenderShouldHaveAcctListException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		adminLogger.info(logId + "Editing " + newIntraSender);
		IntraSender currentIntraSender = findIntraSenderById(trxInfo, newIntraSender.getIntraSenderId());

		if (newIntraSender.getSystemSenderFlag()) {
			adminLogger.debug(logId
					+ "change systemSenderFlag to true and remove accounts associated to the intraSednder.");
			currentIntraSender.setSystemSenderFlag(true);
			currentIntraSender.setAccountList(null);

		} else { // SystemFalg false, Intra sender should have account list.
			currentIntraSender.setSystemSenderFlag(false);
			if (newIntraSender.getAccountList() == null || newIntraSender.getAccountList().isEmpty()) {
				adminLogger.error(logId + "IntraSender's accountList Shouldn't be empty.");
				throw new IntraSenderShouldHaveAcctListException();
			}
			adminLogger.debug(logId + "setting account list.");
			currentIntraSender.setAccountList(newIntraSender.getAccountList());

		}
		if (newIntraSender.getSenderName() != null
				&& !newIntraSender.getSenderName().equals(currentIntraSender.getSenderName())) {
			validateIntraSenderName(logId, newIntraSender.getSenderName());
			currentIntraSender.setSenderName(newIntraSender.getSenderName());
		}
		adminLogger.debug(logId + currentIntraSender + ", merging the changes.");

		intraSenderDao.edit(currentIntraSender);
		adminLogger.debug(logId + "changes merged successfully.");

	}

	@Override
	public List<IntraSender> findIntraSender(AdminTrxInfo trxInfo, int frist, int max) throws DBException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {
			acctLogger.info(logId + "Fetching intra senders list from=" + frist + " to max=" + max);
			List<IntraSender> intraSenderList = intraSenderDao.findRange(frist, max, "senderName");
			acctLogger.info(logId + "intra sender list fitched successfully with size=" + intraSenderList.size());
			return intraSenderList;
		} catch (DBException e) {
			acctLogger.error(logId + "DB Exception while fetching intra sender list", e);
			return null;
		}
	}

	@Override
	public int countIntraSendersNames(AdminTrxInfo trxInfo) throws DBException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {
			acctLogger.info(logId + "Counting intra senders list rows");
			int maxNum = intraSenderDao.count();
			return maxNum;
		} catch (DBException e) {
			acctLogger.error(logId + "DB Exception while counting rows intra senders list", e);
			return 0;
		} catch (Exception e) {
			acctLogger.error(logId + "Error while counting rows intra senders list", e);
			return 0;
		}
	}

	@Override
	public void deleteIntraSender(AdminTrxInfo trxInfo, String id) throws DBException, IntraSenderNotFoundException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		acctLogger.info(logId + "Deleting intra sender with id=" + id);
		try {
			IntraSender intraSender = intraSenderDao.find(id);
			if (intraSender != null) {

				acctLogger.debug(trxInfo.logId() + "removing " + intraSender);

				intraSenderDao.remove(intraSender);
			} else {
				acctLogger.error(logId + "Intra Sender Not Found Exception");
				throw new IntraSenderNotFoundException(id);
			}

			acctLogger.info(logId + "Intra sender with id=" + id + " deleted successfully");

		} catch (DBException e) {
			acctLogger.error(logId + "Error while deleting intra sender with id=" + id, e);
			throw e;
		}

	}

	IntraSender findIntraSenderById(AdminTrxInfo trxInfo, String id) throws DBException, IntraSenderNotFoundException {
		String logId = userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		acctLogger.info(logId + "find intra sender with id=" + id);
		IntraSender intraSender = null;
		try {
			intraSender = intraSenderDao.find(id);
			if (intraSender != null) {
				acctLogger.debug(logId + "intra sender with id:( " + id + " ) is found : [" + intraSender + " ].");
			} else {
				acctLogger.debug(logId + "intra sender with id:( " + id + " ) is not found ");
				throw new IntraSenderNotFoundException(id);
			}
		} catch (DBException e) {
			acctLogger.error(logId + "DBException while finding intra sender with id:( " + id + ").");
		}

		return intraSender;
	}

	@Override
	public IntraSender findIntraSenderById(TrxInfo trxInfo, String intraId) {
		IntraSender intraSender = null;
		try {
			intraSender = intraSenderDao.find(intraId);

		} catch (DBException e) {
			acctLogger.error(trxInfo.logId() + "DBException while finding intra sender by Id:( " + intraId + ").");
		}
		return intraSender;

	}

}
