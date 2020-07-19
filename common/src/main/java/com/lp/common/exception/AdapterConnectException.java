package com.lp.common.exception;

public class AdapterConnectException extends Exception {

    private static final long serialVersionUID = 1L;
	
	private String businessMessage;

    public AdapterConnectException() {
        super();
    }

    public AdapterConnectException(Exception exception) {
        super(exception);
    }

    public AdapterConnectException(String businessMessage, Exception exception) {
        super(exception);
        this.businessMessage = businessMessage;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }

}
