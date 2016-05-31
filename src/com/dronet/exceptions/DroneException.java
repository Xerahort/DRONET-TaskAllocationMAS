package com.dronet.exceptions;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class DroneException extends Exception {

	private static final long serialVersionUID = -8929197146676178546L;

	public DroneException(String message) { super(message); }
	public DroneException(Throwable cause) { super(cause); }
	public DroneException(String message, Throwable cause) { super(message, cause); }
	public DroneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) { super(message, cause, enableSuppression, writableStackTrace); }

}
