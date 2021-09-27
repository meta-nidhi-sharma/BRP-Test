package com.project;

public class BRPException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status = 0;

    public BRPException(String error, int status) {
        super(error);
        this.status = status;
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public int getStatus() {
        return this.status;
    }
}