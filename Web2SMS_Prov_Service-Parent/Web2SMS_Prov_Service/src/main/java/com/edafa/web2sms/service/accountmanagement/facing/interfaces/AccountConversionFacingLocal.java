/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.dalayer.model.Tier;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountConversionFacingLocal {
    public Tier getTier(TierModel tierModel);
    public TierModel getTierModel(Tier tier);
}
