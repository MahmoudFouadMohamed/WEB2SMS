package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListContactPK;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactListModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.sms.SMSUtils;

@Stateless
public class ListConversionBean {

	/**
	 * Converts from {@code ContactModel} to {@code Contact} entity class.
	 * 
	 * @param contact
	 *            the object of {@code ContactModel}
	 * @return newContact the converted object of {@code Contact}
	 */
	public Contact getContact(Integer listId, ContactModel contact) {

		int maxChar = (int) Configs.MAX_CONTACT_NAME_CHAR.getValue();
		Contact newContact = new Contact();
		boolean validContact = false;
		if (contact.getMsisdn() != null) {
			if (SMSUtils.validateLocalAddress(contact.getMsisdn())) {
				newContact.setListContactsPK(new ListContactPK(listId, contact.getMsisdn()));
				validContact = true;
			} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
					&& SMSUtils.validateInternationalAddress(contact.getMsisdn())) {
				newContact.setListContactsPK(new ListContactPK(listId, contact.getMsisdn()));
				validContact = true;
			}
			newContact.setFirstName(contact.getFirstName());
			newContact.setLastName(contact.getLastName());
			newContact.setValue1(contact.getValue1());
			newContact.setValue2(contact.getValue2());
			newContact.setValue3(contact.getValue3());
			newContact.setValue4(contact.getValue4());
			newContact.setValue5(contact.getValue5());

			// Validating that name will not exceed size conistrain in the DB.
			if (contact.getFirstName() != null && !contact.getFirstName().isEmpty()
					&& contact.getFirstName().length() > maxChar) {
				contact.setFirstName(contact.getFirstName().substring(0, maxChar));
			}
			if (contact.getLastName() != null && !contact.getLastName().isEmpty()
					&& contact.getLastName().length() > maxChar) {
				contact.setLastName(contact.getLastName().substring(0, maxChar));
			}
			if (contact.getValue1() != null && !contact.getValue1().isEmpty()
					&& contact.getValue1().length() > maxChar) {
				contact.setValue1(contact.getValue1().substring(0, maxChar));
			}
			if (contact.getValue2() != null && !contact.getValue2().isEmpty()
					&& contact.getValue2().length() > maxChar) {
				contact.setValue2(contact.getValue2().substring(0, maxChar));
			}
			if (contact.getValue3() != null && !contact.getValue3().isEmpty()
					&& contact.getValue3().length() > maxChar) {
				contact.setValue3(contact.getValue3().substring(0, maxChar));
			}
			if (contact.getValue4() != null && !contact.getValue4().isEmpty()
					&& contact.getValue4().length() > maxChar) {
				contact.setValue4(contact.getValue4().substring(0, maxChar));
			}
			if (contact.getValue5() != null && !contact.getValue5().isEmpty()
					&& contact.getValue5().length() > maxChar) {
				contact.setValue5(contact.getValue5().substring(0, maxChar));
			}

			if (validContact)
				return newContact;
		}
		return null;
	}

	public ContactModel getContactModel(Contact contact) {
		ContactModel newContact = new ContactModel();
		newContact.setFirstName(contact.getFirstName());
		newContact.setLastName(contact.getLastName());
		newContact.setMsisdn(contact.getListContactsPK().getMsisdn());
		newContact.setValue1(contact.getValue1());
		newContact.setValue2(contact.getValue2());
		newContact.setValue3(contact.getValue3());
		newContact.setValue4(contact.getValue4());
		newContact.setValue5(contact.getValue5());
		return newContact;
	}

	public ContactList getContactList(ContactListModel contactListModel) {
		ContactList newList = new ContactList();
		ContactListInfoModel listInfo = contactListModel.getListInfo();

		newList.setListId(listInfo.getListId());
		newList.setListName(listInfo.getListName());
		newList.setDescription(listInfo.getDescription());

		List<ContactModel> contacts = contactListModel.getListContacts();
		if (contacts != null && !contacts.isEmpty()) {
			List<Contact> newContacts = new ArrayList<Contact>();
			newList.setListContacts(newContacts);
			for (Iterator<ContactModel> it = contacts.iterator(); it.hasNext();) {
				ContactModel contactModel = it.next();
				Contact newContact = getContact(listInfo.getListId(), contactModel);
				if (newContact != null)
					newContacts.add(newContact);
			}
		}
		return newList;
	}

	public ContactListModel getContactListModel(ContactList contactList) {
		ContactListModel newList = new ContactListModel();
		ContactListInfoModel listInfo = getContactListInfoModel(contactList);
		newList.setListInfo(listInfo);

		List<Contact> contacts = contactList.getListContacts();
		if (contacts != null) {
			newList.setListContacts(getContactsModel(contacts));
		}
		return newList;
	}

	public List<Contact> getContacts(Integer listId, List<ContactModel> contacts) {
		List<Contact> copyContacts = new ArrayList<Contact>();
		if (contacts != null && !contacts.isEmpty()) {
			for (Iterator<ContactModel> it = contacts.iterator(); it.hasNext();) {
				ContactModel contactModel = it.next();
				Contact newContact = getContact(listId, contactModel);

				if (newContact != null) {
					newContact.getListContactsPK().setListId(listId);
					copyContacts.add(newContact);
				}
			}
		}
		return copyContacts;
	}

	public List<ContactModel> getContactsModel(List<Contact> contacts) {
		if (contacts == null)
			throw new NullPointerException("Contacts list is null");
		List<ContactModel> copyContacts = new ArrayList<ContactModel>(contacts.size());
		for (Contact contact : contacts) {
			copyContacts.add(getContactModel(contact));
		}
		return copyContacts;
	}

	public List<ContactList> getContactLists(List<Integer> lists) {
		List<ContactList> newLists = new ArrayList<ContactList>(lists.size());
		for (Integer listId : lists) {
			newLists.add(new ContactList(listId));
		}
		return newLists;
	}

	public List<ContactListModel> getContactListsModel(List<ContactList> lists) {
		List<ContactListModel> copyLists = new ArrayList<ContactListModel>(lists.size());

		for (ContactList contactList : lists) {
			ContactListModel list = getContactListModel(contactList);
			copyLists.add(list);
		}
		return copyLists;
	}

	public ContactListInfoModel getContactListInfoModel(ContactList contactList) {
		ContactListInfoModel listInfo = new ContactListInfoModel();
		listInfo.setListId(contactList.getListId());
		listInfo.setListName(contactList.getListName());
		listInfo.setDescription(listInfo.getDescription());
		listInfo.setListType(contactList.getListType().getListTypeName());
		listInfo.setContactsCount(contactList.getContactsCount().intValue());
		return listInfo;
	}

	public List<ContactListInfoModel> getContactListsInfoModel(List<ContactList> lists) {
		List<ContactListInfoModel> newLists = new ArrayList<ContactListInfoModel>(lists.size());

		for (ContactList contactList : lists) {

			ContactListInfoModel list = getContactListInfoModel(contactList);
			newLists.add(list);

		}
		return newLists;
	}
}
