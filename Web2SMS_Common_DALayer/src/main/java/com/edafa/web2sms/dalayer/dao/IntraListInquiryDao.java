package com.edafa.web2sms.dalayer.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraListInquiryDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * Session Bean implementation class IntraListInquiryDao
 */
@Stateless
public class IntraListInquiryDao implements IntraListInquiryDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.VODAS_INTRA)
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public IntraListInquiryDao() {
		// TODO Auto-generated constructor stub
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public List<String> inquireIntraList(String billingMsisdn) throws InvalidCustomerForQuotaInquiry,
			IntraListInquiryFailed, DBException {
		List<String> intraList = new ArrayList<String>();
		Connection con = null;
		CallableStatement callableStatement = null;
		try {
			con = (Connection) getEntityManager().unwrap(Connection.class);
			callableStatement = con.prepareCall("{ call SYSADM.WEB2SMS_INTR_COM_MSISDNS(?,?,?,?) }");

			// MSISDN IN VARCHAR2, The dummy MSISDN that has the free unit
			callableStatement.setString(1, billingMsisdn);
			// DATA_CURSOR OUT SYS_REFCURSOR
			callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
			// E_CODE OUT NUMBER, 0 = Success , 1 = Not valid customer
			callableStatement.registerOutParameter(3, Types.NUMERIC);
			// E_DESC OUT VARCHAR2, just error Description
			callableStatement.registerOutParameter(4, Types.VARCHAR);

			callableStatement.execute();

			ResultSet result = ((OracleCallableStatement) callableStatement).getCursor(2);

			int outCode = callableStatement.getInt(3);
			String errMsg = callableStatement.getString(4);

			if (outCode == 1)
				throw new InvalidCustomerForQuotaInquiry(errMsg);
			else if (outCode != 0)
				throw new IntraListInquiryFailed(outCode, "returned message: " + errMsg);

			while (result.next()) {
				intraList.add(result.getString("MSISDN"));
			}
			return intraList;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
}
