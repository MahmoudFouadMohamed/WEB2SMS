package com.edafa.web2sms.ui.admin;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.TierTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.dalayer.model.TierType;
import com.edafa.web2sms.service.admin.interfaces.AdminTierManagementBeanLocal;
import com.edafa.web2sms.service.tier.exceptions.InvalidTierException;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class TierBean {
	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	//

	@EJB
	AdminTierManagementBeanLocal tierManagementBean;

	@EJB
	TierTypeDaoLocal tierTypeDao;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication()
			.getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName,
			locale);

	private boolean editFlag = false;

	ArrayList<Tier> tiers = new ArrayList<Tier>();
	Tier tier = new Tier();
	private TierTypesEnum tierTypeName;
	private List<TierTypesEnum> tierTypesList;
	private boolean expiryDays = true;

	@PostConstruct
	public void init() {
		fillPrps();
		tierTypesList = tierTypeDao.getCachedList();
		if (tierTypesList.get(0).equals(TierTypesEnum.PREPAID))
			expiryDays = true;
		else
			expiryDays = true;

	}

	private void fillPrps() {
		fillTable(0);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable(long currentPage) {
		tiers = new ArrayList<Tier>();
		List list = null;
		try {

			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			list = tierManagementBean.viewTier(CommonUtil.manageTrxInfo(trxID),
					Long.valueOf(currentPage).intValue(), pageSize);
			if (list != null && !list.isEmpty()) {
				tiers = new ArrayList<Tier>(list);
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("noResultFound")),
								null));
			}
			setRowCount(tierManagementBean.count(CommonUtil
					.manageTrxInfo(trxID)));
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

	}

	public void changeTierType(AjaxBehaviorEvent event) {
		if (tierTypeName.equals(TierTypesEnum.PREPAID))
			expiryDays = true;
		else
			expiryDays = false;

	}

	public String add() {

		/*
		 * added after changes by one-off bundle
		 */
		// tier.setTierTypeId( );

		if (editFlag) {

			logger.debug("Updating Tier : " + tier.toString());

			try {
				tierManagementBean.updateTier(CommonUtil.manageTrxInfo(), tier);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("processSucceeded")),
								null));

			} catch (DBException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String
								.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (InvalidTierException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String
								.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (Exception e) {
				if (e.getCause() != null
						&& e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									String.format(bundle
											.getString("dbViolation")), null));
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format(bundle
													.getString("processFailed")),
											null));

				}
//				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		} else {
			try {
				/* added after one-off changes* */
				tier.setTierType(tierTypeDao
						.getCachedObjectByName(tierTypeName));
				logger.debug("Adding Tier : " + tier.toString());

				tierManagementBean.createTier(CommonUtil.manageTrxInfo(), tier);

				String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);

				rowCount = tierManagementBean.count(CommonUtil
						.manageTrxInfo(trxID));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("processSucceeded")),
								null));
			} catch (DBException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String
								.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (InvalidTierException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String
								.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (Exception e) {
				if (e.getCause() != null
						&& e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									String.format(bundle
											.getString("dbViolation")), null));
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format(bundle
													.getString("processFailed")),
											null));

				}
//				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		editFlag = false;
		if (tierTypesList.get(0).equals(TierTypesEnum.PREPAID))
			expiryDays = true;
		else
			expiryDays = true;
		tier = new Tier();
		fillTable(0);
		return null;
	}

	public String edit(String id) {
		for (int i = 0; i < tiers.size(); i++) {
			if (tiers.get(i).getTierId().equals(Integer.valueOf(id))) {
				logger.debug("Loading tier for editing : "
						+ tiers.get(i).toString());
				setTier(tiers.get(i));
			}// end if
		}// end for

		editFlag = true;


		if (tier.getTierType().getTierTypeName()
				.equals(TierTypesEnum.PREPAID)) {
			tierTypeName = TierTypesEnum.PREPAID;
			expiryDays = true;
		}// end if
		else {
			tierTypeName = TierTypesEnum.POSTPAID;
			expiryDays = false;
		}// end else

		return null;// "Tier?faces-redirect=true";
	}// end of method edit

	public String delete(String id) {
		try {
			logger.debug("Removing Tier : " + id);

			String trxID = TrxId.getTrxId();
			tierManagementBean.deleteTier(CommonUtil.manageTrxInfo(trxID),
					Integer.valueOf(id));

			// paging
			// rowCount =
			// templateManagementBeanLocal.countTemplates(CommonUtil.manageTrxInfo(trxID));
			FirstPage();
			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, String
									.format(bundle
											.getString("processSucceeded")),
									null));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));

//			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return null;
	}// end of method delete

	// ////////////////Paging/////////////////////////////

	long rowCount = 0L;
	long currentPage = 0;
	int pageSize = 10;

	public String FirstPage() {
		currentPage = 0;
		fillTable(0);

		return null;
	}

	public String nextPage() {
		if (currentPage + pageSize < rowCount) {
			currentPage += pageSize;
			if (currentPage < rowCount) {
				fillTable(currentPage);
			} else {
				LastPage();
			}
		}

		return null;
	}

	public String previousPage() {
		if (currentPage != 0) {
			currentPage -= pageSize;
			if (currentPage < pageSize) {
				currentPage = 0;
				FirstPage();
			} else {
				fillTable(currentPage);
			}
		}

		return null;
	}

	public String LastPage() {
		if (rowCount != 0) {
			long pagesMod = rowCount % pageSize;
			currentPage = rowCount - pagesMod;
			if (pagesMod > 0) {
				fillTable(rowCount - pagesMod);
			} else {
				fillTable(rowCount - pageSize);
			}
		}// end if

		return null;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public ArrayList<Tier> getTiers() {
		return tiers;
	}

	public void setTiers(ArrayList<Tier> tiers) {
		this.tiers = tiers;
	}

	public Tier getTier() {
		return tier;
	}

	public void setTier(Tier tier) {
		this.tier = tier;
	}

	public TierTypesEnum getTierTypeName() {
		return tierTypeName;
	}

	public void setTierTypeName(TierTypesEnum tierTypeName) {
		this.tierTypeName = tierTypeName;
	}

	public List<TierTypesEnum> getTierTypesList() {
		return tierTypesList;
	}

	public void setTierTypesList(List<TierTypesEnum> tierTypesList) {
		this.tierTypesList = tierTypesList;
	}

	public boolean isExpiryDays() {
		return expiryDays;
	}

	public void setExpiryDays(boolean expiryDays) {
		this.expiryDays = expiryDays;
	}

	// public String getT_Type_Name() {
	// return t_Type_Name;
	// }
	//
	// public void setT_Type_Name(String t_Type_Name) {
	// this.t_Type_Name = t_Type_Name;
	// }

	// ///////////////////////////end Paging///////////////
}
