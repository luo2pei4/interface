package com.lp.common.exception;

public class ValidateException extends Exception {

    private static final long serialVersionUID = 1L;
	
	private String businessMessage;

    public ValidateException() {
        super();
    }

    public ValidateException(Exception exception) {
        super(exception);
    }

    public ValidateException(String businessMessage, Exception exception) {
        super(exception);
        this.businessMessage = businessMessage;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }
}
