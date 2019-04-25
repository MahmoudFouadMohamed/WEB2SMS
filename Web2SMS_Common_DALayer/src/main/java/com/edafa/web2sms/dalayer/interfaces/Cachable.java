package com.edafa.web2sms.dalayer.interfaces;

import com.edafa.web2sms.dalayer.exception.DBException;

public interface Cachable<T, E> {
	void refreshCachedValues() throws DBException;

	T getCachedObjectById(Object id);

	T getCachedObjectByName(E name);
}
