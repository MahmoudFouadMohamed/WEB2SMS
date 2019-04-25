package com.edafa.web2sms.utils.security;

import javax.swing.JOptionPane;

public class HashedPassGen {
	
	public static void main(String []args)
	{
		String password =JOptionPane.showInputDialog("Enter password you want to hash.");
		HashedUtils hu = new HashedUtils();
		JOptionPane.showInputDialog( "test", hu.hashWord(password));
//		(null, "your hashed password is: \n"+hu.hashWord(password));
		
	}

}
