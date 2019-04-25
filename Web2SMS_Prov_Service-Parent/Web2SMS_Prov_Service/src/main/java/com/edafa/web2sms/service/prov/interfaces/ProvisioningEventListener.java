package com.edafa.web2sms.service.prov.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.service.prov.ProvisioningEvent;

@Local
public interface ProvisioningEventListener {
	void handleProvisioningEvent(ProvisioningEvent e);
}
