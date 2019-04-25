package com.edafa.web2sms.service.api.sms.utils.interfaces;

import javax.ejb.Local;

@Local
public interface SecureKeyLocal {

	public String generateSecureKey();
}
