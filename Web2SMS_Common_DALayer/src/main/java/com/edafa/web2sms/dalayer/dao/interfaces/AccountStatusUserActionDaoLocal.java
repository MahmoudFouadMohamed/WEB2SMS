package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AccountStatusUserAction;

public interface AccountStatusUserActionDaoLocal extends Cachable<List<ActionName>, AccountStatusName> {
	
	List<AccountStatusUserAction> findAll() throws DBException;

}
