package com.lp.common.exception;

public class InternalConnectException extends Exception {

    private static final long serialVersionUID = 1L;
	
	private String businessMessage;

    public InternalConnectException() {
        super();
    }

    public InternalConnectException(Exception exception) {
        super(exception);
    }

    public InternalConnectException(String businessMessage, Exception exception) {
        super(exception);
        this.businessMessage = businessMessage;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }
}
