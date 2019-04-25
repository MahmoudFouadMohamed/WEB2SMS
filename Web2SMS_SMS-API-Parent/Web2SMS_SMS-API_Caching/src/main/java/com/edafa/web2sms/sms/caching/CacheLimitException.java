package com.edafa.web2sms.sms.caching;

public class CacheLimitException extends Exception {

    private static final long serialVersionUID = 5937122150910129042L;

    public CacheLimitException() {
        super();
    }

    public CacheLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLimitException(String message) {
        super(message);
    }

    public CacheLimitException(Throwable cause) {
        super(cause);
    }

}
