package com.dronet.exceptions;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class UtilityException extends Exception {

	private static final long serialVersionUID = 1924421591112595763L;
	
	public UtilityException(String arg0) { super(arg0); }
	public UtilityException(Throwable arg0) { super(arg0); }
	public UtilityException(String arg0, Throwable arg1) { super(arg0, arg1); }
	public UtilityException(String arg0, Throwable arg1, boolean arg2, boolean arg3) { super(arg0, arg1, arg2, arg3); }

}
