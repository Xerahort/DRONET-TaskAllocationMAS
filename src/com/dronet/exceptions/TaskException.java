package com.dronet.exceptions;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class TaskException extends Exception {

	private static final long serialVersionUID = 1883336132967759094L;

	public TaskException(String arg0) { super(arg0); }
	public TaskException(Throwable arg0) { super(arg0); }
	public TaskException(String arg0, Throwable arg1) { super(arg0, arg1); }
	public TaskException(String arg0, Throwable arg1, boolean arg2, boolean arg3) { super(arg0, arg1, arg2, arg3); }

}
