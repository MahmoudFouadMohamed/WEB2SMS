package com.edafa.web2sms.ui.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;


@FacesConverter(value = "CampaignStatusConv")
public class CampaignStatusConv implements Converter
{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
		CampaignStatusName campaignStatusName = CampaignStatusName.valueOf(value.trim());
		return campaignStatusName;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		if (!(value instanceof CampaignStatusName) || ((CampaignStatusName) value) == null)
		{
			return value.toString();
		}

		return ((CampaignStatusName) value).getStatusName();
	}

}
