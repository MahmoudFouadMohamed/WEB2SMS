package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AppErrorEntity;
import com.edafa.web2sms.dalayer.model.Module;

public interface AppErrorDaoLocal extends Cachable<AppErrorEntity, String> {

	List<AppErrorEntity> getModuleAlarms(Module module);

}
