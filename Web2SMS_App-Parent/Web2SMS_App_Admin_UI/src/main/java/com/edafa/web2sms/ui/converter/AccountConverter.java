package com.edafa.web2sms.ui.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.Tier;


@FacesConverter(value = "com.edafa.web2sms.ui.converter.AccountConverter")
public class AccountConverter implements Converter
{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
//		System.out.println("Converter Value : " + value);
		String[] parts = value.split("=");

        String part1 = parts[1];
        String part2 = parts[2];
        String part3 = parts[3];
        String part4 = parts[4];
        String part5 = parts[5];
        

        String[] parts1 = part1.split(",");
        String[] parts2 = part2.split(",");
         String[] parts3 = part3.split(",");
         String[] parts4 = part4.split(",");
         String [] parts5= part5.split(",");
         
        String accountId = parts1[0];
        String status = parts2[0].substring(1);
        String companyName = parts3[0];
        String billingMsisdn = parts4[0];
        String tierId = parts5[0].substring(0, 1);
        
        Account account = new Account();
        AccountStatus accStatus = new AccountStatus(Integer.valueOf(status));
        Tier tier = new Tier (Integer.valueOf(tierId));
        account.setAccountId(accountId);
        account.setBillingMsisdn(billingMsisdn);
        account.setCompanyName(companyName);
        account.setStatus(accStatus);
        account.setTier(tier);
        
        return account;
        
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		return value.toString();
	}

}
