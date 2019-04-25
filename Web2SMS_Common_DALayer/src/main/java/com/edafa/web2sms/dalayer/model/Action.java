package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.model.constants.ActionConst;

/**
 * The persistent class for the USER_ACTIONS database table.
 * 
 */
@Entity
@XmlRootElement
@ObjectTypeConverter(name = "UserActionNameConverter", dataType = String.class, objectType = ActionName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "CREATE_CAMPAIGN", objectValue = "CREATE_CAMPAIGN"),
		@ConversionValue(dataValue = "DELETE_CAMPAIGN", objectValue = "DELETE_CAMPAIGN"),
		@ConversionValue(dataValue = "EDIT_CAMPAIGN", objectValue = "EDIT_CAMPAIGN"),
		@ConversionValue(dataValue = "SEND_SMS", objectValue = "SEND_SMS"),
		@ConversionValue(dataValue = "UPDATE_CAMPAIGN_ACTION", objectValue = "UPDATE_CAMPAIGN_ACTION"),
		@ConversionValue(dataValue = "VIEW_ACTIVE_CAMPAINGS", objectValue = "VIEW_ACTIVE_CAMPAINGS"),
		@ConversionValue(dataValue = "VIEW_CAMPAIGNS_HISTORY", objectValue = "VIEW_CAMPAIGNS_HISTORY"),
		@ConversionValue(dataValue = "VIEW_REPORTS", objectValue = "VIEW_REPORTS"),
		@ConversionValue(dataValue = "GENERATE_DETAILED_REPORT", objectValue = "GENERATE_DETAILED_REPORT"),
		@ConversionValue(dataValue = "CREATE_LIST", objectValue = "CREATE_LIST"),
		@ConversionValue(dataValue = "DELETE_LIST", objectValue = "DELETE_LIST"),
		@ConversionValue(dataValue = "EDIT_LIST", objectValue = "EDIT_LIST"),
		@ConversionValue(dataValue = "REFRESH_LIST", objectValue = "REFRESH_LIST"),
		@ConversionValue(dataValue = "VIEW_LISTS", objectValue = "VIEW_LISTS"),
		@ConversionValue(dataValue = "VIEW_LIST_CONTACTS", objectValue = "VIEW_LIST_CONTACTS"),
		@ConversionValue(dataValue = "EXPORT_LIST_TO_FILE", objectValue = "EXPORT_LIST_TO_FILE"),
		@ConversionValue(dataValue = "CREATE_TEMPLATE", objectValue = "CREATE_TEMPLATE"),
		@ConversionValue(dataValue = "DELETE_TEMPLATE", objectValue = "DELETE_TEMPLATE"),
		@ConversionValue(dataValue = "EDIT_TEMPLATE", objectValue = "EDIT_TEMPLATE"),
		@ConversionValue(dataValue = "SEARCH_FOR_CONTCAT", objectValue = "SEARCH_FOR_CONTCAT"),
		@ConversionValue(dataValue = "EDIT_CONTCAT", objectValue = "EDIT_CONTCAT"),
		@ConversionValue(dataValue = "VIEW_TEMPLATES", objectValue = "VIEW_TEMPLATES"),
		@ConversionValue(dataValue = "VIEW_ADMIN_TEMPLATES", objectValue = "VIEW_ADMIN_TEMPLATES"),
		@ConversionValue(dataValue = "INQUIRY_SMS_ID", objectValue = "INQUIRY_SMS_ID"),
		@ConversionValue(dataValue = "INQUIRY_SMS_DATES", objectValue = "INQUIRY_SMS_DATES"),

		@ConversionValue(dataValue = "VIEW_GROUPS", objectValue = "VIEW_GROUPS"),
		@ConversionValue(dataValue = "VIEW_OWN_GROUP", objectValue = "VIEW_OWN_GROUP"),
		@ConversionValue(dataValue = "CREATE_GROUP", objectValue = "CREATE_GROUP"),
		@ConversionValue(dataValue = "EDIT_GROUP", objectValue = "EDIT_GROUP"),
		@ConversionValue(dataValue = "DELETE_GROUP", objectValue = "DELETE_GROUP"),
		@ConversionValue(dataValue = "VIEW_USERS", objectValue = "VIEW_USERS"),
		@ConversionValue(dataValue = "VIEW_OWN_GROUP_USERS", objectValue = "VIEW_OWN_GROUP_USERS"),
		@ConversionValue(dataValue = "VIEW_DEFAULT_GROUP_USERS", objectValue = "VIEW_DEFAULT_GROUP_USERS"),
		@ConversionValue(dataValue = "EDIT_USER_INFO", objectValue = "EDIT_USER_INFO"),
		@ConversionValue(dataValue = "EDIT_GROUP_USERS", objectValue = "ASSIGN_GROUP_USERS"),
		@ConversionValue(dataValue = "EDIT_GROUP_PRIVILEGES", objectValue = "EDIT_GROUP_PRIVILEGES"),
		@ConversionValue(dataValue = "MARK_GROUP_ADMIN", objectValue = "MARK_GROUP_ADMIN"),
		@ConversionValue(dataValue = "GET_CAMP_CREATE_NOTIFY", objectValue = "GET_CAMP_CREATE_NOTIFY"),
		@ConversionValue(dataValue = "APPROVE_CAMPAIGN", objectValue = "APPROVE_CAMPAIGN"),
		@ConversionValue(dataValue = "VIEW_PENDING_CAMPAIGNS", objectValue = "VIEW_PENDING_CAMPAIGNS"),
		@ConversionValue(dataValue = "REJECT_CAMPAIGN", objectValue = "REJECT_CAMPAIGN"),
		@ConversionValue(dataValue = "SEND_CAMPAIGN", objectValue = "SEND_CAMPAIGN")

})
@Table(name = "USER_ACTIONS")
@NamedQueries({ @NamedQuery(name = "Action.findAll", query = "SELECT u FROM Action u"),
		// @NamedQuery(name = "UserAction.findByAccountIdAndUserName", query = "SELECT userActions.a FROM (SELECT a FROM Action a join a.privileges ap join ap.accountGroups apg join apg.accountUsers apgu where apgu.username = :username and apgu.accountId = :accountId) userActions, (SELECT a FROM Action a join a.privileges ap join ap.accountStatusList aps join aps.accountList apsl where apsl.accountId = :accountId) accountStatusActions where userActions.actionId = accountStatusActions.actionId")
		// @NamedQuery(name = "UserAction.findByAccountIdAndUserName", query = "SELECT u FROM Action u")// a join a.privileges ap join ap.accountGroups apg join apg.accountUsers apgu where apgu.username = :username and apgu.accountId = :accountId) userActions, (SELECT a FROM Action a join a.privileges ap join ap.accountStatusList aps join aps.accountList apsl where apsl.accountId = :accountId) accountStatusActions where userActions.actionId = accountStatusActions.actionId")
})
@NamedNativeQueries({
		@NamedNativeQuery(name = "Action.findByAccountIdAndUserName", query = "SELECT DISTINCT user_act.ACTION_NAME FROM (select USER_ACTIONS.* from ACCOUNTS, ACCOUNT_USERS, ACCOUNT_GROUPS, ACCOUNT_GROUP_SYS_PRIVILEGES, ACCOUNT_GROUP_USERS, SYSTEM_PRIVILEGES, SYSTEM_PRIVILEGE_ACTIONS, USER_ACTIONS\n"
				+ "where ACCOUNTS.ACCOUNT_ID = ACCOUNT_USERS.ACCOUNT_ID and ACCOUNT_USERS.ACCOUNT_USER_ID = ACCOUNT_GROUP_USERS.ACCOUNT_USER_ID and ACCOUNT_GROUPS.ACCOUNT_GROUP_ID = ACCOUNT_GROUP_USERS.ACCOUNT_GROUP_ID and ACCOUNT_GROUPS.ACCOUNT_GROUP_ID = ACCOUNT_GROUP_SYS_PRIVILEGES.ACCOUNT_GROUP_ID and SYSTEM_PRIVILEGES.PRIVILEGE_ID =  ACCOUNT_GROUP_SYS_PRIVILEGES.PRIVILEGE_ID and SYSTEM_PRIVILEGES.PRIVILEGE_ID = SYSTEM_PRIVILEGE_ACTIONS.PRIVILEGE_ID and USER_ACTIONS.ACTION_ID = SYSTEM_PRIVILEGE_ACTIONS.ACTION_ID and ACCOUNT_USERS.STATUS_ID = ACCOUNTS.STATUS_ID\n"
				+ "AND ACCOUNTS.ACCOUNT_ID = ? and ACCOUNT_USERS.USERNAME = ?) user_act,\n"
				+ "(select USER_ACTIONS.* from ACCOUNTS, SYSTEM_PRIVILEGES, SYSTEM_PRIVILEGE_ACTIONS, USER_ACTIONS, ACCOUNT_STATUS, ACCOUNT_STATUS_SYS_PRIVILEGES\n"
				+ "where ACCOUNTS.STATUS_ID = ACCOUNT_STATUS.ACCOUNT_STATUS_ID and ACCOUNT_STATUS.ACCOUNT_STATUS_ID = ACCOUNT_STATUS_SYS_PRIVILEGES.ACCOUNT_STATUS_ID and ACCOUNT_STATUS_SYS_PRIVILEGES.PRIVILEGE_ID = SYSTEM_PRIVILEGES.PRIVILEGE_ID and SYSTEM_PRIVILEGES.PRIVILEGE_ID = SYSTEM_PRIVILEGE_ACTIONS.PRIVILEGE_ID and USER_ACTIONS.ACTION_ID = SYSTEM_PRIVILEGE_ACTIONS.ACTION_ID and\n"
				+ "ACCOUNTS.ACCOUNT_ID = ?) account_act where user_act.action_id = account_act.action_id") })

public class Action implements ActionConst, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ACTION_ID")
	private long actionId;

	@Column(name = "ACTION_NAME")
	@Convert("UserActionNameConverter")
	@Enumerated(EnumType.STRING)
	private ActionName actionName;

	// bi-directional many-to-one association to AccountStatusUserAction
	@OneToMany(mappedBy = "action")
	private List<AccountStatusUserAction> accountStatusUserAction;

	// bi-directional many-to-many association to AccountStatus
	@ManyToMany(mappedBy = "action")
	private List<AccountStatus> accountStatus;

	@JoinTable(name = "SYSTEM_PRIVILEGE_ACTIONS", joinColumns = {
			@JoinColumn(name = "ACTION_ID", referencedColumnName = "ACTION_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "PRIVILEGE_ID") })
	@ManyToMany
	private List<Privilege> privileges;

	public Action() {
	}

	public long getActionId() {
		return this.actionId;
	}

	public void setActionId(long actionId) {
		this.actionId = actionId;
	}

	public ActionName getActionName() {
		return this.actionName;
	}

	public void setActionName(ActionName actionName) {
		this.actionName = actionName;
	}

	public List<AccountStatusUserAction> getAccountStatusUserAction() {
		return this.accountStatusUserAction;
	}

	public void setAccountStatusUserAction(List<AccountStatusUserAction> accountStatusUserAction) {
		this.accountStatusUserAction = accountStatusUserAction;
	}

	public AccountStatusUserAction addAccountStatusUserAction(AccountStatusUserAction accountStatusUserAction) {
		getAccountStatusUserAction().add(accountStatusUserAction);
		accountStatusUserAction.setAction(this);

		return accountStatusUserAction;
	}

	public AccountStatusUserAction removeAccountStatusUserAction(AccountStatusUserAction accountStatusUserAction) {
		getAccountStatusUserAction().remove(accountStatusUserAction);
		accountStatusUserAction.setAction(null);

		return accountStatusUserAction;
	}

	public List<AccountStatus> getAccountStatus() {
		return this.accountStatus;
	}

	public void setAccountStatus(List<AccountStatus> accountStatus) {
		this.accountStatus = accountStatus;
	}

	@XmlTransient
	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

}