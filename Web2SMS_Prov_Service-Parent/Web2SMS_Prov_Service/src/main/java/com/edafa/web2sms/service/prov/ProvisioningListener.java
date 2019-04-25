package com.edafa.web2sms.service.prov;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanRemote;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.prov.interfaces.ProvisioningEventListener;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class ProvisioningListener
 */
@Stateless
public class ProvisioningListener implements ProvisioningEventListener {
	Logger provLogger = LogManager.getLogger(LoggersEnum.PROV.name());

	CampaignManagementBeanRemote campaignManagementBean;

	@Override
	@Asynchronous
	public void handleProvisioningEvent(ProvisioningEvent e) {
		TrxInfo trxInfo = new TrxInfo(TrxId.getTrxId());
		provLogger.info(trxInfo.logId() + "Handle new provisioning event: " + e.logEvent());
		switch (e.getNewStatus()) {
		case INACTIVE:
		case SUSPENDED:
			holdRunningCampaigns(trxInfo, e);
			break;
		default:
			break;
		}
		provLogger.info(trxInfo.logId() + "Provisioning event handled");
	}

        private void injectCampaignManagementBean(String trxLogId) throws NamingException{
            String providerUrl = (String) Configs.APP_PROVIDER_URL.getValue();
            String jndi = (String) Configs.CAMPAIGN_MANAGEMENT_BEAN_JNDI.getValue();
            provLogger.debug(trxLogId + "Lookup APP provider at providerUrl(" + providerUrl + ") with JNDI(" + jndi + ")");

            InitialContext ic = new InitialContext ();
            ic.addToEnvironment(javax.naming.Context.PROVIDER_URL, providerUrl);
            campaignManagementBean = (CampaignManagementBeanRemote) ic.lookup(jndi);
            
        }
	private void holdRunningCampaigns(TrxInfo trxInfo, ProvisioningEvent e) {
		provLogger.info(trxInfo.logId() + "Update action to hold running campaigns for account(" + e.getAccountId()
				+ ")");
		try {
//                    if (campaignManagementBean == null) {
                    injectCampaignManagementBean(trxInfo.logId());
//                    }
                    int updatedCount = campaignManagementBean.holdRunningCampaigns(trxInfo, e.getAccountId());
                    provLogger.info(trxInfo.logId() + "Updated action to hold running campaigns, count=" + updatedCount);
                } catch (DBException e1) {
			provLogger.error(trxInfo.logId() + "Failed to update action to hold running campaigns", e1);
		} catch (NamingException e1) {
			provLogger.error(trxInfo.logId() + "Failed to reach app to update action to hold running campaigns", e1);
		} catch (Exception e1) {
			provLogger.error(trxInfo.logId() + "Failed to update action to hold running campaigns", e1);
		}
	}

}
