package com.dronet.exceptions;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class DroneAgentException extends Exception {

	private static final long serialVersionUID = 8119650698239037805L;
	
	public DroneAgentException(String message) { super(message); }
	public DroneAgentException(Throwable cause) { super(cause); }
	public DroneAgentException(String message, Throwable cause) { super(message, cause); }
	public DroneAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) { super(message, cause, enableSuppression, writableStackTrace); }
}
