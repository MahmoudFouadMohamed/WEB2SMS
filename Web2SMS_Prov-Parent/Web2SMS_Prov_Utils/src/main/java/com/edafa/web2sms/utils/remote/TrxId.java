package com.edafa.web2sms.utils.remote;

import com.edafa.utils.IdGenerator;

public class TrxId {

    private static final String PREFIX = "PROV";

    public static String getTrxId() {
        return getTrxId(PREFIX);
    }

    public static String getTrxId(String prefix) {
        return IdGenerator.generateId(prefix);
    }
}