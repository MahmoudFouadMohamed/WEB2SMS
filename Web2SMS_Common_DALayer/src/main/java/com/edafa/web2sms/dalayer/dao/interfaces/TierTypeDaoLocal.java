package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.TierType;

@Local
public interface TierTypeDaoLocal extends Cachable<TierType,TierTypesEnum>{

//	TierType findById(Integer id);

	List<TierTypesEnum> getCachedList();

}
