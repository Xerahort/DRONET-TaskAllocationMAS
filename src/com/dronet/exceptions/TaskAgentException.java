package com.dronet.exceptions;

public class TaskAgentException extends Exception {

	private static final long serialVersionUID = 6861352426493452758L;

	public TaskAgentException(String message) { super(message); }
	public TaskAgentException(Throwable cause) { super(cause); }
	public TaskAgentException(String message, Throwable cause) { super(message, cause); }
	public TaskAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) { super(message, cause, enableSuppression, writableStackTrace); }


}
