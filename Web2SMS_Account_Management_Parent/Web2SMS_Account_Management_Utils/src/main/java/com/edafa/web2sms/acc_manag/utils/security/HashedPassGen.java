package com.edafa.web2sms.acc_manag.utils.security;

import javax.swing.JOptionPane;

public class HashedPassGen {
	
	public static void main(String []args)
	{
		String password =JOptionPane.showInputDialog("Enter password you want to hash.");
		HashUtils hu = new HashUtils();
		JOptionPane.showInputDialog( "test", hu.hashWord(password));
//		(null, "your hashed password is: \n"+hu.hashWord(password));
		
	}

}
