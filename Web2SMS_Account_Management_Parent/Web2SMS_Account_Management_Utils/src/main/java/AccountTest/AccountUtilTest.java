/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccountTest;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author mahmoud
 */
@Stateless
@LocalBean
public class AccountUtilTest {

    public String getTestWard() {
        return "Test account from utils done .....";
    }
    
    
}
