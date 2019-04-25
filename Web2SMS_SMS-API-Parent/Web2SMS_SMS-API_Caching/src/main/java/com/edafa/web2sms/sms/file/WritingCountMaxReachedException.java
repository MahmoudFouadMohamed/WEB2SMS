package com.edafa.web2sms.sms.file;

public class WritingCountMaxReachedException extends Exception {

    private static final long serialVersionUID = -7362142215879530901L;

    public WritingCountMaxReachedException() {
        super();
    }

    public WritingCountMaxReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WritingCountMaxReachedException(String message) {
        super(message);
    }

    public WritingCountMaxReachedException(Throwable cause) {
        super(cause);
    }

}
