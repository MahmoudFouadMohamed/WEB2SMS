package com.edafa.web2sms.acc_manag.service.conversoin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountUser;

/**
 * Session Bean implementation class UserConversionBean
 */
@Stateless
@LocalBean
public class UserConversionBean {

	/**
	 * Default constructor. 
	 */
	public UserConversionBean() {
		// zero argument constructor
	}

	public AccountUser updateAccountUser(UserDetailsModel userModel, AccountUser accountUser) {
                updateAccountUserBasicInfo(userModel, accountUser);
                updateAccountUsergroups(userModel, accountUser);
		return accountUser;
	}

    public AccountUser updateAccountUserBasicInfo(UserDetailsModel userModel, AccountUser accountUser) {
        if (userModel.getEmail() != null) {
            accountUser.setEmail(userModel.getEmail());
        }
        if (userModel.getName() != null) {
            accountUser.setName(userModel.getName());
        }
        if (userModel.getPhoneNumber() != null) {
            accountUser.setPhoneNumber(userModel.getPhoneNumber());
        }
        return accountUser;
    }

        public AccountUser updateAccountUsergroups(UserDetailsModel userModel, AccountUser accountUser) {
            String groupId = userModel.getGroupId();

            if (!userModel.getGroupId().equals(accountUser.getVisibleAccountGroups().get(0).getAccountGroupId())) {
                List<AccountGroup> acctGroups = new ArrayList<>();
                AccountGroup ag = new AccountGroup(groupId);
                acctGroups.add(ag);
                accountUser.setAccountGroups(acctGroups);
            }
            return accountUser;
	}

        
	private boolean isExist(String str) {
		return str != null && !str.isEmpty();
	}

	public List<UserDetailsModel> getUsersModel(List<AccountUser> accountUsers) {

		List<UserDetailsModel> usersModels = new ArrayList<UserDetailsModel>();
		for (Iterator<AccountUser> iterator = accountUsers.iterator(); iterator.hasNext();) {
			AccountUser accountUser = (AccountUser) iterator.next();
			UserDetailsModel userModel = getUserModel(accountUser);
			usersModels.add(userModel);
		}

		return usersModels;
	}

	private UserDetailsModel getUserModel(AccountUser accountUser) {
		UserDetailsModel userModel = new UserDetailsModel();

		userModel.setUserId(accountUser.getAccountUserId());
		userModel.setUsername(accountUser.getUsername());
		userModel.setEmail(accountUser.getEmail());
		userModel.setName(accountUser.getName());
		userModel.setPhoneNumber(accountUser.getPhoneNumber());

		List<AccountGroup> groups = accountUser.getVisibleAccountGroups();

		if (groups != null && !groups.isEmpty()) {
			userModel.setGroupId(groups.get(0).getAccountGroupId());
                        userModel.setGroupName(groups.get(0).getGroupName());
		}

		return userModel;
	}

}
