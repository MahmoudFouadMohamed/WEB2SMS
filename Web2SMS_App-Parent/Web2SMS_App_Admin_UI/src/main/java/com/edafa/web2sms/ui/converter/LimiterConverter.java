package com.edafa.web2sms.ui.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.edafa.web2sms.dalayer.model.SendingRateLimiter;


@FacesConverter(value = "com.edafa.web2sms.ui.converter.LimiterConverter")
public class LimiterConverter implements Converter
{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
		
		/*
		 * SendingRateLimiter{limiterId=1, maxPermits=101, description=banking, smsapiEnabled=false, campEnabled=true, accounts=}
		 */
		 
//		System.out.println("Converter Value : " + value);
		String[] parts = value.split("=");

        String part1 = parts[1];
        String part2 = parts[2];
        String part3 = parts[3];
        String part4 = parts[4];
        String part5 = parts[5];
//        String part6 = parts[6];

//System.out.println(part1);
//System.out.println(part2);
//System.out.println(part3);
//System.out.println(part4);
//System.out.println(part5);
//System.out.println(part6);

        String[] parts1 = part1.split(",");
        String[] parts2 = part2.split(",");
         String[] parts3 = part3.split(",");
         String[] parts4 = part4.split(",");
         String [] parts5= part5.split(",");
//         String [] parts6= part5.split(",");

        String limiterID = parts1[0];
        String maxPermits = parts2[0];
        String description = parts3[0];
        String smsapiEnabled = parts4[0];
        String campEnabled = parts5[0];
//        
//        System.out.println(limiterID);
//        System.out.println(maxPermits);
//        System.out.println(description);
//        System.out.println(smsapiEnabled);
//        System.out.println(campEnabled);
//System.out.println("b"+parts6[0]);
//System.out.println("c "+parts6[1]);

        SendingRateLimiter limiter = new SendingRateLimiter();
        limiter.setLimiterId(limiterID);
        limiter.setDescription(description);
        limiter.setMaxPermits(Integer.valueOf(maxPermits));
        limiter.setCampEnabled(Boolean.valueOf(campEnabled));
        limiter.setSmsapiEnabled(Boolean.valueOf(smsapiEnabled));
        
//        System.out.println("return : " + limiter);
        return limiter;
        
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		return value.toString();
	}

}
