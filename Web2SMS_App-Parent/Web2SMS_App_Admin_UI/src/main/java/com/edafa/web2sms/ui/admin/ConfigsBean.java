package com.edafa.web2sms.ui.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.service.admin.exception.ConfigsServiceConnectionException;
import com.edafa.web2sms.service.admin.interfaces.AdminConfigManagementBeanLocal;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.ws.utils.configs.FailedToReadConfigsException_Exception;
import com.edafa.ws.utils.configs.FailedToSaveConfigsException_Exception;
import com.edafa.ws.utils.configs.InvalidConfigsException_Exception;
import com.edafa.ws.utils.configs.model.Config;
import com.edafa.ws.utils.configs.model.ModuleConfigs;

public class ConfigsBean {

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	//

	@EJB
	AdminConfigManagementBeanLocal adminConfigManagementBeanLocal;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication().getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	// private boolean editFlag = false;

	ModuleConfigs moduleConfig = new ModuleConfigs();

	ArrayList<ModuleConfigs> moduleConfigs = new ArrayList<ModuleConfigs>();

	@PostConstruct
	public void init() {
		fillPrps();

	}

	private void fillPrps() {
		fillTable();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable() {
		moduleConfigs = new ArrayList<ModuleConfigs>();

		List<ModuleConfigs> list = new ArrayList<ModuleConfigs>();
		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			list = adminConfigManagementBeanLocal.readConfigs();
			if (list != null && !list.isEmpty()) {
				moduleConfigs = new ArrayList<ModuleConfigs>(list);
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("noResultFound")),
								null));
			}
			// setRowCount(templateManagementBeanLocal.count(CommonUtil.manageTrxInfo(trxID)));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
			// e.printStackTrace();
			// FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			logger.error("Error while filling table", e);

		}

	}

	public String updateConfigs() {

		try {
			adminConfigManagementBeanLocal.saveConfigs(moduleConfig);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("processSucceeded")),
							null));
			fillTable();

		} catch (FailedToSaveConfigsException_Exception e) {
			
			if(e.getMessage().contains("sending rate"))
			{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(e.getMessage()),
								null));
			}
			else{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
								null));
			}
			logger.error("Failed to save configuartions exception while updating configurations ", e);

		} catch (InvalidConfigsException_Exception e) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getFaultInfo().getMessage(), null));

			for (Config conf : e.getFaultInfo().getInvalidConfigs()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Module : " + conf.getModule() + ", Property :"
								+ conf.getKey() + ", Type : " + conf.getConfigType(), null));
			}

			logger.error("Invalid configuartions exception while updating configurations ", e);

			// FacesContext.getCurrentInstance().addMessage(null,
			// new FacesMessage(FacesMessage.SEVERITY_ERROR,
			// String.format(bundle.getString("processFailed")), null));
//			e.printStackTrace();
		} catch (ConfigsServiceConnectionException e) {
			logger.error("ConfigsServiceConnectionException", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
		}

		return "";
	}

	// public String updateAllConfigs() {
	//
	// // try {
	// // adminConfigManagementBeanLocal.updateAllConfig(moduleConfigs);
	// //
	// // FacesContext.getCurrentInstance().addMessage(null,
	// // new FacesMessage(FacesMessage.SEVERITY_INFO,
	// // String.format(bundle.getString("processSucceeded")), null));
	// //
	// // fillTable();
	// // } catch (DBException e) {
	// // FacesContext.getCurrentInstance().addMessage(null,
	// // new FacesMessage(FacesMessage.SEVERITY_ERROR,
	// // String.format(bundle.getString("processFailed")), null));
	// // e.printStackTrace();
	// // e.printStackTrace();
	// // } catch (InvalidConfigException e) {
	// // FacesContext.getCurrentInstance().addMessage(null,
	// // new FacesMessage(FacesMessage.SEVERITY_ERROR,
	// // String.format(bundle.getString("processFailed")), null));
	// // e.printStackTrace();
	// // e.printStackTrace();
	// // }
	//
	// return "";
	// }

	public String applyConfigs() {
		try {
			adminConfigManagementBeanLocal.refreshModuleConfigs(moduleConfig.getModule());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("processSucceeded")),
							null));

			fillTable();

		} catch (FailedToReadConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
			logger.error("Failed to read configs exception while applying confiqurations", e);
//			e.printStackTrace();
		} catch (InvalidConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
			logger.error("Invalid configs exception while applying confiqurations", e);
//			e.printStackTrace();
		} catch (ConfigsServiceConnectionException e) {
			logger.error("ConfigsServiceConnectionException", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
		}

		return "";
	}

	public String applyAllConfigs() {

		try {
			adminConfigManagementBeanLocal.refreshAllModuleConfigs();

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("processSucceeded")),
							null));

			fillTable();

		} catch (FailedToReadConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
//			e.printStackTrace();
		} catch (InvalidConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
//			e.printStackTrace();

		} catch (ConfigsServiceConnectionException e) {
			logger.error("ConfigsServiceConnectionException", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("connection_error")),
							null));

		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
		}
		return "";
	}

	// public boolean isEditFlag() {
	// return editFlag;
	// }
	//
	// public void setEditFlag(boolean editFlag) {
	// this.editFlag = editFlag;
	// }

	public ModuleConfigs getModuleConfig() {
		return moduleConfig;
	}

	public void setModuleConfig(ModuleConfigs moduleConfig) {
		this.moduleConfig = moduleConfig;
	}

	public ArrayList<ModuleConfigs> getModuleConfigs() {
		return moduleConfigs;
	}

	public void setModuleConfigs(ArrayList<ModuleConfigs> moduleConfigs) {
		this.moduleConfigs = moduleConfigs;
	}

}
