package com.edafa.web2sms.dalayer.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.QuotaInquiryDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;

/**
 * Session Bean implementation class QutaInquireDao
 */
@Stateless
public class QuotaInquiryDao implements QuotaInquiryDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.VODAS)
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public QuotaInquiryDao() {
		// TODO Auto-generated constructor stub
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public QuotaInfo inquireQuota(String billingMsisdn) throws InvalidCustomerForQuotaInquiry, QuotaInquiryFailed,
			DBException {

		QuotaInfo result = new QuotaInfo();
		Connection con = null;
		CallableStatement callableStatement = null;
		try {
			con = (Connection) getEntityManager().unwrap(Connection.class);
			callableStatement = con.prepareCall("{ call WEB2SMS_QUOTA_INQUIRY(?,?,?,?,?,?) }");

			result = new QuotaInfo();
			result.setBillingMsisdn(billingMsisdn);
			// MSISDN IN VARCHAR2, The dummy MSISDN that has the free unit
			callableStatement.setString(1, billingMsisdn);
			// GRANTED_QOUTA OUT NUMBER, No of granted SMSs in the customer’s
			// quota
			callableStatement.registerOutParameter(2, Types.NUMERIC);
			// CONSUMED_SMS OUT NUMBER, No of consumed SMSs during the current
			// bill cycle
			callableStatement.registerOutParameter(3, Types.NUMERIC);
			// EXPIRY_DATE OUT DATE, End of the current bill cycle
			callableStatement.registerOutParameter(4, Types.DATE);
			// E_CODE OUT NUMBER, 0 = Success , 1 = Not valid customer
			callableStatement.registerOutParameter(5, Types.NUMERIC);
			// E_DESC OUT VARCHAR2, just error Description
			callableStatement.registerOutParameter(6, Types.VARCHAR);

			callableStatement.execute();

			int outCode = callableStatement.getInt(5);
			String errMsg = callableStatement.getString(6);

			if (outCode == 1)
				throw new InvalidCustomerForQuotaInquiry(errMsg);
			else if (outCode != 0)
				throw new QuotaInquiryFailed(outCode, "returned message: " + errMsg);

			BigDecimal grantedSMS = callableStatement.getBigDecimal(2);
			BigDecimal consumedSMS = callableStatement.getBigDecimal(3);
			Date expiryDate = callableStatement.getDate(4);

			if (grantedSMS == null || consumedSMS == null || expiryDate == null)
				throw new QuotaInquiryFailed("Invalid returned values: grantedSMS=" + grantedSMS + ", consumedSMS="
						+ consumedSMS + ", expiryDate=" + expiryDate);

			result.setGrantedSMS(grantedSMS.intValue());
			result.setConsumedSMS(consumedSMS.intValue());
			result.setReservedSMS(0);
			if (expiryDate != null) {
				result.setExpiryDate(new java.util.Date(expiryDate.getTime()));
			}

		} catch (SQLException e) {
			throw new DBException(e);
		}

		return result;

	}

	
}
