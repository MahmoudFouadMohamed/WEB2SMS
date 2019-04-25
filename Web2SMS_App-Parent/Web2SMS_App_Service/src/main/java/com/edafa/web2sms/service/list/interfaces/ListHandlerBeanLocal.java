package com.edafa.web2sms.service.list.interfaces;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Local;

@Local
public interface ListHandlerBeanLocal {
	
	public void handleAsyncoResult(List<Future<Integer>> asyncoResultList, Integer listId);

}
