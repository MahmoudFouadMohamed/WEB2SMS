package com.edafa.web2sms.service.list;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.ContactListDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.service.list.interfaces.ListHandlerBeanLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class ListHandlerBean
 */
@Stateless
public class ListHandlerBean implements ListHandlerBeanLocal {

	Logger listLogger = LogManager.getLogger(LoggersEnum.LIST_MNGMT.name());

	@EJB
	private ContactListDaoLocal contactListDao;
	@EJB
	private ListTypeDaoLocal listTypeDao;

	/**
	 * Default constructor.
	 */
	public ListHandlerBean() {
		// TODO Auto-generated constructor stub
	}

	@Asynchronous
	public void handleAsyncoResult(List<Future<Integer>> asyncoResultList, Integer listId) {
		int listCount = 0;

		try {
			ContactList list = contactListDao.findByListId(listId);
			if (list != null) {
				listLogger.debug("Found " + list);
				ListTypeName type = list.getListType().getListTypeName();

				switch (type) {
				case PRECUSTOMIZED_LIST:
				case PRENORMAL_LIST: {
					listLogger.info("List(" + listId + "). will be converted to "+type +" list.");

					for (Future<Integer> future : asyncoResultList) {
						listCount += future.get();
					}
					if (listCount > 0) {
						if (type.equals(ListTypeName.PRECUSTOMIZED_LIST))
							list.setListType(listTypeDao.getCachedObjectByName(ListTypeName.CUSTOMIZED_LIST));
						else
							list.setListType(listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST));
						list.setContactsCount((long) listCount);
						contactListDao.edit(list);
						listLogger.info("List(" + listId + "). creation completed successfully, the list contain "
								+ listCount + " contact(s).");
					} else {
						listLogger.info("List(" + listId + "). Failed to persist contacts, Deleting the list.");
						contactListDao.removeByListId(listId);
					}

					break;
				}
				case NORMAL_LIST: {
					listLogger.info("List(" + listId + ") will be deleted.");
					if (asyncoResultList != null && !asyncoResultList.isEmpty())
						asyncoResultList.get(0).get();
					listLogger.info("List(" + listId + ") deleted.");

					break;
				}
				case INTRA_LIST:
				case TEMP_LIST:
				case UNKNOWN:
				case VIRTUAL_LIST:
				default:
					break;

				}
			} else {
				listLogger.warn("There is no list with id(" + listId + ").");
			}

		} catch (InterruptedException e) {
			listLogger.error("InterruptedException", e);
			try {
				contactListDao.removeByListId(listId);
			} catch (DBException e1) {
				listLogger.error("DB Exception", e);

			}

		} catch (ExecutionException e) {
			listLogger.error("ExecutionException", e);
			try {
				contactListDao.removeByListId(listId);
			} catch (DBException e1) {
				listLogger.error("DB Exception", e);

			}

		} catch (DBException e) {
			listLogger.error("DB Exception", e);
			try {
				contactListDao.removeByListId(listId);
			} catch (DBException e1) {
				listLogger.error("DB Exception", e);

			}

		}

	}

}
