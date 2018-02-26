package com.company.transfer.service.exception;

public class TransferServiceException extends RuntimeException {

	private static final long serialVersionUID = 1668584501354958168L;

	public TransferServiceException() {
		super();
	}

	public TransferServiceException(String message) {
		super(message);
	}

	public TransferServiceException(Throwable cause) {
		super(cause);
	}

	public TransferServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransferServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
